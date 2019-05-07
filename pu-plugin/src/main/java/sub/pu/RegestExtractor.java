package sub.pu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegestExtractor {

	private List<Milestone> milestones = new ArrayList<>();
	private SolrSearcher searcher = new SolrSearcher();
	
	public List<Regest> processLines(String[] csvLines, String[] endLines) throws Exception {
		for (int i = 1; i < csvLines.length; i++) {
			processCsvLine(csvLines[i], i);
		}
		
		for (String endLine : endLines) {
			processEndLine(endLine);
		}

		Collections.sort(milestones, new Milestone.BookOrderComparator());
		removeDoubles();
		
		List<Regest> regests = new ArrayList<>();
		for (Milestone m : milestones) {
			
			if (!m.isRegestBeginning) {
				Regest ending = new Regest();
				ending.textLines.add(m.solrQuery);
				ending.bookOrder = m.lineNumber;
				regests.add(ending);
				continue;
			}
			
			Regest regest = new Regest();
			regest.bookOrder = m.lineNumber;
			regest.textLines.add(m.lineContents);
			regest.pope = m.regestInfo.pope;
			
			regests.add(regest);
		}
		
		return regests;
	}
	
	private void processCsvLine(String csvLine, int tocIndex) throws Exception {
		String[] lineParts = csvLine.split(",");
		if (lineParts.length < 6)
			return;
		
		RegestInfo regestInfo = new RegestInfo();
		regestInfo.date = lineParts[1];
		regestInfo.pope = lineParts[2];
		regestInfo.number = lineParts[4].replace("*", "\\*");
		regestInfo.page = lineParts[5];
		regestInfo.tocIndex = tocIndex;
		regestInfo.csvLine = csvLine;
		
		if (lineParts[4].length() > 5 || regestInfo.page.length() > 3) {
			System.out.println("Bad syntax: " + csvLine);
			return;
		}
		
		Milestone result = searcher.findRegestBeginning(regestInfo);		
		milestones.add(result);
	}
	
	private void processEndLine(String endLine) throws Exception {
		String page = endLine.substring(0, endLine.indexOf(','));
		String textLine = endLine.substring(endLine.indexOf(',') + 1);
		
		Milestone result = searcher.findRegestEnding(page, textLine);
		milestones.add(result);
	}

	private void removeDoubles() {
		for (int i = milestones.size() - 1; i >= 1; i--) {
			Milestone current = milestones.get(i);
			if (current.lineNumber == milestones.get(i - 1).lineNumber) {
				System.out.println("Double entry: " + current.regestInfo.csvLine + " line " + current.lineNumber);
				milestones.remove(i);
			}
		}
	}
	

}
