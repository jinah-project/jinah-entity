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

import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.obadaro.jinah.entity.util.mock.MockedQuery;

/**
 * @author Roberto Badaro
 *
 */
public class WhereClauseTest {

    @Test
    public void tSetQueryParams() {

        Parameters p = new Parameters();
        p.setId(Long.valueOf(120L));
        p.setBeginDate(new Date());

        WhereClause wc = WhereClauseBuilder.createWhereClause(p);

        MockedQuery query = new MockedQuery();
        WhereClause.setQueryParameters(query, wc);

        Map<String, Object> params = query.getSettedParams();

        Assert.assertTrue(params != null);
        Assert.assertEquals(3, params.size());
        Assert.assertTrue(p.getId().equals(params.get("id")));
        Assert.assertTrue(p.getBeginDate().equals(params.get("beginDate")));

        Object pvalid = params.get("valid");
        Assert.assertTrue(pvalid != null);
        Assert.assertTrue(pvalid instanceof Boolean);
        Assert.assertTrue(((Boolean) pvalid) == p.isValid());
    }

    @Test
    public void tSetQueryParamsOneParameter() {

        Parameters p = new Parameters();
        p.setId(Long.valueOf(120L));
        p.setBeginDate(null);

        WhereClause wc = WhereClauseBuilder.createWhereClause(p);

        MockedQuery query = new MockedQuery();
        WhereClause.setQueryParameters(query, wc);

        Map<String, Object> params = query.getSettedParams();

        Assert.assertTrue(params != null);
        Assert.assertEquals(2, params.size());
        Assert.assertTrue(p.getId().equals(params.get("id")));
        Assert.assertTrue(null == params.get("beginDate"));
    }

}
