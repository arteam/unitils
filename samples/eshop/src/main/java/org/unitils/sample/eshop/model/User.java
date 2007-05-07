/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.sample.eshop.model;


import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Represents an eshop user
 */
@Entity
@Table(name = "USER")
@SequenceGenerator(name = "SEQUENCE", sequenceName = "USER_ID_SEQ")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE")
    private Long id;

    @Column
    private String userName;

    @Column
    private int age;

    public User(Long id, String userName, int age) {
        this.id = id;
        this.userName = userName;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public int getAge() {
        return age;
    }
}
