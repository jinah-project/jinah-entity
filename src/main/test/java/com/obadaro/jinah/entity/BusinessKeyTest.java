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

import org.junit.Assert;
import org.junit.Test;

import com.obadaro.jinah.common.util.reflection.FieldAccess;
import com.obadaro.jinah.entity.annotation.BusinessKey;

/**
 * @author Roberto Badaro
 */
public class BusinessKeyTest {

    public void config() {
        //
    }

    @Test
    public void tRecoverBusinessKey() throws Exception {

        final FieldAccess[] fields = BusinessKeyHelper.getBusinessKeyFields(DaEntity.class);
        Assert.assertTrue(checkKeys(DaEntity.class, fields));
    }

    @Test
    public void tRecoverBusinessKeyForNonAnnotated() throws Exception {

        final FieldAccess[] fields = BusinessKeyHelper.getBusinessKeyFields(NonAnnotatedEntity.class);
        Assert.assertTrue(fields.length == 0);
    }

    @Test
    public void tEquals() throws Exception {

        final AnotherEntity a = new AnotherEntity();
        final AnotherEntity b = new AnotherEntity();

        a.nome = "Minas";
        b.nome = "Minas";

        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void tEqualsNonAnnotated() throws Exception {

        final NonAnnotatedEntity a = new NonAnnotatedEntity();
        final NonAnnotatedEntity b = new NonAnnotatedEntity();

        a.nome = "Minas";
        b.nome = "Minas";

        try {
            a.equals(b);

        } catch (Exception e) {
            Assert.assertTrue(e instanceof BusinessKeyException);
            Assert.assertTrue(e.getMessage().startsWith("Invalid or inexistent"));
        }
    }

    @Test
    public void tHashCodeNonAnnotated() throws Exception {

        final NonAnnotatedEntity a = new NonAnnotatedEntity();
        a.nome = "Minas";

        try {
            a.hashCode();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof BusinessKeyException);
            Assert.assertTrue(e.getMessage().startsWith("Invalid or inexistent"));
        }
    }

    @Test
    public void tNotEquals() throws Exception {

        final AnotherEntity a = new AnotherEntity();
        final AnotherEntity b = new AnotherEntity();

        a.nome = "Minas";
        b.nome = "Sao Paulo";

        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void tEqualsRaw() throws Exception {

        final DaEntity a = new DaEntity();
        final DaEntity b = new DaEntity();

        a.sigla = "MG";
        a.nome = "Minas";

        b.sigla = "MG";
        b.nome = "Minas";

        Assert.assertTrue(BusinessKeyHelper.equals(a, b));
    }

    @Test
    public void tNotEqualsRaw() throws Exception {

        final DaEntity a = new DaEntity();
        final DaEntity b = new DaEntity();

        a.sigla = "MG";
        a.nome = "Minas";

        b.sigla = "SP";
        b.nome = "Sao Paulo";

        Assert.assertFalse(BusinessKeyHelper.equals(a, b));
    }

    @Test
    public void tHashCodeEquals() throws Exception {

        final DaEntity a = new DaEntity();
        final DaEntity b = new DaEntity();

        a.sigla = "MG";
        a.nome = "Minas";

        b.sigla = "MG";
        b.nome = "Minas";

        int hashA = BusinessKeyHelper.hashCode(a);
        int hashB = BusinessKeyHelper.hashCode(b);

        Assert.assertTrue(hashA == hashB);
    }

    @Test
    public void tHashCodeNotEquals() throws Exception {

        final DaEntity a = new DaEntity();
        final DaEntity b = new DaEntity();

        a.sigla = "MG";
        a.nome = "Minas";

        b.sigla = "MG";
        b.nome = "Minas Gerais";

        int hashA = BusinessKeyHelper.hashCode(a);
        int hashB = BusinessKeyHelper.hashCode(b);

        Assert.assertTrue(hashA != hashB);
    }

    boolean checkKeys(final Class<?> annotatedClass, final FieldAccess[] fieldsFound) {

        final BusinessKey bkAnnotation = annotatedClass.getAnnotation(BusinessKey.class);
        if (bkAnnotation == null) {
            throw new IllegalArgumentException("bkAnnotation is null.");
        }

        final String[] keys = bkAnnotation.value();

        if (fieldsFound == null || fieldsFound.length != keys.length) {
            return false;
        } else if (keys != null) {
            final int len = keys.length;
            for (int i = 0; i < len; i++) {
                if (!keys[i].equals(fieldsFound[i].getName())) {
                    return false;
                }
            }
        }

        return true;
    }

    // --------------------------------------------------------------
    // Inner classes
    // --------------------------------------------------------------

    @BusinessKey({ "sigla", "nome" })
    class DaEntity {
        String sigla;
        String nome;

        public String getSigla() {
            return sigla;
        }

        public void setSigla(String sigla) {
            this.sigla = sigla;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

    @BusinessKey("nome")
    class AnotherEntity extends MinimalStructure {
        private static final long serialVersionUID = 1L;
        String nome;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

    class NonAnnotatedEntity extends MinimalStructure {
        private static final long serialVersionUID = 1L;
        String nome;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

}
