/* 
 * JINAH Project - Java Is Not A Hammer
 * http://obadaro.com/jinah
 *
 * Copyright 2010-2012 Roberto Badaro 
 * and individual contributors by the @authors tag.
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
package com.obadaro.jinah.entity.annotation.util;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.TemporalType;

/**
 * Defines a where clause fragment.
 * 
 * @author Roberto Badaro
 * @since 1.1
 */
@Documented
@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface WhereClauseFragment {

    /**
     * The field name where to obtain the parameter value. This name must be used as parameter name
     * on fragment.
     */
    String name();

    /**
     * Where clause fragment using the annotated value. Example: {@code "obj.id = :id"}
     */
    String fragment();

    /**
     * Where clause fragment when the annotated value is null and must be used in where clause.
     * Example: {@code "obj.id is null"}
     */
    String ifNull() default "";

    /**
     * Treat empty or blank string as null value? Default: true.
     */
    boolean blankStringAsNull() default true;

    /**
     * Temportal type for annotated value if it is a {@link java.util.Date}. Default: DATE.
     */
    TemporalType temporalType() default TemporalType.DATE;

}
