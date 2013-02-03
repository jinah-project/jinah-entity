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

import static com.obadaro.jinah.common.util.Preconditions.checkArgument;
import static com.obadaro.jinah.common.util.Preconditions.checkArgumentNotNull;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.obadaro.jinah.common.util.reflection.FieldAccess;
import com.obadaro.jinah.common.util.reflection.Reflections;
import com.obadaro.jinah.entity.annotation.BusinessKey;

/**
 * Provides methods to recover the fields declared on a BusinessKey annotation; execute
 * {@code equals} and construct {@code hashcode} of annotated object using the business key fields.
 * 
 * @author Roberto Badaro
 */
public class BusinessKeyHelper {

    public static Map<Class<?>, FieldAccess[]> businessKeyClassMap =
            new WeakHashMap<Class<?>, FieldAccess[]>();

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final ReadLock rLock = lock.readLock();
    private static final WriteLock wLock = lock.writeLock();

    /**
     * Returns the fields declared on BusinessKey annotation. If the annotation is not present, or
     * the value is empty, or has inexistent field names, returns an empty FieldAccess array.
     * If the number of recovered fields is not equal to the number of declared fields, an
     * {@link BusinessKeyException} is thrown.
     * 
     * @param clazz
     *            Class annotated with BusinessKey.
     * 
     * @return The fields declared on BusinessKey annotation. Or an empty FieldAccess array, if no
     *         fields declared/found.
     */
    public static FieldAccess[] getBusinessKeyFields(final Class<?> clazz) {

        checkArgumentNotNull(clazz, "The 'clazz' parameter can't be null.");

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

            if (businessKey != null && businessKey.length > 0) {
                _businessKeyFields = Reflections.findFields(clazz, null, false, businessKey);

                if (businessKey.length != _businessKeyFields.length) {
                    throw new BusinessKeyException(String.format(
                        "Declared fields (%s) differs of fields found (%s).", businessKey.length,
                        _businessKeyFields.length));
                }
            } else {
                _businessKeyFields = new FieldAccess[0];
            }

            businessKeyClassMap.put(clazz, _businessKeyFields);
            return _businessKeyFields;
        } finally {
            wLock.unlock();
        }
    }

    /**
     * Checks if the {@code other} object is "equal to" {@code thiz} object using the BusinessKey
     * declared fields. If no BusinessKey annotation is present on {@code thiz} object class, an
     * {@code BusinessKeyException} is thrown.
     * 
     * @param thiz
     *            The mandatory object.
     * @param other
     *            The other one to be compared against {@code thiz}.
     * @return {@code true} if thiz object is the same as the {@code other} argument; false
     *         otherwise.
     */
    public static boolean equals(final Object thiz, final Object other) {

        checkArgumentNotNull(thiz, "The 'thiz' parameter can't be null.");

        if (thiz == other) {
            return true;
        }

        final Class<?> clazz = thiz.getClass();
        FieldAccess[] fBusinessKey = getBusinessKeyFields(clazz);
        validate(clazz, fBusinessKey);

        return equals(thiz, other, fBusinessKey);
    }

    /**
     * Checks if the {@code other} object is "equal to" {@code thiz} object using the BusinessKey
     * declared fields.
     * 
     * @param thiz
     *            The mandatory object.
     * @param other
     *            The other one to be compared against {@code thiz}.
     * 
     * @param fBusinessKey
     *            The business key fields.
     * @return {@code true} if thiz object is the same as the {@code other} argument; false
     *         otherwise.
     */
    public static boolean equals(final Object thiz, final Object other, final FieldAccess[] fBusinessKey) {

        checkArgumentNotNull(thiz, "The 'thiz' parameter can't be null.");
        checkArgument(fBusinessKey != null && fBusinessKey.length > 0,
            "The 'fBusinessKey' parameter must be informed.");

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

                if (v1 != v2 && (v1 == null || v2 == null || !v1.equals(v2))) {
                    return false;
                }
            } catch (final Exception e) {
                throw new BusinessKeyException(
                    String.format("Error processing equals: %s", e.getMessage()), e);
            }
        }

        return true;
    }

    /**
     * Calculates the hashcode using the fields of "obj" as declared on his BusinessKey annotation.
     * 
     * @param obj
     * @return
     */
    public static int hashCode(final Object obj) {

        return hashCode(obj, 37);
    }

    /**
     * Calculates the hashcode using the fields of "obj" as declared on his BusinessKey annotation.
     * 
     * @param obj
     * @param primeBase
     * @return
     */
    public static int hashCode(final Object obj, final int primeBase) {

        checkArgumentNotNull(obj, "The 'obj' parameter can't be null.");

        Class<?> clazz = obj.getClass();
        FieldAccess[] fBusinessKey = getBusinessKeyFields(clazz);
        validate(clazz, fBusinessKey);

        return hashCode(obj, fBusinessKey, primeBase);
    }

    /**
     * Calculates the hashcode using the fields of "obj" as declared on his BusinessKey annotation.
     * 
     * @param obj
     * @param fBusinessKey
     * @param primeBase
     * @return
     */
    public static int hashCode(final Object obj, final FieldAccess[] fBusinessKey, final int primeBase) {

        checkArgumentNotNull(obj, "The 'obj' parameter can't be null.");
        checkArgument(fBusinessKey != null && fBusinessKey.length > 0,
            "The 'fBusinessKey' parameter must be informed.");
        checkArgument(primeBase > 0, "The 'primeBase' must be > 0.");

        int result = 1;

        try {
            for (final FieldAccess f : fBusinessKey) {
                final Object v = f.get(obj);
                result = primeBase * result + (v != null ? v.hashCode() : 0);
            }
            return result;

        } catch (final Exception e) {
            throw new BusinessKeyException(String.format("Error processing hashCode: %s", e.getMessage()), e);
        }
    }

    /**
     * Validate if fBusinessKey is provided. If not, an BusinessKeyException is thrown.
     * 
     * @param clazz
     * @param fBusinessKey
     */
    protected static void validate(Class<?> clazz, final FieldAccess[] fBusinessKey) {

        if (fBusinessKey == null || fBusinessKey.length == 0) {
            throw new BusinessKeyException(String.format(
                "Invalid or inexistent BusinessKey annotaion for '%s'.", clazz.getName()));
        }
    }
}
