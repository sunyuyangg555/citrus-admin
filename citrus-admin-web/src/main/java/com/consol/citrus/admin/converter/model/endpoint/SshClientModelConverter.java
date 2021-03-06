/*
 * Copyright 2006-2017 the original author or authors.
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

package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.model.config.ssh.SshClientModel;
import com.consol.citrus.ssh.client.SshClient;
import com.consol.citrus.ssh.client.SshEndpointConfiguration;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class SshClientModelConverter extends AbstractEndpointModelConverter<SshClientModel, SshClient, SshEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public SshClientModelConverter() {
        super(SshClientModel.class, SshClient.class, SshEndpointConfiguration.class);

        addDecorator(new AbstractModelConverter.MethodCallDecorator("port") {
            @Override
            public Object decorateArgument(Object arg) {
                return Integer.valueOf(arg.toString());
            }
        });
    }

    @Override
    public SshClientModel convert(String id, SshClient model) {
        SshClientModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(SshClientModel model) {
        return getJavaConfig(model, model.getId(), "ssh().client()");
    }
}
