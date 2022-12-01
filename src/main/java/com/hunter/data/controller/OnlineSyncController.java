package com.hunter.data.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunter.web.model.Admin;
import com.hunter.web.model.Customer;
import com.hunter.web.model.Expense;
import com.hunter.web.model.ExpenseType;
import com.hunter.web.model.Income;
import com.hunter.web.model.IncomeType;
import com.hunter.web.model.Moderator;
import com.hunter.web.model.Party;
import com.hunter.web.model.Product;
import com.hunter.web.model.Reminder;
import com.hunter.web.model.StockIn;
import com.hunter.web.model.StockOut;
import com.hunter.web.model.StockOutProduct;
import com.hunter.web.model.User;
import com.hunter.web.repo.UserRepo;
import com.hunter.web.service.AdminService;
import com.hunter.web.service.CustomerService;
import com.hunter.web.service.DeleteInfoService;
import com.hunter.web.service.ExpenseService;
import com.hunter.web.service.ExpenseTypeService;
import com.hunter.web.service.IncomeService;
import com.hunter.web.service.IncomeTypeService;
import com.hunter.web.service.ModeratorService;
import com.hunter.web.service.PartyService;
import com.hunter.web.service.ProductService;
import com.hunter.web.service.ReminderService;
import com.hunter.web.service.StockInService;
import com.hunter.web.service.StockOutProductService;
import com.hunter.web.service.StockOutService;

@RestController
@RequestMapping("/api")
@PropertySource("classpath:hunter_garments.properties")
public class OnlineSyncController {

	@Autowired AdminService adminService;
	@Autowired ModeratorService moderatorService;
	@Autowired CustomerService customerService;
	@Autowired PartyService partyService;
	@Autowired StockInService stockInService;
	@Autowired ProductService productService;
	@Autowired StockOutService stockOutService;
	@Autowired StockOutProductService stockOutProductService;
	@Autowired ReminderService reminderService;
	@Autowired IncomeTypeService incomeTypeService;
	@Autowired ExpenseTypeService expenseTypeService;
	@Autowired IncomeService incomeService;
	@Autowired ExpenseService expenseService;
	@Autowired DeleteInfoService deleteInfoService;

	@Autowired private JwtTokenUtil jwtTokenUtil;
	@Autowired UserRepo userRepo;

	@RequestMapping(value="/authenticate")
	public String hello(@RequestBody User user) {
		User restUser = userRepo.getUserByUsername(user.getUsername());
		System.out.println("User = " + user.getUsername());
		if(restUser == null) return "NO USER FOUND!";
		
		if(new BCryptPasswordEncoder().matches(user.getPin(), restUser.getPin())) {
			System.out.println("Authentication successful !");
			
//			String newPin = new DecimalFormat("000000").format(new Random().nextInt(999999));
			return jwtTokenUtil.generateToken(user.getUsername()); // + "|~|" + newPin;

		} else {
			return "INVALID CREDENTIAL";
		}
	}
	
	@PostMapping(value="/ack_token")
	@ResponseStatus(HttpStatus.OK)
	public void ackPinChange(@RequestBody User user) {
		User restUser = userRepo.getUserByUsername(user.getUsername());
		System.out.println("Pin - " + user.getPin());
		restUser.setPin(new BCryptPasswordEncoder().encode(user.getPin()));
		userRepo.save(restUser);

	}

	@PostMapping("/get_admin_data")
	@ResponseStatus(HttpStatus.OK)
	public String getAdminData() {
		return getJsonString(adminService.findAllNotSyncedData());

	}

	@PostMapping("/get_moderator_data")
	@ResponseStatus(HttpStatus.OK)
	public String getModeratorData() {
		return getJsonString(moderatorService.findAllNotSyncedData());

	}

	@PostMapping("/get_customer_data")
	@ResponseStatus(HttpStatus.OK)
	public String getCustomerData() {
		return getJsonString(customerService.findAllNotSyncedData());

	}

	@PostMapping("/get_party_data")
	@ResponseStatus(HttpStatus.OK)
	public String getPartyData() {
		return getJsonString(partyService.findAllNotSyncedData());

	}

	@PostMapping("/get_stock_in_data")
	@ResponseStatus(HttpStatus.OK)
	public String getStockInData() {
		return getJsonString(stockInService.findAllNotSyncedData());

	}

	@PostMapping("/get_product_data")
	@ResponseStatus(HttpStatus.OK)
	public String getProductData() {
		return getJsonString(productService.findAllNotSyncedData());

	}

	@PostMapping("/get_stock_out_data")
	@ResponseStatus(HttpStatus.OK)
	public String getStockOutData() {
		return getJsonString(stockOutService.findAllNotSyncedData());

	}

	@PostMapping("/get_stock_out_product_data")
	@ResponseStatus(HttpStatus.OK)
	public String getStockOutProductData() {
		return getJsonString(stockOutProductService.findAllNotSyncedData());

	}

