package com.CircleBackend.demo.entities;


import jakarta.persistence.*;


@Entity
@Table(name = "TABLE_USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String UserId;
    
    @Enumerated(EnumType.STRING)
    private Role role;

    private String secreString;
    private String Ciphertext;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;

    public User() {
    }

    public User(Long id, String firstName, String lastName, String email, String password, String phone, String userId, Role role, String secreString, String ciphertext, Wallet wallet) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        UserId = userId;
        this.role = role;
        this.secreString = secreString;
        Ciphertext = ciphertext;
        this.wallet = wallet;
    }

    public String getCiphertext() {
        return Ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        Ciphertext = ciphertext;
    }

    public String getSecreString() {
        return secreString;
    }

    public void setSecreString(String secreString) {
        this.secreString = secreString;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
