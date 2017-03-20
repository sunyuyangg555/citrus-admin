import {NgModule} from "@angular/core";
import {StoreModule} from "@ngrx/store";
import {StoreDevtoolsModule} from "@ngrx/store-devtools";
import {RouterStoreModule, RouterState} from "@ngrx/router-store";
import {routerReducer as router} from '@ngrx/router-store'
import {reduce as tests, TestState} from './components/test/test.state'
import {reduce as configuration} from './components/configuration/configuration.state'
import {ConfigurationState} from "./components/configuration/configuration.state";

export interface AppState {
    router:RouterState,
    tests:TestState,
    configuration:ConfigurationState
}

@NgModule({
    imports: [
        StoreModule.provideStore({
            router,
            tests,
            configuration
        }),
        RouterStoreModule.connectRouter(),
        StoreDevtoolsModule.instrumentOnlyWithExtension({})
    ]
})
export class StateModule {}