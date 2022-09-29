package com.hunter.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hunter.web.model.Party;

public interface PartyRepo extends JpaRepository<Party, Long>{
	
}
