package com.shailu.deposito_dental_pos.repository;


import com.shailu.deposito_dental_pos.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String code);
    Optional<User> findByUsernameAndPassword(String username, String password);

}
