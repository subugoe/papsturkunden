package sub.pu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegestEnricher {

	public void addChapters(List<Regest> regests, String[] chapterLines) {
		for (Regest regest : regests) {
			addChaptersToRegest(regest, chapterLines);
		}
	}

	void addChaptersToRegest(Regest regest, String[] chapterLines) {
		Map<String, String[]> chapterMap = convert(chapterLines);
	}

	private Map<String, String[]> convert(String[] chapterLines) {
		Map<String, String[]> chapterMap = new HashMap<>();
		
		String currentChapter = "";
		String currentSubchapter = "";
		String currentPage = "";
		
		for (String line : chapterLines) {
			String[] lineFields = line.split(";");
			if (lineFields.length == 0) { 
				continue;
			} else if (lineFields.length == 1) {
				currentChapter = lineFields[0];
				currentSubchapter = "";
			} else if (lineFields.length == 3) {
				
			} else {
				throw new RuntimeException("Error in line: " + line);
			}
			System.out.println("- " + lineFields[0]);
		}
		
		return chapterMap;
	}

}
