package sub.pu;

import java.util.ArrayList;
import java.util.List;

public class RegestExtractor {

	private List<String> results = new ArrayList<>();
	private SolrSearcher searcher = new SolrSearcher();
	
	public List<String> processLines(String[] csvLines) throws Exception {
		for (int i = 1; i < csvLines.length; i++) {
			processLine(csvLines[i]);
		}

		return results;
	}
	
	private void processLine(String csvLine) throws Exception {
		String[] lineParts = csvLine.split(",");
		if (lineParts.length < 6)
			return;
		
		RegestInfo regestInfo = new RegestInfo();
		regestInfo.date = lineParts[1];
		regestInfo.pope = lineParts[2];
		regestInfo.number = lineParts[4].replace("*", "\\*");
		regestInfo.page = lineParts[5];
		
		if (lineParts[4].length() > 5 || regestInfo.page.length() > 3) {
			results.add("00000: " + csvLine);
			return;
		}
		
		String result = searcher.findRegestBeginning(regestInfo);
		results.add(result);
	}
	

}
