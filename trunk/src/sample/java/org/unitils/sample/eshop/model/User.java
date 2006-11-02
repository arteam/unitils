package org.unitils.sample.eshop.model;


import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 *
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
