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
        if (user.get().getWallet() != null) {
            throw new RuntimeException("Wallet already exists");
        }
//        Wallet wallet = new Wallet();
//        String secreString = circleAPI.generateSecret();
//        user.get().setSecreString(secreString);
//
//        String chipperText = circleAPI.generateCiphertext(secreString);
//        user.get().setCiphertext(chipperText);
//        String response= circleAPI.createWallet();
//        user.get().setWallet(wallet);
//        System.out.println(response);

            circleAPI.createWallet();
        return new WalletResDto();

    }

    public List<WalletResDto> getAllWallet() throws Exception {

        circleAPI.getWallets();

        return null;
    }


}
