package sub.pu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegestExtractor {

	private List<String> results = new ArrayList<>();
	private List<Milestone> milestones = new ArrayList<>();
	private SolrSearcher searcher = new SolrSearcher();
	
	public List<String> processLines(String[] csvLines) throws Exception {
		for (int i = 1; i < csvLines.length; i++) {
			processLine(csvLines[i], i);
		}

		Collections.sort(milestones, new Milestone.BookOrderComparator());
		for (Milestone m : milestones) {
			String lineNumber = m.lineNumber + "";
			for (int i = lineNumber.length(); i < 5; i++) {
				lineNumber = "0" + lineNumber;
			}

			results.add(lineNumber + ":   " + m.lineContents + "     ------    " + m.regestInfo.pope /*+ m.solrQuery*/);
		}
		
		return results;
	}
	
	private void processLine(String csvLine, int tocIndex) throws Exception {
		String[] lineParts = csvLine.split(",");
		if (lineParts.length < 6)
			return;
		
		RegestInfo regestInfo = new RegestInfo();
		regestInfo.date = lineParts[1];
		regestInfo.pope = lineParts[2];
		regestInfo.number = lineParts[4].replace("*", "\\*");
		regestInfo.page = lineParts[5];
		regestInfo.tocIndex = tocIndex;
		
		if (lineParts[4].length() > 5 || regestInfo.page.length() > 3) {
			System.out.println(csvLine);
			return;
		}
		
		Milestone result = searcher.findRegestBeginning(regestInfo);
		milestones.add(result);
	}
	

}