	@PostMapping("/get_income_type_data")
	@ResponseStatus(HttpStatus.OK)
	public String getIncomeTypeData() {
		return getJsonString(incomeTypeService.findAllNotSyncedData());

	}

	@PostMapping("/get_expense_type_data")
	@ResponseStatus(HttpStatus.OK)
	public String getExpenseTypeData() {
		return getJsonString(expenseTypeService.findAllNotSyncedData());

	}

	@PostMapping("/get_income_data")
	@ResponseStatus(HttpStatus.OK)
	public String getIncomeData() {
		return getJsonString(incomeService.findAllNotSyncedData());

	}

	@PostMapping("/get_expense_data")
	@ResponseStatus(HttpStatus.OK)
	public String getExpenseData() {
		return getJsonString(expenseService.findAllNotSyncedData());

	}

	@PostMapping("/get_reminder_data")
	@ResponseStatus(HttpStatus.OK)
	public String getReminderData() {
		return getJsonString(reminderService.findAllNotSyncedData());

	}

	@PutMapping("/admin_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void adminAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		adminService.markAsSynced(id, remoteId);

	}

	@PutMapping("/moderator_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void moderatorAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		moderatorService.markAsSynced(id, remoteId);

	}

	@PutMapping("/party_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void partyAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		partyService.markAsSynced(id, remoteId);

	}

	@PutMapping("/customer_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void customerAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		customerService.markAsSynced(id, remoteId);

	}

	@PutMapping("/stock_in_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void stockInAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		stockInService.markAsSynced(id, remoteId);

	}

	@PutMapping("/product_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void productAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		productService.markAsSynced(id, remoteId);

	}

	@PutMapping("/stock_out_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void stockOutAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		stockOutService.markAsSynced(id, remoteId);

	}

	@PutMapping("/stock_out_product_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void stockOutProductAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		stockOutProductService.markAsSynced(id, remoteId);

	}

	@PutMapping("/income_type_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void incomeTypeAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		incomeTypeService.markAsSynced(id, remoteId);

	}

	@PutMapping("/expense_type_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void expenseTypeAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		expenseTypeService.markAsSynced(id, remoteId);

	}

	@PutMapping("/income_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void incomeAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		incomeService.markAsSynced(id, remoteId);

	}

	@PutMapping("/expense_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void expenseAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		expenseService.markAsSynced(id, remoteId);

	}

	@PutMapping("/reminder_ack/{id}/{remoteId}")
	@ResponseStatus(HttpStatus.OK)
	public void reminderAck(@PathVariable("id") Long id, @PathVariable("remoteId") Long remoteId) {
		System.out.println("Id: " + id + " Remote id: " + remoteId);
		reminderService.markAsSynced(id, remoteId);

	}

