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
			if (beginsWrittenRecord(line) || beginsComment(line)) {
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
			if (!hasBegun && beginsWrittenRecord(line)) {
				hasBegun = true;
			} else if (beginsComment(line)) {
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
		for (int i = 1; i < regestLines.size(); i++) {
			String line = regestLines.get(i);
			if (!hasBegun && beginsComment(line)) {
				hasBegun = true;
			}
			if (hasBegun) {
				comment += line + " ";
			}
		}
		return comment;
	}
	
	private boolean beginsWrittenRecord(String line) {
		return isBlockBeginning(line, writtenRecordSpacesFrom, writtenRecordSpacesTo);
	}

	private boolean beginsComment(String line) {
		return isBlockBeginning(line, commentSpacesFrom, commentSpacesTo);
	}

	private boolean isBlockBeginning(String line, int spacesFrom, int spacesTo) {
		int leadingSpaces = line.length() - line.replaceAll("^\\s*", "").length();
		return leadingSpaces >= spacesFrom && leadingSpaces <= spacesTo;
	}
}
