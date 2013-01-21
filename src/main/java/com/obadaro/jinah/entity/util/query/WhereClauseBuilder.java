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

import static com.obadaro.jinah.common.util.Preconditions.checkArgumentNotNull;
import static com.obadaro.jinah.common.util.Preconditions.checkState;
import static com.obadaro.jinah.common.util.Strings.isBlank;
import static com.obadaro.jinah.common.util.Strings.isNotBlank;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.obadaro.jinah.common.JinahException;
import com.obadaro.jinah.common.util.reflection.FieldAccess;
import com.obadaro.jinah.common.util.reflection.Reflections;
import com.obadaro.jinah.entity.annotation.util.WhereClauseFragment;
import com.obadaro.jinah.entity.annotation.util.WhereClauseFragments;

/**
 * A WhereClause builder.
 * 
 * @author Roberto Badaro
 * @since 1.1
 */
public class WhereClauseBuilder {

    protected static final Class<?>[] NO_ARGS = new Class<?>[0];


    /**
     * Creates a {@link WhereClause} object with the {@code parameters}.
     * 
     * @param parameters
     *            Object annotated with {@link WhereClauseFragments}.
     * @return A {@link WhereClause} object with the {@code parameters}.
     */
    public static WhereClause createWhereClause(Object parameters) {

        checkArgumentNotNull(parameters);

        Class<?> clazz = parameters.getClass();
        WhereClauseFragments fragments = clazz.getAnnotation(WhereClauseFragments.class);

        checkState(fragments != null,
            String.format("@WhereClauseFragments not found on class %s.", clazz.getName()));

        WhereClauseFragment[] wcfs = fragments.value();
        String[] names = new String[wcfs.length];

        Map<String, WhereClauseFragment> fragmentsMap = createFragmentsMap(wcfs, names);

        Map<String, WhereFragment> whereParameters = new HashMap<String, WhereFragment>(wcfs.length);
        StringBuilder where = new StringBuilder();

        FieldAccess[] fas = Reflections.findFields(clazz, names);

        buildWhereFragments(parameters, fas, fragmentsMap, whereParameters, where);
        buildRemainingWhereFragments(parameters, fragmentsMap, whereParameters, where);

        String swhere = where.toString();
        if (endsWithOperator(swhere)) {
            swhere = swhere.substring(0, swhere.length() - 4);
        }

        WhereClause whereClause = new WhereClause(whereParameters, swhere.trim());
        return whereClause;
    }

    protected static Map<String, WhereClauseFragment> createFragmentsMap(final WhereClauseFragment[] fragments,
                                                                        final String[] names) {

        Map<String, WhereClauseFragment> fragmentsMap =
            new HashMap<String, WhereClauseFragment>(names.length);

        for (int i = 0; i < names.length; i++) {
            final WhereClauseFragment wcf = fragments[i];
            final String name = wcf.name();
            names[i] = name;
            fragmentsMap.put(name, wcf);
        }

        return fragmentsMap;
    }

    protected static void buildWhereFragments(final Object parameters,
                                              final FieldAccess[] fas,
                                              final Map<String, WhereClauseFragment> fragmentsMap,
                                              final Map<String, WhereFragment> whereParameters,
                                              final StringBuilder where) {

        for (FieldAccess fa : fas) {
            String name = fa.getName();
            WhereClauseFragment wcf = fragmentsMap.remove(name);
            createFragment(wcf, fa.get(parameters), fa.getField().getType(), whereParameters, where);
        }
    }

    /**
     * Builds remaining "where fragments" using the properties getter methods.
     * 
     * @param parameters
     * @param fragmentsMap
     * @param whereParameters
     * @param where
     */
    protected static void buildRemainingWhereFragments(final Object parameters,
                                                       final Map<String, WhereClauseFragment> fragmentsMap,
                                                       final Map<String, WhereFragment> whereParameters,
                                                       final StringBuilder where) {

        if (fragmentsMap.isEmpty()) {
            return;
        }

        Class<?> clazz = parameters.getClass();
        List<String> notfound = new ArrayList<String>();

        for (Entry<String, WhereClauseFragment> entry : fragmentsMap.entrySet()) {
            String name = entry.getKey();
            Method m = findPublicGetter(clazz, name);

            if (m != null) {
                createFragment(entry.getValue(), invokeGetter(parameters, m), m.getReturnType(),
                    whereParameters, where);
            } else {
                notfound.add(name);
            }
        }

        if (!notfound.isEmpty()) {
            String missing = Arrays.toString(notfound.toArray());
            throw new JinahException(String.format(
                "Missing properties: %s. No fields or public getters found.", missing));
        }
    }

    protected static Method findPublicGetter(Class<?> clazz, String name) {

        String baseName = name.substring(0, 1).toUpperCase() + name.substring(1);
        String get = "get" + baseName;
        String is = "is" + baseName;

        Method m = Reflections.findMethod(clazz, get, NO_ARGS);
        if (m == null || !Modifier.isPublic(m.getModifiers())) {
            m = Reflections.findMethod(clazz, is, NO_ARGS);
        }

        if (m != null && Modifier.isPublic(m.getModifiers())) {
            return m;
        }
        return null;
    }

    protected static Object invokeGetter(Object parameters, Method getter) {

        try {
            return getter.invoke(parameters);
        } catch (Throwable e) {
            throw new JinahException(e);
        }
    }

    protected static WhereFragment createFragment(WhereClauseFragment wcf,
                                                  Object paramValue,
                                                  Class<?> paramType,
                                                  Map<String, WhereFragment> whereParameters,
                                                  StringBuilder where) {

        boolean empty = isEmpty(wcf, paramValue);
        String fragment = null;

        if (empty && isNotBlank(wcf.ifNull())) {
            fragment = wcf.ifNull();
        } else if (!empty) {
            fragment = wcf.fragment();
        }

        WhereFragment wf = null;
        if (isNotBlank(fragment)) {
            fragment = fragment.trim();
            wf = new WhereFragment(paramValue, paramType, wcf, empty);
            whereParameters.put(wcf.name(), wf);

            where.append(fragment).append(' ');
            if (!endsWithOperator(fragment)) {
                where.append("and ");
            }
        }

        return wf;
    }

    /**
     * Returns {@code true} if {@code value} is null. If is a empty or blank String and
     * {@link WhereClauseFragment#blankStringAsNull()} equals TRUE, returns {@code true}. Otherwise,
     * returns {@code false}.
     * 
     * @param wcf
     *            WhereClauseFragment
     * @param value
     *            Object
     * @return {@code true} if {@code value} is null.
     */
    protected static boolean isEmpty(WhereClauseFragment wcf, Object value) {

        if (value != null) {
            if (value instanceof String) {
                return (isBlank((String) value) && wcf.blankStringAsNull());
            }
            return false;
        }
        return true;
    }

    /**
     * Returns {@code true} if the {@code fragment} ends with "AND" or "OR" operator.
     * 
     * @param fragment
     *            Where fragment.
     * @return {@code true} if the {@code fragment} ends with "AND" or "OR" operator, otherwise
     *         {@code false}.
     */
    protected static boolean endsWithOperator(String fragment) {

        String tmp = fragment.trim().toLowerCase();
        return (tmp.endsWith(" and") || tmp.endsWith(" or") || tmp.endsWith(")and") || tmp.endsWith(")or"));
    }
}
