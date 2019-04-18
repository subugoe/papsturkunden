package sub.pu;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

public class RegestExtractor {

	private SolrClient solr;
	private Set<String> mappings = new HashSet<>();
	private UmlautWordMapper mapper;
	private List<String> results = new ArrayList<>();
	private PrintWriter writer;
	
	public static void main(String[] args) throws Exception {
		new RegestExtractor().execute(args);
	}

	public RegestExtractor() throws Exception {
		mappings.add("†:j");
		mappings.add("0:O");
		mappings.add("1:l,I");
		mappings.add("3:\\?");
		mappings.add("5:•f");
		mappings.add("6:€");
		
		writer = new PrintWriter(new FileWriter("/home/dennis/papsturkunden/search-results.txt"));
	}
	
	private void execute(String[] args) throws Exception {
		solr = new HttpSolrClient.Builder("http://localhost:8983/solr").build();
		
		String file = FileUtils.readFileToString(new File("/home/dennis/papsturkunden/ownClowd/Regesta_pontificum_Romanorum_Germania_Germ_I_Export.csv"), "UTF8");
		String[] lines = file.split("\\n");
		
		for (int i = 1; i < lines.length; i++) {
			processLine(lines[i]);
		}
		
		Collections.sort(results);
		for (int i = 0; i < results.size(); i++) {
			String res = results.get(i);
			System.out.println(res + "    -- " + i);
			writer.println(res);
		}
		writer.close();
	}

	private void processLine(String csvLine) throws Exception {
		String[] lineParts = csvLine.split(",");
		if (lineParts.length < 6)
			return;
//		System.out.println();
//		System.out.println(line);
//		System.out.println("-------------");
		String date = lineParts[1];
		String pope = lineParts[2];
		String regestNumber = lineParts[4].replace("*", "\\*");
		String page = lineParts[5];
		
		mapper = new UmlautWordMapper(mappings);

		List<String> combinations = mapper.createMappings(regestNumber);
		String regestNumberQuery = generateOrQuery(combinations);
		combinations.add(regestNumber.substring(0, 1));
		String regestNumberQueryWithStars = generateOrQueryWithStars(combinations);
		
		String dateFuzzyQuery = generateFuzzyQuery(date);
		String dateExactQuery = generateDateOrQuery(date);
		
		String wholeQuery = "page:" + page + " AND "
				+ "("
					+ "line_spaceless:" + regestNumberQueryWithStars + "^35 OR "
					+ "line:" + regestNumberQuery + "^5 OR "
					+ "line:" + dateFuzzyQuery + "^3 OR "
					+ "line:" + dateExactQuery + " OR "
					+ "next_line:*" + pope.split(" ")[0] + "*"
				+ ")";
//		System.out.println(wholeQuery);
//		System.out.println("-------------");
		
		SolrQuery solrQuery = new SolrQuery(wholeQuery);
		QueryResponse response = solr.query("pu", solrQuery);
		String resultLine = (String)response.getResults().get(0).getFieldValue("line");
		String nextLine = (String)response.getResults().get(0).getFieldValue("next_line");
//		System.out.println(resultLine);
//		System.out.println(nextLine);
		
		String lineNumber = (String)response.getResults().get(0).getFieldValue("id");
		for (int i = lineNumber.length(); i < 5; i++) {
			lineNumber = "0" + lineNumber;
		}
		results.add(lineNumber + ":   " + resultLine + "     ------    " + wholeQuery);
//		results.add(lineNumber + ":   " + csvLine);
//		results.add(lineNumber + ":   " + wholeQuery);
	}
	
	private String generateOrQuery(List<String> combinations) {
		return createQuery(combinations, "OR", "");
	}
	
	private String generateOrQueryWithStars(List<String> combinations) {
		return createQuery(combinations, "OR", "*");
	}
	
	private String generateFuzzyQuery(String date) {
		date = date.replace("(", "").replace(")", "");
		String[] parts = date.split("[ \\.-]+");
		return createQuery(new ArrayList<String>(Arrays.asList(parts)), "AND", "~1");
	}
	
	private String generateDateOrQuery(String date) {
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
