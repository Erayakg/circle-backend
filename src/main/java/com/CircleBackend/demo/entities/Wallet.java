package com.CircleBackend.demo.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String UUIDId;
    private String state;
    private String walletSetId;
    private String custodyType;
    private String address;
    private String blockchain;
    private String accountType;
    private String updateDate;
    private String createDate;
    private String scaCore;

    public Wallet() {
    }

    public Wallet(Long id, String UUIDId, String state, String walletSetId, String custodyType, String address, String blockchain, String accountType, String updateDate, String createDate, String scaCore) {
        this.id = id;
        this.UUIDId = UUIDId;
        this.state = state;
        this.walletSetId = walletSetId;
        this.custodyType = custodyType;
        this.address = address;
        this.blockchain = blockchain;
        this.accountType = accountType;
        this.updateDate = updateDate;
        this.createDate = createDate;
        this.scaCore = scaCore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUUIDId() {
        return UUIDId;
    }

    public void setUUIDId(String UUIDId) {
        this.UUIDId = UUIDId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getWalletSetId() {
        return walletSetId;
    }

    public void setWalletSetId(String walletSetId) {
        this.walletSetId = walletSetId;
    }

    public String getCustodyType() {
        return custodyType;
    }

    public void setCustodyType(String custodyType) {
        this.custodyType = custodyType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(String blockchain) {
        this.blockchain = blockchain;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getScaCore() {
        return scaCore;
    }

    public void setScaCore(String scaCore) {
        this.scaCore = scaCore;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(id, wallet.id) && Objects.equals(UUIDId, wallet.UUIDId) && Objects.equals(state, wallet.state) && Objects.equals(walletSetId, wallet.walletSetId) && Objects.equals(custodyType, wallet.custodyType) && Objects.equals(address, wallet.address) && Objects.equals(blockchain, wallet.blockchain) && Objects.equals(accountType, wallet.accountType) && Objects.equals(updateDate, wallet.updateDate) && Objects.equals(createDate, wallet.createDate) && Objects.equals(scaCore, wallet.scaCore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, UUIDId, state, walletSetId, custodyType, address, blockchain, accountType, updateDate, createDate, scaCore);
    }
}