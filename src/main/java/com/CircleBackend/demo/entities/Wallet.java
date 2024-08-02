package com.CircleBackend.demo.entities;

import jakarta.persistence.*;

@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String address;
    private String balance;
    private String walletId;

    public Wallet() {
    }

    public Wallet(Long id, String address, String balance, String walletId) {
        this.id = id;
        this.address = address;
        this.balance = balance;
        this.walletId = walletId;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }
}
