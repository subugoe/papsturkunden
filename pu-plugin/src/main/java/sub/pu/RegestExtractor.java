package sub.pu;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

public class RegestExtractor {

	private SolrClient solr;
	
	public static void main(String[] args) throws Exception {
		new RegestExtractor().execute(args);
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
		
		System.out.println(line);
		String date = lineParts[1];
		String pope = lineParts[2];
		String regestNumber = lineParts[4];
		String page = lineParts[5];
		System.out.println(regestNumber);
		
		SolrQuery solrQuery = new SolrQuery("text:" + regestNumber + " AND page:" + page);
		QueryResponse response = solr.query("pu", solrQuery);
		String result = (String)response.getResults().get(0).getFieldValue("text");
		System.out.println(result);
	}
}
