package sub.pu;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	public static void main(String[] args) throws Exception {
		new RegestExtractor().execute(args);
	}

	public RegestExtractor() {
		mappings.add("†:j");
		mappings.add("1:l");
		mappings.add("3:5");
	}
	
	private void execute(String[] args) throws Exception {
		solr = new HttpSolrClient.Builder("http://localhost:8983/solr").build();
		
		String file = FileUtils.readFileToString(new File("/home/dennis/papsturkunden/ownClowd/Regesta_pontificum_Romanorum_Germania_Germ_I_Export.csv"), "UTF8");
		String[] lines = file.split("\\n");
		
		for (int i = 1; i < lines.length; i++) {
			processLine(lines[i]);
		}
		
	}

	private void processLine(String line) throws Exception {
		String[] lineParts = line.split(",");
		if (lineParts.length < 6)
			return;
		System.out.println();
		System.out.println(line);
		System.out.println("-------------");
		String date = lineParts[1];
		String pope = lineParts[2];
		String regestNumber = lineParts[4].replace("*", "\\*");
		String page = lineParts[5];
		
		mapper = new UmlautWordMapper(mappings);

		List<String> combinations = mapper.createMappings(regestNumber);
		String regestNumberQuery = generateOrQuery(combinations);
		String regestNumberQueryWithStars = generateOrQueryWithStars(combinations);
		
		String dateFuzzyQuery = generateFuzzyQuery(date);
		String dateExactQuery = generateDateOrQuery(date);
		
		String wholeQuery = "page:" + page + " AND "
				+ "("
					+ "text_spaceless:" + regestNumberQueryWithStars + "^50 OR "
					+ "text:" + regestNumberQuery + " OR "
					+ "text:" + dateFuzzyQuery + " OR "
					+ "text:" + dateExactQuery + " OR "
					+ "text_spaceless:*" + pope.split(" ")[0] + "*"
				+ ")";
		System.out.println(wholeQuery);
		System.out.println("-------------");
		
		SolrQuery solrQuery = new SolrQuery(wholeQuery);
		QueryResponse response = solr.query("pu", solrQuery);
		String result = (String)response.getResults().get(0).getFieldValue("text");
		System.out.println(result);
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
