package com.CircleBackend.demo.services;

import com.CircleBackend.demo.dto.TransferTokenReq;
import com.CircleBackend.demo.dto.WalletBalanceResDto;
import com.CircleBackend.demo.dto.WalletResDto;
import com.CircleBackend.demo.entities.User;
import com.CircleBackend.demo.entities.Wallet;
import com.CircleBackend.demo.repositories.UserRepository;
import com.CircleBackend.demo.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class WalletService {

    private final CircleAPI circleAPI;

    private final UserRepository userRepository;

    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(CircleAPI circleAPI, UserRepository userRepository, WalletRepository walletRepository) {
        this.circleAPI = circleAPI;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }
    public  WalletResDto createWallet(Long UserId) throws Exception {
        Optional<User> user = userRepository.findById(UserId);

        if (user.isEmpty()){
            throw new RuntimeException("User not found");
        }
        if (user.get().getWallet() != null){
            throw new RuntimeException("Wallet already exists");
        }

        WalletResDto wallet = circleAPI.createWallet();

        Wallet saveWallet= new Wallet();
        saveWallet.setAccountType(wallet.getAccountType());
        saveWallet.setWalletSetId(wallet.getWalletSetId());
        saveWallet.setCreateDate(wallet.getCreateDate());
        saveWallet.setState(wallet.getState());
        saveWallet.setBlockchain(wallet.getBlockchain());
        saveWallet.setUUIDId(wallet.getId());
        saveWallet.setCustodyType(wallet.getCustodyType());
        saveWallet.setUpdateDate(wallet.getUpdateDate());
        saveWallet.setAddress(wallet.getAddress());
        saveWallet.setScaCore(wallet.getScaCore());
        user.get().setWallet(saveWallet);
        userRepository.save(user.get());

        return  wallet;
    }

    public List<WalletResDto> getAllWallet() throws Exception {

        circleAPI.getWallets();

        return null;
    }
    public WalletResDto getWalletById(Long walletId) throws Exception {

        Wallet byId = walletRepository.getById(walletId);
        WalletResDto wallet = toDto(walletRepository.getById(walletId));
        return wallet;
    }




    private WalletResDto toDto(Wallet wallet)  throws Exception {
        if (wallet == null) {
            throw new RuntimeException("Wallet is null");
        }
        WalletResDto walletDto = new WalletResDto();
        walletDto.setAccountType(wallet.getAccountType());
        walletDto.setWalletSetId(wallet.getWalletSetId());
        walletDto.setCreateDate(wallet.getCreateDate());
        walletDto.setState(wallet.getState());
        walletDto.setBlockchain(wallet.getBlockchain());
        walletDto.setUpdateDate(wallet.getUpdateDate());
        walletDto.setAddress(wallet.getAddress());
        walletDto.setScaCore(wallet.getScaCore());
        walletDto.setCustodyType(wallet.getCustodyType());
        walletDto.setId(wallet.getUUIDId());
        return walletDto;
    }

    private Wallet toEntity(WalletResDto walletDto)  throws Exception {
        if (walletDto == null) {
            throw new RuntimeException("walletDto is null");
        }
        Wallet wallet = new Wallet();
        wallet.setAccountType(walletDto.getAccountType());
        wallet.setWalletSetId(walletDto.getWalletSetId());
        wallet.setCreateDate(walletDto.getCreateDate());
        wallet.setState(walletDto.getState());
        wallet.setBlockchain(wallet.getBlockchain());
        wallet.setUpdateDate(walletDto.getUpdateDate());
        wallet.setAddress(walletDto.getAddress());
        wallet.setScaCore(walletDto.getScaCore());
        wallet.setCustodyType(walletDto.getCustodyType());
        wallet.setUUIDId(walletDto.getId());

        return wallet;

    }

    public WalletBalanceResDto getWalletBalance(String id) throws Exception {

        WalletBalanceResDto walletBalance = circleAPI.getWalletBalance(id);

        return walletBalance;
    }

    public String transferToken(TransferTokenReq transferTokenReq, Long id) throws Exception {
        String string = circleAPI.transferToken(transferTokenReq,id);
        return string;
    }
}
