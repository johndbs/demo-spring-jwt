package com.thinkitdevit.demospringjwt.repository;

import com.thinkitdevit.demospringjwt.model.ERole;
import com.thinkitdevit.demospringjwt.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole role);
}
