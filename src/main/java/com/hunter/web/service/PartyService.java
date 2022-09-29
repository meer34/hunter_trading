package com.hunter.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hunter.web.model.Party;
import com.hunter.web.repo.PartyRepo;

@Service
public class PartyService {

	@Autowired
	private PartyRepo partyRepo;

	public Party saveUserToDB(Party admin) {
		return partyRepo.save(admin);
	}
	
	public Party findUserById(Long id) {
		return partyRepo.findById(id).get();
	}

	public List<Party> getAllUsers() {
		return partyRepo.findAll();
	}

	public void deleteUserById(Long id) {
		partyRepo.deleteById(id);
	}

}