	@RequestMapping(value = "/sync_admin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> syncAdmin(@RequestBody Admin admin) {
		Long id = admin.getId();
		try{
			System.out.println("Received data : " + getJsonString(admin));
			admin = adminService.saveRemoteData(admin);
			System.out.println("Admin local data stored successfully in server");

			return ResponseEntity.ok(admin.getId());

		} catch(Exception e) {
			System.out.println("Exception in Admin data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@RequestMapping(value = "/sync_moderator", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> syncModerator(@RequestBody Moderator moderator) {
		Long id = moderator.getId();
		try{
			System.out.println("Received data : " + getJsonString(moderator));
			moderator = moderatorService.saveRemoteData(moderator);
			System.out.println("Moderator local data stored successfully in server");

			return ResponseEntity.ok(moderator.getId());

		} catch(Exception e) {
			System.out.println("Exception in Moderator data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@RequestMapping(value = "/sync_party", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> syncParty(@RequestBody Party party) {
		Long id = party.getId();
		try{
			System.out.println("Received data : " + getJsonString(party));
			party = partyService.saveRemoteData(party);
			System.out.println("Party local data stored successfully in server");

			return ResponseEntity.ok(party.getId());

		} catch(Exception e) {
			System.out.println("Exception in Party data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@RequestMapping(value = "/sync_customer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> syncCustomer(@RequestBody Customer customer) {
		Long id = customer.getId();
		try{
			System.out.println("Received data : " + getJsonString(customer));
			customer = customerService.saveRemoteData(customer);
			System.out.println("Customer local data stored successfully in server");

			return ResponseEntity.ok(customer.getId());

		} catch(Exception e) {
			System.out.println("Exception in Customer data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@RequestMapping(value = "/sync_stock_in", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> syncStockIn(@RequestBody StockIn stockIn) {
		Long id = stockIn.getId();
		try{
			System.out.println("Received data : " + getJsonString(stockIn));
			stockIn = stockInService.saveRemoteData(stockIn);
			System.out.println("StockIn local data stored successfully in server");

			return ResponseEntity.ok(stockIn.getId());

		} catch(Exception e) {
			System.out.println("Exception in StockIn data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@RequestMapping(value = "/sync_product", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> syncProduct(@RequestBody Product product) {
		Long id = product.getId();
		try{
			System.out.println("Received data : " + getJsonString(product));
			product = productService.saveRemoteData(product);
			System.out.println("Product local data stored successfully in server");

			return ResponseEntity.ok(product.getId());

		} catch(Exception e) {
			System.out.println("Exception in Product data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@RequestMapping(value = "/sync_stock_out", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> syncStockOut(@RequestBody StockOut stockOut) {
		Long id = stockOut.getId();
		try{
			System.out.println("Received data : " + getJsonString(stockOut));
			stockOut = stockOutService.saveRemoteData(stockOut);
			System.out.println("StockOut local data stored successfully in server");

			return ResponseEntity.ok(stockOut.getId());

		} catch(Exception e) {
			System.out.println("Exception in StockOut data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@RequestMapping(value = "/sync_stock_out_product", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> syncStockOutProduct(@RequestBody StockOutProduct stockOutProduct) {
		Long id = stockOutProduct.getId();
		try{
			System.out.println("Received data : " + getJsonString(stockOutProduct));
			stockOutProduct = stockOutProductService.saveRemoteData(stockOutProduct);
			System.out.println("StockOutProduct local data stored successfully in server");

			return ResponseEntity.ok(stockOutProduct.getId());

		} catch(Exception e) {
			System.out.println("Exception in StockOutProduct data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@RequestMapping(value = "/sync_incomeType", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> syncIncomeType(@RequestBody IncomeType incomeType) {
		Long id = incomeType.getId();
		try{
			System.out.println("Received data : " + getJsonString(incomeType));
			incomeType = incomeTypeService.saveRemoteData(incomeType);
			System.out.println("IncomeType local data stored successfully in server");

			return ResponseEntity.ok(incomeType.getId());

		} catch(Exception e) {
			System.out.println("Exception in IncomeType data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@RequestMapping(value = "/sync_expense_type", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> sync(@RequestBody ExpenseType expenseType) {
		Long id = expenseType.getId();
		try{
			System.out.println("Received data : " + getJsonString(expenseType));
			expenseType = expenseTypeService.saveRemoteData(expenseType);
			System.out.println("ExpenseType local data stored successfully in server");

			return ResponseEntity.ok(expenseType.getId());

		} catch(Exception e) {
			System.out.println("Exception in ExpenseType data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@RequestMapping(value = "/sync_income", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> syncIncome(@RequestBody Income income) {
		Long id = income.getId();
		try{
			System.out.println("Received data : " + getJsonString(income));
			income = incomeService.saveRemoteData(income);
			System.out.println("Income local data stored successfully in server");

			return ResponseEntity.ok(income.getId());

		} catch(Exception e) {
			System.out.println("Exception in Income data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@RequestMapping(value = "/sync_expense", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> sync(@RequestBody Expense expense) {
		Long id = expense.getId();
		try{
			System.out.println("Received data : " + getJsonString(expense));
			expense = expenseService.saveRemoteData(expense);
			System.out.println("Expense local data stored successfully in server");

			return ResponseEntity.ok(expense.getId());

		} catch(Exception e) {
			System.out.println("Exception in Expense data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@RequestMapping(value = "/sync_reminder", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> syncReminder(@RequestBody Reminder reminder) {
		Long id = reminder.getId();
		try{
			System.out.println("Received data : " + getJsonString(reminder));
			reminder = reminderService.saveRemoteData(reminder);
			System.out.println("Reminder local data stored successfully in server");

			return ResponseEntity.ok(reminder.getId());

		} catch(Exception e) {
			System.out.println("Exception in Reminder data sync for id: " + id + "Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@PostMapping("/get_delete_info_data")
	@ResponseStatus(HttpStatus.OK)
	public String getDeleteInfo() {
		return getJsonString(deleteInfoService.getAllDeleteInfo());

	}

	@PostMapping("/delete_success_ack")
	@ResponseStatus(HttpStatus.OK)
	public void deleteSuccessAck(@RequestBody List<String> ackClassNameList) {
		for (String className : ackClassNameList) {
			Long count = deleteInfoService.processAcknowledgement(className);
			System.out.println("Acknowledgement processed. Released DeleteInfo count - " + count);
		}
	}

	@RequestMapping(value = "/sync_delete_info_data", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> syncDeleteInfo(@RequestBody Map<String, Set<Long>> mapOfAllDeletedRecords) {
		try{
			System.out.println("Received delete data : " + mapOfAllDeletedRecords.toString());
			List<String> ackClassNameList = deleteInfoService.deleteRemoteData(mapOfAllDeletedRecords);
			return ResponseEntity.ok(ackClassNameList);

		} catch(Exception e) {
			System.out.println("Exception in delete info data sync");
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	private String getJsonString(Object data) {
		try {
			return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(data);

		} catch (IOException e) {
			System.out.println("Exception while converting data to JSON String.");
			e.printStackTrace();
		}
		return null;
	}

}
