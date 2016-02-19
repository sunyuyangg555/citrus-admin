/*
 * Copyright 2006-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.consol.citrus.admin.converter.action;

import com.consol.citrus.actions.SendMessageAction;
import com.consol.citrus.admin.model.TestAction;
import com.consol.citrus.model.testcase.core.ObjectFactory;
import com.consol.citrus.model.testcase.core.SendDefinition;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class SendMessageActionConverter extends AbstractTestActionConverter<SendDefinition, SendMessageAction> {

    /**
     * Default constructor using action type reference.
     */
    public SendMessageActionConverter() {
        super("send");
    }

    @Override
    public TestAction convert(SendDefinition definition) {
        TestAction action = new TestAction(getActionType(), getModelClass());

        action.add(property("endoint", definition));
        action.add(property("actor", "TestActor", definition));
        action.add(property("fork", definition, "false")
                .options("true", "false"));

        return action;
    }

    @Override
    public SendDefinition convertModel(SendMessageAction definition) {
        SendDefinition action = new ObjectFactory().createSendDefinition();

        if (definition.getActor() != null) {
            action.setActor(definition.getActor().getName());
        } else if (definition.getEndpoint().getActor() != null) {
            action.setActor(definition.getEndpoint().getActor().getName());
        }

        action.setDescription(definition.getDescription());
        action.setEndpoint(definition.getEndpoint().getName());

        return action;
    }

    @Override
    public Class<SendDefinition> getModelClass() {
        return SendDefinition.class;
    }
}