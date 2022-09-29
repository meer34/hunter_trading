package com.hunter.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.hunter.web.model.Reminder;
import com.hunter.web.repo.ReminderRepo;
import com.hunter.web.specification.ReminderSearchSpecification;
import com.hunter.web.util.SearchSpecificationBuilder;

@Service
public class ReminderService {

	@Autowired
	private ReminderRepo reminderRepo;

	public Reminder saveReminderToDB(Reminder reminder) {
		return reminderRepo.save(reminder);
	}

	public Reminder findReminderById(Long id) {
		return reminderRepo.findById(id).get();
	}

	public Page<Reminder> getAllReminders(Integer pageNo, Integer pageSize) {
		return reminderRepo.findAll(PageRequest.of(pageNo, pageSize));
	}
	
	public List<Reminder> getAllPendingRemindersForToday() {
		return reminderRepo.findAllPendingRemindersForToday();
	}

	public Page<Reminder> searchReminderByDateAndKeyword(String keyword, 
			String fromDate, String toDate, int pageNo, Integer pageSize) {

		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		ReminderSearchSpecification spec = (ReminderSearchSpecification) SearchSpecificationBuilder.build(fromDate, toDate, keyword, Reminder.class);
		return reminderRepo.findAll(spec, pageRequest);

	}

	public void deleteReminderById(Long id) {
		reminderRepo.deleteById(id);
	}

}
