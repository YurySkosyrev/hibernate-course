package com.dmdev.entity;

import java.time.LocalDate;

public class User {

    private String username;
    private String firstname;
    private String lastname;
    private LocalDate birthDate;
    private Integer age;

    public User() {
    }

    public User(String username, String firstname, String lastname, LocalDate birthDate, Integer age) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthDate = birthDate;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", birthDate=" + birthDate +
                ", age=" + age +
                '}';
    }
}
