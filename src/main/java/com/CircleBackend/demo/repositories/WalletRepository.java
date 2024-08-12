package com.CircleBackend.demo.repositories;

import com.CircleBackend.demo.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
