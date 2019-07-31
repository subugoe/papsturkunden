package sub.pu;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sub.ent.api.ImporterStepUpload;
import sub.pu.data.Regest;
import sub.pu.internalsolr.ImporterStepConvert;

public class Main_oneBook {

	private PropertiesManager p = new PropertiesManager();
	
	public static void main(String[] args) throws Exception {
		new Main_oneBook().execute(args);
	}
	
	void execute(String[] args) throws Exception {
		
		p.initAllProperties(args);

		String tocCsvFile = FileUtils.readFileToString(p.input_tableOfContents, "UTF8");
		String[] tocLines = tocCsvFile.split("\\n");
		p.initPageOffset(tocLines[0]);
		importIntoInternalSolr();
				
		PrintWriter writer = new PrintWriter(new FileWriter(p.output_searchResults));
		PrintWriter writerForXml = new PrintWriter(new FileWriter(p.output_xml));
		PrintWriter writerForSolrXml = new PrintWriter(new FileWriter(p.output_solrxml));
		String endMilestonesFile = FileUtils.readFileToString(p.input_endMilestones, "UTF8");
		String[] endLines = endMilestonesFile.split("\\n");
		
		String chapterFile = FileUtils.readFileToString(p.input_chapters, "UTF8");
		String[] chapterLines = chapterFile.split("\\n");
		
		String jaffeFile = FileUtils.readFileToString(p.input_jaffe, "UTF8");
		String[] jaffeLines = jaffeFile.split("\\n");
		
		RegestExtractor extractor = new RegestExtractor();
		List<Regest> regests = extractor.processLines(tocLines, endLines);
		
		RegestEnricher enricher = new RegestEnricher();
		enricher.addChapters(regests, chapterLines);
		enricher.addJaffes(regests, jaffeLines);
		enricher.addMetadata(regests, p.pdfFileName, p.bookShortName, p.pdfToRealPageOffset);
		
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
		
		if (p.oneBookMode) {
			importCompleteFolderIntoResultsSolr();
		}
	}

	private void importIntoInternalSolr() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("gitDir", p.inputDirectory.getAbsolutePath());
		params.put("bookFileName", p.input_bookFileName);
		params.put("solrXmlDir", p.internalSolrTempDirectory);
		params.put("solrUrl", p.internalSolrUrl);
		params.put("solrImportCore", "pu");
		params.put("pageOffset", "" + p.pdfToRealPageOffset);
		System.out.println("    Importing into internal Solr (" + p.internalSolrUrl + ")");
		new ImporterStepConvert().execute(params);
		new ImporterStepUpload().execute(params);
	}

	void importCompleteFolderIntoResultsSolr() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("solrXmlDir", p.resultsSolrTempDirectory.getAbsolutePath());
		params.put("solrUrl", p.resultsSolrUrl);
		params.put("solrImportCore", "pu");
		
		System.out.println();
		System.out.println("    ### Importing results into Solr (" + p.resultsSolrUrl + ") ###");
		new ImporterStepUpload().execute(params);
	}

}
