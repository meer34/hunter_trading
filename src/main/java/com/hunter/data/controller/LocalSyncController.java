package com.hunter.data.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
@PropertySource("classpath:hunter_garments.properties")
public class LocalSyncController {

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

	@Autowired UserRepo userRepo;

	String authToken = null;
	boolean syncStatus = true;

//	@Scheduled(fixedRate = 10000)
	public void startSync() {
		authToken = null;
		int count = 0;
		do {
			syncData();
			count++;
		} while(syncStatus == false && count < 1);

		if(syncStatus) System.out.println("Data sync with remote was completed successfully!\n");
		else System.out.println("Some issues were there while performing data sync with remote server!\n");

	}

	private void syncData() {
		if(getAuthToken()) {
			sendDeleteInfoData();
			System.out.println("Sync completed for local deletes");
			
			sendLocalDataToOnline();
			System.out.println("Sync completed: Local --> Online");
			
			getDeleteInfoData();
			System.out.println("Sync completed for online deletes");
			
			getOnlineDataInLocal();
			System.out.println("Sync completed: Online --> Local");
		}
	}

	private boolean getAuthToken() {
		User restUser = new User("REST_USER", "999999", true, "API_ROLE");
		restUser.setPin("RestPin#312");
		String response = sendRequestToOnlineServer(restUser, "authenticate");
		
		if(response != null && response.startsWith("Bearer")) {
			/*String[] arr = response.split("\\|\\~\\|");
			authToken = arr[0];
			
			restUser.setPin(arr[1]);
			if(sendRequestToOnlineServer(restUser, "ack_token") != null) userRepo.save(restUser);
			*/
			
			authToken = response;
			return true;

		} else return false;
	}

	private void sendLocalDataToOnline() {
		sendDataByType(adminService, Admin.class);
		sendDataByType(moderatorService, Moderator.class);
		sendDataByType(partyService, Party.class);
		sendDataByType(customerService, Customer.class);
		sendDataByType(stockInService, StockIn.class);
		sendDataByType(productService, Product.class);
		sendDataByType(stockOutService, StockOut.class);
		sendDataByType(stockOutProductService, StockOutProduct.class);
		sendDataByType(incomeTypeService, IncomeType.class);
		sendDataByType(expenseTypeService, ExpenseType.class);
		sendDataByType(incomeService, Income.class);
		sendDataByType(expenseService, Expense.class);
		sendDataByType(reminderService, Reminder.class);

	}

	private void getOnlineDataInLocal() {
		getDataByType(adminService, Admin.class, new TypeToken<List<Admin>>(){}.getType());
		getDataByType(moderatorService, Moderator.class, new TypeToken<List<Moderator>>(){}.getType());
		getDataByType(partyService, Party.class, new TypeToken<List<Party>>(){}.getType());
		getDataByType(customerService, Customer.class, new TypeToken<List<Customer>>(){}.getType());
		getDataByType(stockInService, StockIn.class, new TypeToken<List<StockIn>>(){}.getType());
		getDataByType(productService, Product.class, new TypeToken<List<Product>>(){}.getType());
		getDataByType(stockOutService, StockOut.class, new TypeToken<List<StockOut>>(){}.getType());
		getDataByType(stockOutProductService, StockOutProduct.class, new TypeToken<List<StockOutProduct>>(){}.getType());
		getDataByType(incomeTypeService, IncomeType.class, new TypeToken<List<IncomeType>>(){}.getType());
		getDataByType(expenseTypeService, ExpenseType.class, new TypeToken<List<ExpenseType>>(){}.getType());
		getDataByType(incomeService, Income.class, new TypeToken<List<Income>>(){}.getType());
		getDataByType(expenseService, Expense.class, new TypeToken<List<Expense>>(){}.getType());
		getDataByType(reminderService, Reminder.class, new TypeToken<List<Reminder>>(){}.getType());

	}

	private void getDeleteInfoData() {
		try {
			String response = sendRequestToOnlineServer(null, "get_delete_info_data");
			Map<String, Set<Long>> mapOfDeleteRecords = new Gson().fromJson(response, new TypeToken<Map<String, Set<Long>>>(){}.getType());

			if(mapOfDeleteRecords != null && !mapOfDeleteRecords.isEmpty()) {
				System.out.println("Received delete info data : " + mapOfDeleteRecords.toString());
				List<String> ackClassNameList = deleteInfoService.deleteRemoteData(mapOfDeleteRecords);
				sendRequestToOnlineServer(ackClassNameList, "delete_success_ack");
			}

		} catch (Exception e) {
			syncStatus = false;
			e.printStackTrace();
		}
	}

	private void sendDeleteInfoData() {

		Map<String, Set<Long>> mapOfAllDeletedRecords = null;
		try {
			mapOfAllDeletedRecords = deleteInfoService.getAllDeleteInfo();

			if(mapOfAllDeletedRecords.isEmpty()) return;

			System.out.println("Found deleted records for - " + mapOfAllDeletedRecords.keySet().toString());

			String response = sendRequestToOnlineServer(mapOfAllDeletedRecords, "sync_delete_info_data");
			System.out.println("Recived delete request response: " + response); 
			if(response != null && !response.equals("")) {
				System.out.println("Sent data : " + getJsonString(mapOfAllDeletedRecords));
				List<String> ackClassNameList = new Gson().fromJson(response, new TypeToken<List<String>>(){}.getType());

				for (String className : ackClassNameList) {
					Long count = deleteInfoService.processAcknowledgement(className);
					System.out.println("Acknowledgement processed. Released DeleteInfo count - " + count);
				}

			} else {
				System.out.println("Authentication failure while sending delete info!");
				syncStatus = false;
			}

		} catch (Exception e) {
			syncStatus = false;
			e.printStackTrace();
		}

	}

