package com.CircleBackend.demo.controller;

import com.CircleBackend.demo.dto.TransferTokenReq;
import com.CircleBackend.demo.dto.WalletBalanceResDto;
import com.CircleBackend.demo.dto.WalletResDto;
import com.CircleBackend.demo.services.CircleAPI;
import com.CircleBackend.demo.services.WalletService;
import org.hibernate.internal.build.AllowNonPortable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/wallet")
public class WalletController {

    private final WalletService walletService;
    @Autowired
    public WalletController( WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/create/{id}")
    public ResponseEntity<WalletResDto> createWallet(@PathVariable Long id) throws Exception {
        WalletResDto wallet = walletService.createWallet(id);
        return new ResponseEntity<>(wallet, HttpStatus.CREATED);
    }

    @GetMapping("/getAllWallet")
    public ResponseEntity<List<WalletResDto>> getAllWallet() throws Exception {
        List<WalletResDto> AllWallet = walletService.getAllWallet();
        return new ResponseEntity<>(AllWallet, HttpStatus.ACCEPTED);
    }
    @GetMapping("/{id}/getWallet")
    public ResponseEntity<WalletResDto> getWallet(@PathVariable Long id) throws Exception {

        return new ResponseEntity<>(walletService.getWalletById(id),HttpStatus.FOUND);
    }
    @GetMapping("/{id}/getWalletBalance")
    public ResponseEntity<WalletBalanceResDto> getWalletBalance(@PathVariable String id) throws Exception {
        return new ResponseEntity<>(walletService.getWalletBalance(id),HttpStatus.FOUND);
    }
    @PostMapping("/transaction/{id}")
    public ResponseEntity<String> transferToken(@RequestBody TransferTokenReq transferTokenReq ,@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(walletService.transferToken(transferTokenReq,id),HttpStatus.OK);
    }

}
