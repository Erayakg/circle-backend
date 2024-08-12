package com.CircleBackend.demo.dto;

import com.CircleBackend.demo.entities.Role;

import java.io.Serializable;

/**
 * DTO for {@link com.CircleBackend.demo.entities.User}
 */
public record UserWalletResDto(String firstName, String lastName, String email, String password, String phone,
                               Role role, String secreString, String Ciphertext,
                               WalletDto wallet) implements Serializable {
    /**
     * DTO for {@link com.CircleBackend.demo.entities.Wallet}
     */
    public record WalletDto(String UUIDId, String state, String walletSetId, String custodyType, String address,
                            String blockchain, String accountType, String updateDate, String createDate,
                            String scaCore) implements Serializable {
    }
}