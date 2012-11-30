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
package com.obadaro.jinah.entity.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @since 1.1
 * @author Roberto Badaro
 * 
 * @param <E>
 */
public class QueryResult<E> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The query resulting list. */
    protected List<E> resultList = Collections.emptyList();

    /** Relative index of first row on the general result. */
    protected int firstRow = 0;

    /** Partial record count. */
    protected int parcialCount = 0;

    /** Total record count. */
    protected int totalCount = 0;

    /**
     * No arg constructor.
     */
    public QueryResult() {
        // noop
    }

    /**
     * 
     * @param resultList
     *            The query resulting list.
     * @param firstRow
     *            Relative index of first row on the general result.
     * @param totalCount
     *            Total record count.
     */
    public QueryResult(List<E> resultList, int firstRow, int totalCount) {
        this.resultList = resultList;
        this.firstRow = firstRow;
        this.parcialCount = resultList != null ? resultList.size() : 0;
        this.totalCount = totalCount;
    }

    /**
     * Returns the query resulting list.
     * 
     * @return The query resulting list.
     */
    public List<E> getResultList() {
        return resultList;
    }

    /**
     * Sets the query resulting list.
     * 
     * @param resultList
     *            The query resulting list.
     */
    public void setResultList(List<E> resultList) {
        this.resultList = resultList;
    }

    /**
     * Returns relative index of first row on the general result.
     * 
     * @return Relative index of first row on the general result.
     */
    public int getFirstRow() {
        return firstRow;
    }

    /**
     * Sets the relative index of first row on the general result.
     * 
     * @param firstRow
     *            Relative index of first row on the general result.
     */
    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    /**
     * Returns the partial record count.
     * 
     * @return Partial record count.
     */
    public int getParcialCount() {
        return parcialCount;
    }

    /**
     * Sets the partial record count.
     * 
     * @param parcialCount
     *            Partial record count.
     */
    public void setParcialCount(int parcialCount) {
        this.parcialCount = parcialCount;
    }

    /**
     * Returns the total record count.
     * 
     * @return Total record count.
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the total record count.
     * 
     * @param totalCount
     *            Total record count.
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

}
