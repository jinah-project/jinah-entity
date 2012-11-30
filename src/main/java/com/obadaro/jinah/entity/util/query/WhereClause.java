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

import static com.obadaro.jinah.common.util.Preconditions.checkArgumentNotNull;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 * 
 * 
 * @author Roberto Badaro
 * @since 1.1
 */
public class WhereClause {

    protected Map<String, WhereFragment> whereParameters;
    protected String whereClause;

    public WhereClause() {
    }

    /**
     * @param whereParameters
     * @param whereClause
     */
    public WhereClause(Map<String, WhereFragment> whereParameters, String whereClause) {
        this.whereParameters = whereParameters;
        this.whereClause = whereClause;
    }

    public Map<String, WhereFragment> getWhereParameters() {
        return whereParameters;
    }

    public void setWhereParameters(Map<String, WhereFragment> whereParameters) {
        this.whereParameters = whereParameters;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    /**
     * Sets the query parameters using the {@link WhereClause} information.
     * 
     * @param query
     *            {@link Query} to set the {@code whereClause} parameters.
     * @param whereClause
     *            The {@code WhereClause} with parameters values.
     */
    public static void setQueryParameters(Query query, WhereClause whereClause) {

        checkArgumentNotNull(query, "query");
        checkArgumentNotNull(whereClause, "whereClause");

        for (Entry<String, WhereFragment> entry : whereClause.getWhereParameters().entrySet()) {
            WhereFragment wf = entry.getValue();

            if (!wf.isSkipSetParameter()) {
                String pname = entry.getKey();
                Object pvalue = wf.getParamValue();

                if (!(pvalue instanceof Date)) {
                    query.setParameter(pname, pvalue);

                } else {
                    TemporalType temporalType = wf.getTemporalType();
                    query.setParameter(pname, (Date) pvalue, temporalType);
                }
            }
        }
    }

}
