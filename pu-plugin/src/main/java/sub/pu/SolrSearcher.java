package sub.pu;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

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
	
	public List<String> cutOutRegestText(int fromLine, int toLine) throws Exception {
		String query = "id:[" + fromLine + " TO " + toLine + "}";
		SolrQuery solrQuery = new SolrQuery(query);
		QueryResponse response = solr.query("pu", solrQuery);
		SolrDocumentList solrResults = response.getResults();
		List<String> results = new ArrayList<>();
		for (int i = 0; i < solrResults.getNumFound(); i++) {
			String line = (String)solrResults.get(i).getFieldValue("line");
			results.add(line);
		}

		return results;
	}
}
