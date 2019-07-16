package sub.pu;

import java.util.List;

public class RegestSplitter {
	
	private int writtenRecordSpacesFrom = 5;
	private int writtenRecordSpacesTo = 9;
	private int commentSpacesFrom = 10;
	private int commentSpacesTo = 16;

	public String cutOutMainPart(List<String> regestLines) {
		String mainPart = "";
		for (int i = 1; i < regestLines.size(); i++) {
			String line = regestLines.get(i);
			int currentSpaces = line.length() - line.replaceAll("^\\s*", "").length();
			if (currentSpaces >= writtenRecordSpacesFrom && currentSpaces <= writtenRecordSpacesTo) {
				break;
			}
			mainPart += line + " ";
		}
		return mainPart;
	}

	public String cutOutWrittenRecord(List<String> regestLines) {
		String writtenRecord = "";
		boolean hasBegun = false;
		for (int i = 1; i < regestLines.size(); i++) {
			String line = regestLines.get(i);
			int currentSpaces = line.length() - line.replaceAll("^\\s*", "").length();
			if (!hasBegun && currentSpaces >= writtenRecordSpacesFrom && currentSpaces <= writtenRecordSpacesTo) {
				hasBegun = true;
			} else if (currentSpaces >= commentSpacesFrom && currentSpaces <= commentSpacesTo) {
				break;
			}
			if (hasBegun) {
				writtenRecord += line + " ";
			}
		}
		return writtenRecord;
	}

	public String cutOutComment(List<String> regestLines) {
		String comment = "";
		boolean hasBegun = false;
		for ( int i = 1; i < regestLines.size(); i++) {
			String line = regestLines.get(i);
			int currentSpaces = line.length() - line.replaceAll("^\\s*", "").length();
			if (!hasBegun && currentSpaces >= commentSpacesFrom && currentSpaces <= commentSpacesTo) {
				hasBegun = true;
			}
			if (hasBegun) {
				comment += line + " ";
			}
		}
		return comment;
	}

	
}
