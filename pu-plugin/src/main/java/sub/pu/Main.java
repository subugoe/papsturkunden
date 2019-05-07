package sub.pu;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		new Main().execute(args);
	}
	
	private void execute(String[] args) throws Exception {
		PrintWriter writer = new PrintWriter(new FileWriter("/home/dennis/papsturkunden/search-results.txt"));
		String tocCsvFile = FileUtils.readFileToString(new File("/home/dennis/papsturkunden/ownClowd/Regesta_pontificum_Romanorum_Germania_Germ_I_Export.csv"), "UTF8");
		String[] tocLines = tocCsvFile.split("\\n");
		String endMilestonesFile = FileUtils.readFileToString(new File("/home/dennis/papsturkunden/end-milestones.txt"), "UTF8");
		String[] endLines = endMilestonesFile.split("\\n");
		
		RegestExtractor extractor = new RegestExtractor();
		List<Regest> results = extractor.processLines(tocLines, endLines);
		
		for (Regest regest : results) {
			String lineNumber = regest.bookOrder + "";
			for (int i = lineNumber.length(); i < 5; i++) {
				lineNumber = "0" + lineNumber;
			}

			writer.println(lineNumber + ":   " + regest.textLines.get(0) + "     ------    " + regest.pope);
		}
		writer.close();
	}

}
