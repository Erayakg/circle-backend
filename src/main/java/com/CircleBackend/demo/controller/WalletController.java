package com.CircleBackend.demo.controller;

import com.CircleBackend.demo.services.CircleAPI;
import com.CircleBackend.demo.services.WalletService;
import org.hibernate.internal.build.AllowNonPortable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/wallet")
public class WalletController {

    private final WalletService walletService;
    @Autowired
    public WalletController( WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/create/{id}")
    public ResponseEntity<String> createWallet(@PathVariable Long id) throws Exception {
        String wallet = walletService.createWallet(id);
        return new ResponseEntity<>(wallet, HttpStatus.CREATED);
    }



}