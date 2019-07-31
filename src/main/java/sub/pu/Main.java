package sub.pu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sub.ent.api.ImporterStepUpload;
import sub.pu.data.Regest;
import sub.pu.importing.ImporterStepConvert;

public class Main {

	private File mainDirectory;
	private String pdfFileName;
	private String bookShortName;
	private File inputDirectory;
	private String input_bookFileName;
	private File input_tableOfContents;
	private File input_chapters;
	private File input_jaffe;
	private File input_endMilestones;
	private File output_xml;
	private File output_solrxml;
	private File output_searchResults;
	private String internalSolrUrl;
	private String internalSolrTempDirectory;
	private String resultsSolrUrl;
	private File resultsSolrTempDirectory;
	private int pdfToRealPageOffset;
	
	boolean oneBookMode = false;
	
	public static void main(String[] args) throws Exception {
		new Main().execute(args);
	}
	
	private void execute(String[] args) throws Exception {
		if (args.length == 0) {
			oneBookMode = true;
		}
		
		initAllProperties(args);

		String tocCsvFile = FileUtils.readFileToString(input_tableOfContents, "UTF8");
		String[] tocLines = tocCsvFile.split("\\n");
		initPageOffset(tocLines[0]);
		importIntoInternalSolr();
				
		PrintWriter writer = new PrintWriter(new FileWriter(output_searchResults));
		PrintWriter writerForXml = new PrintWriter(new FileWriter(output_xml));
		PrintWriter writerForSolrXml = new PrintWriter(new FileWriter(output_solrxml));
		String endMilestonesFile = FileUtils.readFileToString(input_endMilestones, "UTF8");
		String[] endLines = endMilestonesFile.split("\\n");
		
		String chapterFile = FileUtils.readFileToString(input_chapters, "UTF8");
		String[] chapterLines = chapterFile.split("\\n");
		
		String jaffeFile = FileUtils.readFileToString(input_jaffe, "UTF8");
		String[] jaffeLines = jaffeFile.split("\\n");
		
		RegestExtractor extractor = new RegestExtractor();
		List<Regest> regests = extractor.processLines(tocLines, endLines);
		
		RegestEnricher enricher = new RegestEnricher();
		enricher.addChapters(regests, chapterLines);
		enricher.addJaffes(regests, jaffeLines);
		enricher.addMetadata(regests, pdfFileName, bookShortName, pdfToRealPageOffset);
		
		XmlRegestWriter regestWriter = new XmlRegestWriter();
		regestWriter.write(regests, writerForXml);
		writerForXml.close();
		
		SolrXmlRegestWriter solrWriter = new SolrXmlRegestWriter();
		solrWriter.write(regests, writerForSolrXml);
		writerForSolrXml.close();
		
		Set<Integer> whitespaces = new HashSet<>();
		for (Regest regest : regests) {
			String lineNumber = regest.bookOrder + "";
			for (int i = lineNumber.length(); i < 5; i++) {
				lineNumber = "0" + lineNumber;
			}

			writer.println(lineNumber + ":   " + regest.textLines.get(0) + "     ------    " + regest.pope);
			
//			writer.println("Query: ");
//			writer.println(regest.queryForDebugging);
			
//			writer.println("Seite: " + regest.page);
//			writer.println("Regestnummer: " + regest.number);
//			writer.println("Datum: " + regest.date);
//			writer.println("Papst: " + regest.pope);
//			writer.println();
//			for (String textLine : regest.textLines) {
//				writer.println(textLine);
//			}
//			writer.println("-------------------------------------------------------------------------------------");
//			RegestSplitter splitter = new RegestSplitter();
//			writer.println("--- Main:");
//			writer.println(splitter.cutOutMainPart(regest.textLines));
//			writer.println("--- Überlieferung:");
//			writer.println(splitter.cutOutWrittenRecord(regest.textLines));
//			writer.println("--- Sachkommentar:");
//			writer.println(splitter.cutOutComment(regest.textLines));
			
			for (String textLine : regest.textLines) {
				if (textLine.startsWith("  ")) {
					String shortLine = textLine.replaceFirst("\\s+", "");
					int whitePrefix = textLine.length() - shortLine.length();
//					writer.println(whitePrefix);
					whitespaces.add(whitePrefix);
				}
			}

		}
//		System.out.println(whitespaces);
		writer.close();
		
		if (oneBookMode) {
			importIntoResultsSolr();
		}
	}

	private void initAllProperties(String[] args) throws Exception {
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

	private void initPageOffset(String firstTocLine) {
		String lastField = firstTocLine.substring(firstTocLine.lastIndexOf(",") + 1);
		pdfToRealPageOffset = Integer.parseInt(lastField);
	}

	private void importIntoInternalSolr() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("gitDir", inputDirectory.getAbsolutePath());
		params.put("bookFileName", input_bookFileName);
		params.put("solrXmlDir", internalSolrTempDirectory);
		params.put("solrUrl", internalSolrUrl);
		params.put("solrImportCore", "pu");
		params.put("pageOffset", "" + pdfToRealPageOffset);
		System.out.println("    Importing into internal Solr (" + internalSolrUrl + ")");
		new ImporterStepConvert().execute(params);
		new ImporterStepUpload().execute(params);
	}

	private void importIntoResultsSolr() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("solrXmlDir", resultsSolrTempDirectory.getAbsolutePath());
		params.put("solrUrl", resultsSolrUrl);
		params.put("solrImportCore", "pu");
		
		System.out.println();
		System.out.println("    Importing into Solr with results (" + resultsSolrUrl + ")");
		new ImporterStepUpload().execute(params);
	}

}
