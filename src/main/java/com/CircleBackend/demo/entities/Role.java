package com.CircleBackend.demo.entities;

public enum Role {

    ROLE_ADMIN("ADMIN", 1),
    ROLE_USER("USER", 2),
    ROLE_SELLER("SELLER", 3);

    private String name;
    private int type;

    Role(String user, int type) {
        this.name = user;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}