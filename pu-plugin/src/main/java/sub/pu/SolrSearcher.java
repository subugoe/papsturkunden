package sub.pu;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

public class SolrSearcher {

	private SolrClient solr;

	public SolrSearcher() {
		solr = new HttpSolrClient.Builder("http://localhost:8983/solr").build();
	}
	
	public String findRegestBeginning(RegestInfo regestInfo) throws Exception {
		String wholeQuery = new QueryGenerator().generateQuery(regestInfo);
		
		SolrQuery solrQuery = new SolrQuery(wholeQuery);
		QueryResponse response = solr.query("pu", solrQuery);
		String resultLine = (String)response.getResults().get(0).getFieldValue("line");
		
		String lineNumber = "" + response.getResults().get(0).getFieldValue("id");
		for (int i = lineNumber.length(); i < 5; i++) {
			lineNumber = "0" + lineNumber;
		}

		return lineNumber + ":   " + resultLine + "     ------    " + wholeQuery;
	}
}
