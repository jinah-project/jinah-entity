/* 
 * @# MockedTypedQuery.java 
 *
 * Copyright 2012 Roberto Badaro
 */
package com.obadaro.jinah.entity.util.mock;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

/**
 * @author Roberto Badaro
 *
 */
public class MockedTypedQuery<X> implements TypedQuery<X> {

    protected MockedQuery query;

    public MockedTypedQuery(MockedQuery query) {
        this.query = query;
    }

    @Override
    public int executeUpdate() {
        return 0;
    }

    @Override
    public int getMaxResults() {
        return 0;
    }

    @Override
    public int getFirstResult() {
        return 0;
    }

    @Override
    public Map<String, Object> getHints() {
        return null;
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return null;
    }

    @Override
    public Parameter<?> getParameter(String name) {
        return null;
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        return null;
    }

    @Override
    public Parameter<?> getParameter(int position) {
        return null;
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        return null;
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        return false;
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param) {
        return null;
    }

    @Override
    public Object getParameterValue(String name) {
        return null;
    }

    @Override
    public Object getParameterValue(int position) {
        return null;
    }

    @Override
    public FlushModeType getFlushMode() {
        return null;
    }

    @Override
    public LockModeType getLockMode() {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return null;
    }

    @Override
    public List<X> getResultList() {
        return null;
    }

    @Override
    public X getSingleResult() {
        return null;
    }

    @Override
    public TypedQuery<X> setMaxResults(int maxResult) {
        return null;
    }

    @Override
    public TypedQuery<X> setFirstResult(int startPosition) {
        return null;
    }

    @Override
    public TypedQuery<X> setHint(String hintName, Object value) {
        return null;
    }

    @Override
    public <T> TypedQuery<X> setParameter(Parameter<T> param, T value) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(String name, Object value) {
        query.setParameter(name, value);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(String name, Date value, TemporalType temporalType) {
        query.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(int position, Object value) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(int position, Calendar value, TemporalType temporalType) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(int position, Date value, TemporalType temporalType) {
        return null;
    }

    @Override
    public TypedQuery<X> setFlushMode(FlushModeType flushMode) {
        return null;
    }

    @Override
    public TypedQuery<X> setLockMode(LockModeType lockMode) {
        return null;
    }

}
