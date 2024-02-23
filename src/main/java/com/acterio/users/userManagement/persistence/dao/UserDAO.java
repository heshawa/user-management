package com.acterio.users.userManagement.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.acterio.users.userManagement.model.User;

public interface UserDAO extends JpaRepository<User, String> {
    @Query(value = "SELECT SUBSTRING(email, INSTR(email, '@') + 1) AS domain, COUNT(*) AS count FROM user GROUP BY domain", nativeQuery = true)
    List<Object[]> countUsersByDomain();
    
    List<User> findUserByEmailAddress(String emailAddress);
}
