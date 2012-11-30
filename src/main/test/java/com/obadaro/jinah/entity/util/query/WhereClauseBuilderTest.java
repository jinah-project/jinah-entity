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

import org.junit.Assert;
import org.junit.Test;

import com.obadaro.jinah.entity.util.query.WhereClause;
import com.obadaro.jinah.entity.util.query.WhereClauseBuilder;

/**
 * @author Roberto Badaro
 * 
 */
public class WhereClauseBuilderTest {

    @Test
    public void tBuild() {

        Parameters p = new Parameters();
        p.setId(Long.valueOf(1L));
        p.setBeginDate(new Date());
        p.setEndDate(new Date());

        WhereClause wc = WhereClauseBuilder.createWhereClause(p);

        Assert.assertTrue(wc != null);
        Assert.assertTrue(wc.getWhereParameters() != null);
        Assert.assertEquals(4, wc.getWhereParameters().size());
        Assert.assertTrue("obj.id = :id and obj.startDate >= :beginDate and obj.endDate <= :endDate and obj.validated = :valid".equals(wc.getWhereClause()));
    }

    @Test
    public void tBuildNullArgs() {

        Parameters p = new Parameters();
        WhereClause wc = WhereClauseBuilder.createWhereClause(p);

        Assert.assertTrue(wc != null);
        Assert.assertTrue(wc.getWhereParameters() != null);
        Assert.assertEquals(2, wc.getWhereParameters().size());
        Assert.assertTrue("obj.endDate is null and obj.validated = :valid".equals(wc.getWhereClause()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tBuildReturningNull() {

        WhereClauseBuilder.createWhereClause(null);
    }

}
