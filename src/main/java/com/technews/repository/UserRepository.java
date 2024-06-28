package com.technews.repository;

import com.technews.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Contains data retrieval, storage, and search functionality
@Repository

//JPA Repository ensures that CRUD methods and SQL are visible to the interface
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(String email) throws Exception;
}
