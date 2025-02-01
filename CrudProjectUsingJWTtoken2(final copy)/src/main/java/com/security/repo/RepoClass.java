package com.security.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.security.model.Usertable;

@Repository
public interface RepoClass extends JpaRepository<Usertable, Integer>
{
	Usertable findByUsername(String username);
}
