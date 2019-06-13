package sub.pu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegestEnricher {

	private Map<Integer, String[]> chapterMap = new HashMap<>();

	public void addChapters(List<Regest> regests, String[] chapterLines) {
		addInfosToChapterMap(chapterLines, maxRegestPage(regests));
		for (Regest regest : regests) {
			addChaptersToRegest(regest);
		}
	}

	private void addInfosToChapterMap(String[] chapterLines, int maxPage) {
		String currentChapter = "";
		boolean chapterChanged = false;
		String changedChapter = "";
		String currentSubchapter = "";
		int currentPage = 1;
		
		for (String line : chapterLines) {
			String[] lineFields = line.split(";");
			if (lineFields.length == 0 || lineFields.length == 2) { 
				continue;
			} else if (lineFields.length == 1) {
				chapterChanged = true;
				changedChapter = lineFields[0];
			} else if (lineFields.length == 3) {
				int pageFrom = currentPage;
				int pageTo = Integer.parseInt(lineFields[2]);
				addToChapterMap(pageFrom, pageTo, currentChapter, currentSubchapter);
				currentSubchapter = lineFields[1];
				currentPage = pageTo;
				if (chapterChanged) {
					chapterChanged = false;
					currentChapter = changedChapter;
				}
			} else {
				throw new RuntimeException("Error in line (length " + lineFields.length + "): " + line);
			}
		}
		addToChapterMap(currentPage, maxPage + 1, currentChapter, currentSubchapter);
	}

	private int maxRegestPage(List<Regest> regests) {
		List<Integer> allPages = new ArrayList<>();
		for (Regest regest : regests) {
			Integer page = Integer.valueOf(regest.page);
			allPages.add(page);
		}
		return Collections.max(allPages);
	}

	private void addToChapterMap(int pageFrom, int pageTo, String currentChapter, String currentSubchapter) {
		for (int i = pageFrom; i < pageTo; i++) {
			chapterMap.put(i, new String[] {currentChapter, currentSubchapter});
		}
	}

	private void addChaptersToRegest(Regest regest) {
		Integer regestPage = Integer.valueOf(regest.page);
		regest.chapter = chapterMap.get(regestPage)[0];
		regest.subchapter = chapterMap.get(regestPage)[1];
	}

	public void addJaffes(List<Regest> regests, String[] jaffeLines) {
		Map<String, String> jaffeMap = new HashMap<>();
		for (String line : jaffeLines) {
			String[] lineFields = line.split(",");
			if (lineFields.length == 2 && !"".equals(lineFields[0])) {
				jaffeMap.put(lineFields[0], lineFields[1]);
			}
		}
		for (Regest regest : regests) {
			if (regest.csvLine.endsWith("R")) {
				String keyNumber = regest.csvLine.split(",")[0];
				regest.jaffe = jaffeMap.get(keyNumber);
			}
		}
	}

}
