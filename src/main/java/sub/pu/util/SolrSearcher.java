package sub.pu.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import sub.pu.data.Milestone;
import sub.pu.data.RegestInfo;

public class SolrSearcher {

	private SolrClient solr;
	private String solrUrl = "http://localhost:8983/solr";
	private String solrCore = "pu";

	public SolrSearcher() {
		solr = new HttpSolrClient.Builder(solrUrl).build();
	}
	
	public Milestone findRegestBeginning(RegestInfo regestInfo) throws Exception {
		String wholeQuery = new QueryGenerator().generateQuery(regestInfo);
		SolrDocumentList results = askSolr(wholeQuery);
		
		String resultLine = (String)results.get(0).getFieldValue("line");
		String bookName = (String)results.get(0).getFieldValue("book");
		int lineNumber = (int)results.get(0).getFieldValue("id");

		Milestone milestone = new Milestone();
		milestone.bookName = bookName;
		milestone.lineNumber = lineNumber;
		milestone.regestInfo = regestInfo;
		milestone.lineContents = resultLine;
		milestone.solrQuery = wholeQuery;
		
		return milestone;
	}
	
	private SolrDocumentList askSolr(String query) throws Exception {
		SolrQuery solrQuery = new SolrQuery(query);
		QueryResponse response = solr.query(solrCore, solrQuery);
		return response.getResults();
	}
	
	public Milestone findRegestEnding(String page, String textLine) throws Exception {
		String query = "page:" + page + " AND line_spaceless:\"" + textLine.replaceAll("\\s", "") + "\"";
		SolrDocumentList results = askSolr(query);
		int lineNumber = (int)results.get(0).getFieldValue("id");

		Milestone milestone = new Milestone();
		milestone.isRegestBeginning = false;
		milestone.lineNumber = lineNumber;
		milestone.solrQuery = query;

		return milestone;
	}
	
	public List<String> cutOutRegestText(int fromLine, int toLine) throws Exception {
		String query = "id:[" + fromLine + " TO " + toLine + "}";
		SolrDocumentList solrResults = askSolr(query);
		List<String> listResults = new ArrayList<>();
		for (int i = 0; i < solrResults.getNumFound(); i++) {
			String line = (String)solrResults.get(i).getFieldValue("line");
			listResults.add(line);
		}

		return listResults;
	}
}
