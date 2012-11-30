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
package com.obadaro.jinah.entity.util.query;

import javax.persistence.TemporalType;

import com.obadaro.jinah.entity.annotation.util.WhereClauseFragment;

/**
 * Represents a where fragment configured by {@link WhereClauseFragment}.
 * 
 * @author Roberto Badaro
 * @since 1.1
 */
public class WhereFragment {

    /**
     * The field name where to obtain the parameter value. This name must be used as parameter name
     * on fragment.
     */
    private String name;

    /**
     * Where clause fragment using the annotated value. Example: {@code "obj.id = :id"}
     */
    private String fragment;

    /**
     * Temportal type for annotated value if it is a {@link java.util.Date}. Default: DATE.
     */
    private TemporalType temporalType = TemporalType.DATE;

    private boolean skipSetParameter;
    private Object paramValue;
    private Class<?> paramType;

    public WhereFragment() {
    }

    public WhereFragment(
            Object paramValue,
            Class<?> paramType,
            WhereClauseFragment whereClauseFragment,
            boolean skipSetParameter) {

        name = whereClauseFragment.name();
        fragment = whereClauseFragment.fragment();
        temporalType = whereClauseFragment.temporalType();

        this.paramValue = paramValue;
        this.paramType = paramType;
        this.skipSetParameter = skipSetParameter;
    }

    public boolean isSkipSetParameter() {
        return skipSetParameter;
    }

    public void setSkipSetParameter(boolean useIfNullFragment) {
        skipSetParameter = useIfNullFragment;
    }

    public Object getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object paramValue) {
        this.paramValue = paramValue;
    }

    public Class<?> getParamType() {
        return paramType;
    }

    public void setParamType(Class<?> paramType) {
        this.paramType = paramType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public void setTemporalType(TemporalType temporalType) {
        this.temporalType = temporalType;
    }

}
