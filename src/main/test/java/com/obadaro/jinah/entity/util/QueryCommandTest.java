/* 
 * @# QueryCommandTest.java 
 *
 * Copyright 2012 Roberto Badaro
 */
package com.obadaro.jinah.entity.util;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.obadaro.jinah.entity.annotation.util.WhereClauseFragment;
import com.obadaro.jinah.entity.annotation.util.WhereClauseFragments;
import com.obadaro.jinah.entity.util.mock.MockedQuery;
import com.obadaro.jinah.entity.util.mock.MockedTypedQuery;
import com.obadaro.jinah.entity.util.query.Parameters;

/**
 * @author Roberto Badaro
 * 
 */
public class QueryCommandTest {

    private EntityManager em;
    private QueryCommandImpl queryCmd;
    private boolean initialized = false;

    @Before
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void init() {

        if (initialized) {
            return;
        }
        initialized = true;

        em = EasyMock.createMock(EntityManager.class);

        // Named queries
        EasyMock.expect(em.createNamedQuery((String) EasyMock.anyObject())).andReturn(createAnyQuery(null));

        EasyMock.expect(em.createNamedQuery((String) EasyMock.anyObject(), (Class<?>) EasyMock.anyObject()))
            .andReturn(createAnyQuery((String) null, (Class) null));

        // Queries

        EasyMock.expect(em.createQuery((String) EasyMock.anyObject())).andReturn(createAnyQuery(null));

        EasyMock.expect(em.createQuery((String) EasyMock.anyObject(), (Class<?>) EasyMock.anyObject()))
            .andReturn(createAnyQuery((String) null, (Class) null));

        queryCmd = new QueryCommandImpl(em);
    }

    @Test
    public void __start() {
        // noop
    }

    @Test(expected = IllegalArgumentException.class)
    public void tConfigureNullArgs() {

        queryCmd.configure(null, null);
    }

    @Test
    public void tConfigure() {

        Param p = new Param();
        p.setId(Long.valueOf(120L));
        p.setBeginDate(new Date());

        QueryRequest req =
            new QueryRequest(
                "select obj.id from Parameters obj", p, Arrays.asList(new String[] { "obj.startDate" }));

        queryCmd.configure(req, Long.class);

        Assert.assertNotNull(queryCmd.listQuery);
        Assert.assertNull(queryCmd.countQuery);
        Assert.assertNotNull(queryCmd.whereClause);

        Assert.assertTrue(queryCmd.whereClause.getWhereParameters().size() == 2);

        Assert.assertEquals(
            "select obj.id from Parameters obj where obj.id = :id and obj.startDate = :beginDate order by obj.startDate",
            queryCmd.listQuery);

    }

    @Test
    public void tConfigureStartingWithFrom() {

        Param p = new Param();
        p.setId(Long.valueOf(120L));
        p.setBeginDate(new Date());

        QueryRequest req =
            new QueryRequest("from Parameters obj", p, Arrays.asList(new String[] { "obj.startDate" }));

        req.configPartialResult(0, 20, true);

        queryCmd.configure(req, Long.class);

        Assert.assertNotNull(queryCmd.listQuery);
        Assert.assertNotNull(queryCmd.countQuery);
        Assert.assertNotNull(queryCmd.whereClause);

        Assert.assertTrue(queryCmd.whereClause.getWhereParameters().size() == 2);

        Assert.assertEquals(
            "from Parameters obj where obj.id = :id and obj.startDate = :beginDate order by obj.startDate",
            queryCmd.listQuery);

        Assert.assertEquals(
            "select COUNT(*) from Parameters obj where obj.id = :id and obj.startDate = :beginDate",
            queryCmd.countQuery);
    }

    @Test
    public void tConfigureWithCount() {

        Param p = new Param();
        p.setId(Long.valueOf(120L));
        p.setBeginDate(new Date());

        QueryRequest req =
            new QueryRequest(
                "select obj.id from Parameters obj", p, Arrays.asList(new String[] { "obj.startDate" }));
        req.configPartialResult(0, 20, true);

        queryCmd.configure(req, Long.class);

        Assert.assertNotNull(queryCmd.listQuery);
        Assert.assertNotNull(queryCmd.countQuery);
        Assert.assertNotNull(queryCmd.whereClause);

        Assert.assertTrue(queryCmd.whereClause.getWhereParameters().size() == 2);

        Assert.assertEquals(
            "select obj.id from Parameters obj where obj.id = :id and obj.startDate = :beginDate order by obj.startDate",
            queryCmd.listQuery);

        Assert.assertEquals(
            "select count(*) from parameters obj where obj.id = :id and obj.startdate = :begindate",
            queryCmd.countQuery.toLowerCase());

    }

    private Query createAnyQuery(String nameOrQuery) {

        return new MockedQuery();
    }

    private <T> TypedQuery<T> createAnyQuery(String nameOrQuery, Class<T> type) {

        TypedQuery<T> tq = new MockedTypedQuery<T>(new MockedQuery());
        return tq;
    }

    // Inner classes ---------------

    //@formatter:off
    @WhereClauseFragments({
        @WhereClauseFragment(name="id",         fragment = "obj.id = :id"),
        @WhereClauseFragment(name="beginDate",  fragment = "obj.startDate = :beginDate", temporalType = TemporalType.DATE),
        @WhereClauseFragment(name="endDate",    fragment = "obj.endDate = :endDate" ,temporalType = TemporalType.DATE)
    })
    //@formatter:on
    class Param extends Parameters {
        private static final long serialVersionUID = 1L;
    }

}
