package com.obadaro.jinah.entity.util;

import java.io.Serializable;
import java.util.List;

import com.obadaro.jinah.entity.annotation.util.WhereClauseFragments;

/**
 * @author Roberto Badaro
 * 
 */
public class QueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The values of {@code query/queryCount} are NamedQueries names?
     */
    private boolean namedQuery;

    private String query;

    /**
     * If this query request is used to retrieve partial result and the primary query is not good to
     * simply replace the select clause by a {@code COUNT}, a specialized query must be informed.
     * <p>
     * If this is a NamedQuery request, then the {@code queryCount} must be a NamedQuery name where
     * the select clause is a "SELECT COUNT".
     * </p>
     */
    private String queryCount;

    /** Query parameters. An object with {@link WhereClauseFragments} annotation. */
    private Serializable queryParameters;

    /**
     * Order by fragments to append to the query. Used only if this is not a NamedQuery request.
     */
    private List<String> orderBy;

    /**
     * Must execute the queryCount to know general total rows will be retrieved?
     */
    private boolean countTotal = false;

    private int firstRow = 0;

    /**
     * Max results to retrieve.
     */
    private int maxResults = 0;

    /**
     * 
     */
    public QueryRequest() {
    }

    /**
     * @param query
     */
    public QueryRequest(String query) {
        this.query = query;
    }

    /**
     * @param query
     * @param queryParameters
     */
    public QueryRequest(String query, Serializable queryParameters) {

        this.query = query;
        this.queryParameters = queryParameters;
    }

    /**
     * 
     * @param query
     * @param queryParameters
     * @param orderBy
     */
    public QueryRequest(String query, Serializable queryParameters, List<String> orderBy) {

        this.query = query;
        this.queryParameters = queryParameters;
        this.orderBy = orderBy;
    }

    /**
     * Sets partial result configuration.
     * 
     * @param firstRow
     * @param maxResults
     * @param countTotal
     * @return A reference to this object.
     */
    public QueryRequest configPartialResult(int firstRow, int maxResults, boolean countTotal) {

        this.countTotal = countTotal;
        this.firstRow = firstRow;
        this.maxResults = maxResults;

        return this;
    }

    /**
     * Returns {@code true} if the values of {@code query/queryCount} are NamedQueries names.
     * 
     * @return {@code true} if the values of {@code query/queryCount} are NamedQueries names.
     */
    public boolean isNamedQuery() {
        return namedQuery;
    }

    /**
     * Inform if the values of {@code query/queryCount} are NamedQueries names.
     * 
     * @param namedQuery
     *            {@code query/queryCount} are NamedQueries names?
     */
    public void setNamedQuery(boolean namedQuery) {
        this.namedQuery = namedQuery;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(String queryCount) {
        this.queryCount = queryCount;
    }

    public Serializable getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Serializable queryParameters) {
        this.queryParameters = queryParameters;
    }

    /**
     * Returns the "order by" fragments to append to the query. Used only if this is not a
     * NamedQuery request.
     * 
     * @return "Order by" fragments to append to the query. Used only if this is not a NamedQuery
     *         request.
     */
    public List<String> getOrderBy() {
        return orderBy;
    }

    /**
     * Sets the "order by" fragments to append to the query. Used only if this is not a NamedQuery
     * request.
     * 
     * @param orderBy
     *            "order by" fragments to append to the query.
     */
    public void setOrderBy(List<String> orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isCountTotal() {
        return countTotal;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public int getMaxResults() {
        return maxResults;
    }

}
