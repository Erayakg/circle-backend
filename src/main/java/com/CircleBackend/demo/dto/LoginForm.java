package com.CircleBackend.demo.dto;

import java.util.Objects;

public class LoginForm {

    private final String username;
    private final String password;

    public LoginForm(String email, String password) {
        this.username = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginForm entity = (LoginForm) o;
        return Objects.equals(this.username, entity.username) &&
                Objects.equals(this.password, entity.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "email = " + username + ", " +
                "password = " + password + ")";
    }
}
