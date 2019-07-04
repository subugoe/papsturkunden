package sub.pu;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sub.pu.data.Regest;

public class Main {

	public static void main(String[] args) throws Exception {
		new Main().execute(args);
	}
	
	private void execute(String[] args) throws Exception {
		PrintWriter writer = new PrintWriter(new FileWriter("/home/dennis/papsturkunden_germania3/search-results.txt"));
		PrintWriter writerForXml = new PrintWriter(new FileWriter("/home/dennis/papsturkunden_germania3/germania3.xml"));
		String tocCsvFile = FileUtils.readFileToString(new File("/home/dennis/papsturkunden_germania3/TOC.csv"), "UTF8");
		String[] tocLines = tocCsvFile.split("\\n");
		String endMilestonesFile = FileUtils.readFileToString(new File("/home/dennis/papsturkunden_germania3/end-milestones.txt"), "UTF8");
		String[] endLines = endMilestonesFile.split("\\n");
		
		String chapterFile = FileUtils.readFileToString(new File("/home/dennis/papsturkunden_germania3/chapters.csv"), "UTF8");
		String[] chapterLines = chapterFile.split("\\n");
		
		String jaffeFile = FileUtils.readFileToString(new File("/home/dennis/papsturkunden_germania3/jaffe.csv"), "UTF8");
		String[] jaffeLines = jaffeFile.split("\\n");
		
		RegestExtractor extractor = new RegestExtractor();
		List<Regest> regests = extractor.processLines(tocLines, endLines);
		
		RegestEnricher enricher = new RegestEnricher();
		enricher.addChapters(regests, chapterLines);
		enricher.addJaffes(regests, jaffeLines);
		
		XmlRegestWriter regestWriter = new XmlRegestWriter();
		regestWriter.write(regests, writerForXml);
		writerForXml.close();
		
		for (Regest regest : regests) {
			String lineNumber = regest.bookOrder + "";
			for (int i = lineNumber.length(); i < 5; i++) {
				lineNumber = "0" + lineNumber;
			}

			writer.println(lineNumber + ":   " + regest.textLines.get(0) + "     ------    " + regest.pope);

//			writer.println("Seite: " + regest.page);
//			writer.println("Regestnummer: " + regest.number);
//			writer.println("Datum: " + regest.date);
//			writer.println("Papst: " + regest.pope);
//			writer.println();
//			for (String textLine : regest.textLines) {
//				writer.println(textLine);
//			}
//			writer.println("-------------------------------------------------------------------------------------");
		}
		writer.close();
	}

}
