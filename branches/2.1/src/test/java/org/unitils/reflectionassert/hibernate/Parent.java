/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.reflectionassert.hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

/**
 * todo javadoc
 *
 * @author Tim Peeters
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Entity
public class Parent implements Serializable {

    private Long id;

    private List<Child> children;


    public Parent() {
        this(null);
    }


    public Parent(Long id) {
        this.id = id;
    }


    @Id
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    @OneToMany(mappedBy = "parent")
    public List<Child> getChildren() {
        return children;
    }


    public void setChildren(List<Child> children) {
        this.children = children;
    }
}
