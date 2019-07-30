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
	private String solrTempDirectory;
	private int pdfToRealPageOffset;
	
	public static void main(String[] args) throws Exception {
		new Main().execute(args);
	}
	
	private void execute(String[] args) throws Exception {
		initAllProperties();

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
		System.out.println(whitespaces);
		writer.close();
	}

	private void initAllProperties() throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream("config.txt"));
		mainDirectory = new File((String) props.get("mainDirectory"));
		pdfFileName = (String) props.get("pdfFileName");
		bookShortName = (String) props.get("bookShortName");
		inputDirectory = new File(mainDirectory, (String) props.get("inputSubDirectory"));
		input_bookFileName = (String) props.get("input_book");
		input_tableOfContents = new File(inputDirectory, (String) props.get("input_tableOfContents"));
		input_chapters = new File(inputDirectory, (String) props.get("input_chapters"));
		input_jaffe = new File(inputDirectory, (String) props.get("input_jaffe"));
		input_endMilestones = new File(inputDirectory, (String) props.get("input_endMilestones"));
		output_xml = new File(mainDirectory, (String) props.get("output_xml"));
		output_solrxml = new File(mainDirectory, bookShortName + "_solr.xml");
		output_searchResults = new File(mainDirectory, (String) props.get("output_searchResults"));
		internalSolrUrl = (String) props.get("internalSolrUrl");
		solrTempDirectory = new File(mainDirectory, "solrxml").getAbsolutePath();
	}

	private void initPageOffset(String firstTocLine) {
		String lastField = firstTocLine.substring(firstTocLine.lastIndexOf(",") + 1);
		pdfToRealPageOffset = Integer.parseInt(lastField);
	}

	private void importIntoInternalSolr() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("gitDir", inputDirectory.getAbsolutePath());
		params.put("bookFileName", input_bookFileName);
		params.put("solrXmlDir", solrTempDirectory);
		params.put("solrUrl", internalSolrUrl);
		params.put("solrImportCore", "pu");
		params.put("pageOffset", "" + pdfToRealPageOffset);
		
		new ImporterStepConvert().execute(params);
		new ImporterStepUpload().execute(params);
	}

}
