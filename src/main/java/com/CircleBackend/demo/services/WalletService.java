package com.CircleBackend.demo.services;

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


}
