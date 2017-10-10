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

package com.consol.citrus.admin.configuration;

import java.lang.annotation.*;
import java.util.function.Supplier;

/**
 * @author Christoph Deppisch
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface SystemProperty {

    String name() default "";

    String environment() default "";

    String defaultValue() default "";

    Class<? extends Supplier<String>> defaultValueSupplier() default NullValueSupplier.class;

    /**
     * Default value supplier returning null value.
     */
    class NullValueSupplier implements Supplier<String> {
        @Override
        public String get() {
            return null;
        }
    }
}
