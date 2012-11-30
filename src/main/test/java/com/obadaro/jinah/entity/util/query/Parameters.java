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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.TemporalType;

import com.obadaro.jinah.entity.annotation.util.WhereClauseFragment;
import com.obadaro.jinah.entity.annotation.util.WhereClauseFragments;

/**
 * @author Roberto Badaro
 * 
 */
//@formatter:off
@WhereClauseFragments({
    @WhereClauseFragment(name="id",         fragment = "obj.id = :id"),
    @WhereClauseFragment(name="beginDate",  fragment = "obj.startDate >= :beginDate", temporalType = TemporalType.DATE),
    @WhereClauseFragment(name="endDate",    fragment = "obj.endDate <= :endDate", 
                                            ifNull = "obj.endDate is null",temporalType = TemporalType.DATE),
    @WhereClauseFragment(name="valid",      fragment = "obj.validated = :valid")
})
//@formatter:on
public class Parameters implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Date beginDate;
    private Date endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isValid() {
        return true;
    }

}
