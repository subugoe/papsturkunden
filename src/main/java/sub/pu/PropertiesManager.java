package sub.pu;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesManager {

	public File mainDirectory;
	public String pdfFileName;
	public String bookShortName;
	public File inputDirectory;
	public String input_bookFileName;
	public File input_tableOfContents;
	public File input_chapters;
	public File input_jaffe;
	public File input_endMilestones;
	public File output_xml;
	public File output_solrxml;
	public File output_searchResults;
	public String internalSolrUrl;
	public String internalSolrTempDirectory;
	public String resultsSolrUrl;
	public File resultsSolrTempDirectory;
	public int pdfToRealPageOffset;
	public boolean oneBookMode = false;
	
	public void initAllProperties(String[] args) throws Exception {
		if (args.length == 0) {
			oneBookMode = true;
		}
		Properties bookSpecificProps = new Properties();
		if (oneBookMode) {
			bookSpecificProps.load(new FileInputStream("config-one-book.txt"));
		} else {
			bookSpecificProps.load(new FileInputStream(args[0]));
		}
		mainDirectory = new File((String) bookSpecificProps.get("mainDirectory"));
		pdfFileName = (String) bookSpecificProps.get("pdfFileName");
		bookShortName = (String) bookSpecificProps.get("bookShortName");
		
		Properties generalProps = new Properties();
		generalProps.load(new FileInputStream("config-general.txt"));
		inputDirectory = new File(mainDirectory, (String) generalProps.get("inputSubDirectory"));
		input_bookFileName = (String) generalProps.get("input_book");
		input_tableOfContents = new File(inputDirectory, (String) generalProps.get("input_tableOfContents"));
		input_chapters = new File(inputDirectory, (String) generalProps.get("input_chapters"));
		input_jaffe = new File(inputDirectory, (String) generalProps.get("input_jaffe"));
		input_endMilestones = new File(inputDirectory, (String) generalProps.get("input_endMilestones"));
		
		resultsSolrUrl = (String) generalProps.get("resultsSolrUrl");
		resultsSolrTempDirectory = new File((String) generalProps.get("output_solrXmlDirectory"));
		output_solrxml = new File(resultsSolrTempDirectory, bookShortName + "_solr.xml");
		
		output_xml = new File(mainDirectory, (String) generalProps.get("output_xml"));
		output_searchResults = new File(mainDirectory, (String) generalProps.get("output_searchResults"));
		internalSolrUrl = (String) generalProps.get("internalSolrUrl");
		internalSolrTempDirectory = new File(mainDirectory, "solrxml").getAbsolutePath();
	}

	public void initPageOffset(String firstTocLine) {
		String lastField = firstTocLine.substring(firstTocLine.lastIndexOf(",") + 1);
		pdfToRealPageOffset = Integer.parseInt(lastField);
	}

}
