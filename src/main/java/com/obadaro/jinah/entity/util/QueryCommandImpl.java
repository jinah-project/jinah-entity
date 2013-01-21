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

import static com.obadaro.jinah.common.util.Preconditions.checkArgument;
import static com.obadaro.jinah.common.util.Preconditions.checkArgumentNotNull;
import static com.obadaro.jinah.common.util.Strings.isBlank;
import static com.obadaro.jinah.common.util.Strings.isNotBlank;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.obadaro.jinah.common.util.Strings;
import com.obadaro.jinah.entity.annotation.util.WhereClauseFragments;
import com.obadaro.jinah.entity.util.query.WhereClause;
import com.obadaro.jinah.entity.util.query.WhereClauseBuilder;

/**
 * A {@link QueryCommand} implementation.
 * 
 * @author Roberto Badaro
 * 
 * @see QueryCommand
 * @see QueryRequest
 * @see QueryResult
 * @see WhereClauseFragments
 * @see WhereClauseBuilder
 */
public class QueryCommandImpl implements QueryCommand {

    protected final EntityManager em;
    protected QueryRequest request;
    protected Class<?> resultType;
    protected WhereClause whereClause;
    protected String listQuery;
    protected String countQuery;

    protected boolean isNamedQuery;
    protected boolean countTotalRows;

    /**
     * Constructor to receive an EntityManager to execute the queries.
     * 
     * @param em
     */
    public QueryCommandImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public <T> void configure(QueryRequest request, Class<T> resultType) {

        checkArgumentNotNull(resultType, "resultType");
        checkArgumentNotNull(request, "request");
        checkArgument(isNotBlank(request.getQuery()), "request.getQuery()");

        this.request = request;
        this.resultType = resultType;
        listQuery = request.getQuery();
        countQuery = request.getQueryCount();
        whereClause = null;

        countTotalRows = request.isCountTotal();
        isNamedQuery = request.isNamedQuery();

        if (countTotalRows && isBlank(countQuery)) {
            checkArgument(!isNamedQuery, "request.getQueryCount()");

            int p = 0;
            String lower = listQuery.toLowerCase();
            if (!lower.startsWith("from ")) {
                p = lower.indexOf(" from ");
            }

            countQuery = "select COUNT(*) " + listQuery.substring(p).trim();
        }

        createWhereClause();
        appendWhereClause();
        appendOrderBy();
    }

    @Override
    public <T> QueryResult<T> execute() {

        QueryResult<T> result = new QueryResult<T>();

        if (countTotalRows) {
            int count = countTotalRows();
            if (count == 0) {
                return result;
            }
            result.setTotalCount(count);
        }

        Query q = null;
        boolean useTypedQuery = (Object.class != resultType);

        if (!isNamedQuery) {
            if (useTypedQuery) {
                q = em.createQuery(listQuery, resultType);
            } else {
                q = em.createQuery(listQuery);
            }
        } else {
            if (useTypedQuery) {
                q = em.createNamedQuery(listQuery, resultType);
            } else {
                q = em.createNamedQuery(listQuery);
            }
        }

        int firstRow = request.getFirstRow();
        int maxRows = request.getMaxResults();

        if (firstRow > -1) {
            q.setFirstResult(firstRow);
            result.setFirstRow(firstRow);
        }
        if (maxRows > 0) {
            q.setMaxResults(maxRows);
        }

        setQueryParameters(q, false);

        @SuppressWarnings("unchecked")
        List<T> list = q.getResultList();
        result.setResultList(list);
        result.setParcialCount(list.size());

        if (!countTotalRows) {
            result.setTotalCount(result.getParcialCount());
        }

        return result;
    }

    /**
     * Creates the {@link WhereClause} object.
     */
    protected void createWhereClause() {

        if (request.getQueryParameters() != null) {
            whereClause = WhereClauseBuilder.createWhereClause(request.getQueryParameters());
        }
    }

    /**
     * Appends the WHERE clause from {@link WhereClause} object to the listQuery and countQuery if
     * needed.
     */
    protected void appendWhereClause() {

        if (whereClause != null && !isNamedQuery) {
            String where = whereClause.getWhereClause();

            listQuery = appendClause(listQuery, SelectPart.where, where);
            if (countTotalRows) {
                countQuery = appendClause(countQuery, SelectPart.where, where);
            }
        }
    }

    /**
     * Appends the order by clause if defined.
     */
    protected void appendOrderBy() {

        List<String> listOrderBy = request.getOrderBy();
        if (!isNamedQuery && listOrderBy != null && !listOrderBy.isEmpty()) {
            String order = Arrays.toString(listOrderBy.toArray());
            listQuery = appendClause(listQuery, SelectPart.order_by, order.substring(1, order.length() - 1));
        }
    }

    /**
     * Sets the query parameters before execution.
     * 
     * @param query
     * @param isCountQuery
     */
    protected void setQueryParameters(Query query, boolean isCountQuery) {

        if (whereClause != null) {
            WhereClause.setQueryParameters(query, whereClause);
        }
    }

    /**
     * Appends a {@code clause} to the {@code query}.
     * 
     * @param query
     * @param token
     *            The clause token (where, order by, group by)
     * @param clause
     *            The clause fragment.
     * @return
     */
    protected String appendClause(String query, SelectPart part, String clause) {

        if (Strings.isBlank(clause)) {

            return query;

        } else {
            String token = part.getPart();
            StringBuilder sb = new StringBuilder(query.trim());

            if (!query.trim().toLowerCase().contains(" " + token + " ")) {

                sb.append(' ').append(token).append(' ');

            } else if (SelectPart.where == part) {

                sb.append(" and ");
            }

            return sb.append(clause).toString();
        }
    }

    /**
     * Executes the count query.
     * 
     * @return
     */
    protected int countTotalRows() {

        TypedQuery<Long> qCount = null;

        if (!isNamedQuery) {
            qCount = em.createQuery(countQuery, Long.class);
        } else {
            qCount = em.createNamedQuery(countQuery, Long.class);
        }

        setQueryParameters(qCount, true);

        return qCount.getSingleResult().intValue();
    }

    // Inner classes

    public static enum SelectPart {
        select, from, where, group_by("group by"), having, order_by("order by");

        private final String part;

        private SelectPart() {
            part = name();
        }

        private SelectPart(String token) {
            part = token;
        }

        public String getPart() {
            return part;
        }
    }

}
