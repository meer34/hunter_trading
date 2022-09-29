

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchSpecificationBuilder {

	private static List<SearchCriteria> listOfCriteria;
	static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	public static StockInSearchSpecification build(String fromDate, String toDate, String keyword, Class<?> classObj) {
		try {
			listOfCriteria = new ArrayList<>();

			for (Field field : classObj.getDeclaredFields()) {
				if(field.getGenericType().getTypeName().equals("java.lang.String")) {
					listOfCriteria.add(new SearchCriteria(field.getName(), ":", keyword));
				};
				//java.lang.Integer, java.lang.Double, int, long, double
				//root.get(criteria.getKey()).getJavaType() == String.class
			}

			if(fromDate != null && !fromDate.equalsIgnoreCase("") && toDate != null && !toDate.equalsIgnoreCase("")) {
				listOfCriteria.add(new SearchCriteria("date", ">", formatter.parse(fromDate)));
				listOfCriteria.add(new SearchCriteria("date", "<", formatter.parse(toDate)));

			} else if((fromDate == null || fromDate.equalsIgnoreCase("")) && toDate != null && !toDate.equalsIgnoreCase("")) {
				listOfCriteria.add(new SearchCriteria("date", "<", formatter.parse(toDate)));

			} else if((toDate == null || toDate.equalsIgnoreCase("")) && fromDate != null && !fromDate.equalsIgnoreCase("")) {
				listOfCriteria.add(new SearchCriteria("date", ">", formatter.parse(fromDate)));

			}
			
			System.out.println("#####Criteria size --" + listOfCriteria.size());
			return new StockInSearchSpecification(listOfCriteria);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
