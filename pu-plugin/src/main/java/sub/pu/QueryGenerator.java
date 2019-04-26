package sub.pu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueryGenerator {

	private Set<String> mappings = new HashSet<>();
	private UmlautWordMapper mapper;
	private List<String> combinations;
	
	public QueryGenerator() {
		mappings.add("†:j");
		mappings.add("0:O");
		mappings.add("1:l,I");
		mappings.add("3:\\?");
		mappings.add("5:•f");
		mappings.add("6:€");
		mappings.add("8:S");
		mapper = new UmlautWordMapper(mappings);
	}
	
	public String generateQuery(RegestInfo regestInfo) {
		combinations = mapper.createMappings(regestInfo.number);
		
		String regestNumberQuery = generateOrQuery();
		combinations.add(regestInfo.number.substring(0, 1));
		String regestNumberQueryWithStars = generateOrQueryWithStars();
		
		String dateFuzzyQuery = generateFuzzyQuery(regestInfo.date);
		String dateExactQuery = generateDateOrQuery(regestInfo.date);
		
		String wholeQuery = "page:" + regestInfo.page + " AND "
				+ "("
					+ "line_spaceless:" + regestNumberQueryWithStars + "^40 OR "
					+ "line:" + regestNumberQuery + "^5 OR "
					+ "line:" + dateFuzzyQuery + "^3 OR "
					+ "line:" + dateExactQuery + " OR "
					+ "next_line:*" + regestInfo.pope.split(" ")[0] + "*"
				+ ")";
		return wholeQuery;
	}
	
	public String generateOrQuery() {
		return createQuery(combinations, "OR", "");
	}
		
	public String generateOrQueryWithStars() {
		return createQuery(combinations, "OR", "*");
	}
	
	public String generateFuzzyQuery(String date) {
		date = date.replace("(", "").replace(")", "");
		String[] parts = date.split("[ \\.-]+");
		return createQuery(new ArrayList<String>(Arrays.asList(parts)), "AND", "~1");
	}
	
	public String generateDateOrQuery(String date) {
		date = date.replace("(", "").replace(")", "");
		String[] parts = date.split("[ \\.-]+");
		return createQuery(new ArrayList<String>(Arrays.asList(parts)), "OR", "");
	}
	
	private String createQuery(List<String> strings, String operator, String suffix) {
		String query = "(";
		for (int i = 0; i < strings.size(); i++) {
			if (i > 0) {
				query += " " + operator + " ";
			}
			query += strings.get(i) + suffix;
		}
		query += ")";
		
		return query;
	}

}
