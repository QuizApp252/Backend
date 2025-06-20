package org.example.backend.repository;

import org.example.backend.model.Otp;
import org.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IOtpRepository extends JpaRepository<Otp,Integer> {
    Optional<Otp> findByUserEmail(String email);
    void deleteByUser(User user);
}
