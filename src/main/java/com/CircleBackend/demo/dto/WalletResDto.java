package com.CircleBackend.demo.dto;

import lombok.Data;

@Data
public class WalletResDto {

    private String id;
    private String state;
    private String walletSetId;
    private String custodyType;
    private String  address;
    private String blockchain;
    private String  accountType;
    private String  updateDate;
    private String  createDate;
    private String  scaCore;


}
