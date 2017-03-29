import {Component, Input, OnInit} from '@angular/core';
import {TestDetail, TestResult} from "../../../../model/tests";
import {TestService} from "../../../../service/test.service";
import {SocketEvent} from "../../../../model/socket.event";
import {Message} from "../../../../model/message";
import {Alert} from "../../../../model/alert";
import {AlertService} from "../../../../service/alert.service";
import * as _ from 'lodash';
import * as moment from 'moment';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {Frame} from "stompjs";
import {TestStateService} from "../../test.state";
import {Observable} from "rxjs";

@Component({
    selector: 'test-run-outlet',
    template: '<test-run [detail]="detail|async"></test-run>'
})
export class TestRunOutlet implements OnInit{
    detail:Observable<TestDetail>;
    constructor(private testState:TestStateService) {}
    ngOnInit() {
        this.detail = this.testState.selectedTestDetail;
    }
}

@Component({
    selector: "test-run",
    templateUrl: 'test-run.html'
})
export class TestRunComponent {
    @Input() detail: TestDetail;

    constructor(private _testService: TestService,
                private _alertService: AlertService) {
        this.stompClient = Stomp.over(new SockJS(`/api/logging`) as WebSocket);
        this.stompClient.connect({}, (frame:Frame) => {
            if (frame) {
                this.subscribe();
            }
        });
    }

    result: TestResult;
    running = false;
    completed = 0;
    failed = false;
    stompClient: any;

    finishedActions = 0;

    processOutput = "";
    currentOutput = "";

    messages: Message[];

    execute() {
        this.processOutput = "";
        this.currentOutput = "";
        this.running = true;
        this.failed = false;
        this.completed = 0;
        this.finishedActions = 0;
        this.messages = [];
        this._testService.execute(this.detail)
            .subscribe(
                result => {
                    this.result = result;
                },
                error => this.notifyError(<any>error));
    }

    subscribe() {
        if (this.stompClient) {
            this.stompClient.subscribe('/topic/log-output', (output:Stomp.Message)=> {
                var event: SocketEvent = JSON.parse(output.body);
                jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
                this.processOutput += event.msg;
                this.currentOutput = event.msg;
                this.handle(event);
            });
            this.stompClient.subscribe('/topic/test-events', (output:Stomp.Message) => {
                var event: SocketEvent = JSON.parse(output.body);
                this.handle(event);
            });
            this.stompClient.subscribe('/topic/messages', (output:Stomp.Message) => {
                var message = JSON.parse(output.body);
                this.handleMessage(message);
            });
        }
    }

    openConsole() {
        (jQuery('#dialog-console') as any).modal();
    }

    handleMessage(message:any) {
        this.messages.push(new Message(_.uniqueId(), message.type, message.msg, moment().toISOString()));
    }

    handle(event: SocketEvent) {
        if ("PROCESS_START" == event.type) {
            this.completed = 1;
        } else if ("TEST_START" == event.type) {
            this.completed = 1;
        } else if ("TEST_ACTION_FINISH" == event.type) {
            this.finishedActions++;

            if (this.detail.actions.length) {
                this.completed = Math.round((this.finishedActions / this.detail.actions.length) * 100);
            } else if (this.completed < 90) {
                this.completed += 2;
            }
        } else if ("TEST_FAILED" == event.type || "PROCESS_FAILED" == event.type) {
            this.failed = true;
        } else {
            if (this.completed < 11) {
                this.completed++;
            }
        }

        if ("PROCESS_FAILED" == event.type || "PROCESS_SUCCESS" == event.type) {
            this.completed = 100;
            this.running = false;
            this.currentOutput = this.processOutput;
            jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
        }
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}