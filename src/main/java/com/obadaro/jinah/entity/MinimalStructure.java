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

import java.io.Serializable;

/**
 * A minimalist structure that overrides the {@link #equals(Object)} and {@link #hashCode()}
 * methods, delegating the work to {@link BusinessKeyHelper}.
 * <p>
 * This class can be used as ancestor for both entity classes and composite key classes.
 * </p>
 * 
 * @author Roberto Badaro
 * @version $Id$
 */
public abstract class MinimalStructure implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MinimalStructure)) {
            return false;
        }
        return BusinessKeyHelper.equals(this, obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return BusinessKeyHelper.hashCode(this);
    }

}
