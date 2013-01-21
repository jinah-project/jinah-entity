/* 
 * JINAH Project - Java Is Not A Hammer
 * http://obadaro.com/jinah
 * 
 * Copyright (C) 2010-2012 Roberto Badaro 
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
package com.obadaro.jinah.entity;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.obadaro.jinah.common.util.reflection.FieldAccess;
import com.obadaro.jinah.common.util.reflection.Reflections;
import com.obadaro.jinah.entity.annotation.BusinessKey;

/**
 * @author Roberto Badaro
 */
public class BusinessKeyHelper {

    public static Map<Class<?>, FieldAccess[]> businessKeyClassMap = new WeakHashMap<Class<?>, FieldAccess[]>();

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final ReadLock rLock = lock.readLock();
    private static final WriteLock wLock = lock.writeLock();

    /**
     * @return
     */
    public static FieldAccess[] getBusinessKeyFields(final Class<?> clazz) {

        FieldAccess[] _businessKeyFields = null;

        rLock.lock();
        try {
            _businessKeyFields = businessKeyClassMap.get(clazz);
            if (_businessKeyFields != null) {
                return _businessKeyFields;
            }
        } finally {
            rLock.unlock();
        }

        wLock.lock();
        try {
            // retest
            _businessKeyFields = businessKeyClassMap.get(clazz);
            if (_businessKeyFields != null) {
                return _businessKeyFields;
            }

            String[] businessKey = null;
            final BusinessKey bk = clazz.getAnnotation(BusinessKey.class);
            if (bk != null) {
                businessKey = bk.value();
            }

            if (businessKey == null || businessKey.length == 0) {
                businessKey = new String[] { "id" };
            }

            _businessKeyFields = Reflections.findFields(clazz, businessKey);

            businessKeyClassMap.put(clazz, _businessKeyFields);
            return _businessKeyFields;
        } finally {
            wLock.unlock();
        }
    }

    /**
     * @param thiz
     * @param other
     * @return
     */
    public static boolean equals(final Object thiz, final Object other) {

        if (thiz == other) {
            return true;
        }

        FieldAccess[] fBusinessKey = null;
        if (thiz != null) {
            fBusinessKey = getBusinessKeyFields(thiz.getClass());
        }

        return equals(thiz, other, fBusinessKey);
    }

    /**
     * @param thiz
     * @param other
     * @param fBusinessKey
     * @return
     */
    public static boolean equals(final Object thiz,
                                 final Object other,
                                 final FieldAccess[] fBusinessKey) {

        if (thiz == other) {
            return true;
        }

        if (other == null || !thiz.getClass().isAssignableFrom(other.getClass())) {
            return false;
        }

        for (final FieldAccess f : fBusinessKey) {
            try {
                final Object v1 = f.get(thiz);
                final Object v2 = f.get(other);

                if ((v1 != null && v2 == null) || (v1 == null && v2 != null)) {
                    return false;
                }

                if (v1 != v2) {
                    if (!v1.equals(v2)) {
                        return false;
                    }
                }
            } catch (final Exception e) {
                throw new IllegalStateException("Error processing equals: " + e.getMessage(), e);
            }
        }

        return true;
    }

    /**
     * Calculate hashcode using prime 37.
     * 
     * @param obj
     * @return
     */
    public static int hashCode(final Object obj) {

        return hashCode(obj, 37);
    }

    public static int hashCode(final Object obj, final int primeBase) {

        FieldAccess[] fBusinessKey = null;
        if (obj != null) {
            fBusinessKey = getBusinessKeyFields(obj.getClass());
        }

        return hashCode(obj, fBusinessKey, primeBase);
    }

    /**
     * @param obj
     * @param fBusinessKey
     * @param primeBase
     * @return
     */
    public static int hashCode(final Object obj,
                               final FieldAccess[] fBusinessKey,
                               final int primeBase) {

        int result = 1;

        try {
            for (final FieldAccess f : fBusinessKey) {
                final Object v = f.get(obj);
                result = primeBase * result + (v != null ? v.hashCode() : 0);
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Error processing hashCode: " + e.getMessage(), e);
        }

        return result;
    }

}