	private void getDataByType(Object dataServiceObject, Class<?> cls, Type typeOf) {
		String classUrl = getUrlFromClassName(cls.getSimpleName());

		//		System.out.println("URL is - " + "get_" + classUrl + "_data");
		String response = sendRequestToOnlineServer(null, "get_" + classUrl + "_data");
		if(response == null) return;

		List<?> listOfData = new Gson().fromJson(response, typeOf);
		if(listOfData == null) return;

		for (Object data : listOfData) {
			try {
				data = dataServiceObject.getClass().getMethod("saveRemoteData", cls).invoke(dataServiceObject, data);
				Long id = (Long) data.getClass().getMethod("getId").invoke(data);
				Long remoteId = (Long) data.getClass().getMethod("getRemoteId").invoke(data);

				boolean ackResponse = sendAckToOnlineServer(id, remoteId, classUrl + "_ack");
				if(!ackResponse) {
					deleteInfoService.deleteData(cls, id);
					syncStatus = false;
				}

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}

	}

	private  void sendDataByType(Object dataServiceObject, Class<?> cls) {
		String classUrl = getUrlFromClassName(cls.getSimpleName());

		List<?> listOfData = null;
		try {
			listOfData = (List<?>) dataServiceObject.getClass().getMethod("findAllNotSyncedData").invoke(dataServiceObject);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		if(listOfData == null) {
			System.out.println("Data list not received from DB");
			return;
		}

		for (Object data : listOfData) {
			try {
				String response = sendRequestToOnlineServer(data, "sync_" + classUrl);
				System.out.println("Response is : " + response);

				if(response != null && !response.equals("")) {
					System.out.println("Sent data : " + getJsonString(data));
					Long remoteId = Long.parseLong(response);
					Long id = (Long) data.getClass().getMethod("getId").invoke(data);

					dataServiceObject.getClass().getMethod("markAsSynced", Long.class, Long.class).invoke(dataServiceObject, id, remoteId);

					System.out.println(cls.getSimpleName() + " - Pushed local id : " + id +" with remote id: " + remoteId);
				} else {
					syncStatus = false;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String sendRequestToOnlineServer(Object data, String url) {
		try {
			String payload = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(data);

			//			System.out.println("Sending Data: " + payload);
			URL urlObj = new URL("https://trading.hunter-software.com/api/" + url);
			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", authToken);
			con.setDoOutput(true);

			try(OutputStream os = con.getOutputStream()) {
				byte[] input = payload.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			int responseCode = con.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line+"\n");
				}
				br.close();
				if(sb.length() > 0) return sb.delete(sb.length()-1, sb.length()).toString();
				else return "";

			} else if(responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
				System.out.println("Authentication failed while sening data!");
				return null;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private boolean sendAckToOnlineServer(Long id, Long remoteId, String url) {
		try {
			url = "https://trading.hunter-software.com/api/" + url + "/" + replaceNull(remoteId) + "/" + replaceNull(id);
			System.out.println("Ack URL is - " + url);

			URL urlObj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
			con.setRequestProperty("Authorization", authToken);

			con.setRequestMethod("PUT");
			int responseCode = con.getResponseCode();

			if(responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("Acknowledgement was sent successfully!");
				return true;
			}  else if(responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
				System.out.println("Authentication failed while sending acknowledgement!");

			} else System.out.println("Acknowledgement send failed!");

		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public String getUrlFromClassName(String classNameString) { 

		StringBuilder classUrlFormatter = new StringBuilder(classNameString);

		Map<Integer, Character> mapOfUpperCharData = new HashMap<>();
		for(int i=classUrlFormatter.length()-1; i>=0; i--) {
			if(Character.isUpperCase(classUrlFormatter.charAt(i))) {
				mapOfUpperCharData.put(i, classUrlFormatter.charAt(i));
			}
		}

		if(!mapOfUpperCharData.isEmpty()) {
			int i = 0;
			String replaceValue = null;
			int startIndex = 0; 
			int endIndex = 0;
			for (Entry<Integer, Character> entry : mapOfUpperCharData.entrySet()) {
				if(entry.getKey() != 0) {
					replaceValue = "_" + entry.getValue().toString().toLowerCase();
					startIndex = entry.getKey() + i;
					endIndex = entry.getKey() + ++i;
				}else {
					startIndex = entry.getKey() + i;
					endIndex = entry.getKey() + i + 1;
					replaceValue = entry.getValue().toString().toLowerCase();
				}
				classUrlFormatter.replace(startIndex, endIndex, replaceValue);
			}
		}

		return classUrlFormatter.toString();
	}

	private long replaceNull(Long val) {
		return val != null ? val : 0L;
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
