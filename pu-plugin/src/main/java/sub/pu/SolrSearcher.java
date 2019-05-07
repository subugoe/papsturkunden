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
	
	public Milestone findRegestBeginning(RegestInfo regestInfo) throws Exception {
		String wholeQuery = new QueryGenerator().generateQuery(regestInfo);
		
		SolrQuery solrQuery = new SolrQuery(wholeQuery);
		QueryResponse response = solr.query("pu", solrQuery);
		String resultLine = (String)response.getResults().get(0).getFieldValue("line");
		
		int lineNumber = (int)response.getResults().get(0).getFieldValue("id");

		Milestone milestone = new Milestone();
		milestone.lineNumber = lineNumber;
		milestone.regestInfo = regestInfo;
		milestone.lineContents = resultLine;
		milestone.solrQuery = wholeQuery;
		
		return milestone;
	}
	
	public Milestone findRegestEnding(String page, String textLine) throws Exception {
		String query = "page:" + page + " AND line_spaceless:\"" + textLine.replaceAll("\\s", "") + "\"";
		SolrQuery solrQuery = new SolrQuery(query);
		QueryResponse response = solr.query("pu", solrQuery);
		int lineNumber = (int)response.getResults().get(0).getFieldValue("id");

		Milestone milestone = new Milestone();
		milestone.isRegestBeginning = false;
		milestone.lineNumber = lineNumber;
		milestone.solrQuery = query;

		return milestone;
	}
}
