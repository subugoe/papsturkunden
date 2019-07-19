package sub.pu;

import java.util.ArrayList;
import java.util.List;

import sub.pu.data.Regest;
import sub.pu.util.Regex;

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
	
	public String getPlaceAndDate(Regest regest) {
		String headerLine = regest.textLines.get(0).trim();
		if (headerLine.length() > 5) {
			return headerLine.substring(5).replaceAll("\\s+", " ").replace("<", " ").trim();
		} else {
			return "";
		}
	}

	public String getInitium(Regest regest) {
		String oneLineRegest = "";
		for (int i = 1; i < regest.textLines.size(); i++) {
			oneLineRegest += regest.textLines.get(i) + " ";
		}
		String possibleInitium = Regex.extractFirst(" —([^\\.]+).", oneLineRegest).trim();
		if (possibleInitium.length() > 4) {
			return possibleInitium;
		} else {
			return "";
		}
	}
	
	public String getPopeAndSupplier(Regest regest) {
		if (regest.popeIsAlsoPontifikat) {
			return regest.pope + " (" + regest.pope + ")";
		} else {
			String line = regest.textLines.get(1);
			line = combineWhitespacedWords(line, "[IVX]");
			line = combineWhitespacedWords(line, "[()\\[\\]a-zA-Z]");
						
//			System.out.println(line);
//			System.out.println(regest.textLines.get(2));
//			System.out.println();
			return parseSupplier(line) + " (" + regest.pope + ")";
		}
	}
	
	String combineWhitespacedWords(String line, String filterRegex) {
		List<Integer> lonelyLetterIndexes = new ArrayList<>();
		for (int i = 0; i < line.length(); i++) {
			if (isLonelyLetter(i, line, filterRegex)) {
				lonelyLetterIndexes.add(i);
			}
		}
		for (int i = lonelyLetterIndexes.size() - 1; i > 0; i--) {
			int current = lonelyLetterIndexes.get(i);
			int preceding = lonelyLetterIndexes.get(i - 1);
			if (current - preceding == 2) {
				line = removeCharacterFromLine(current - 1, line);
			}
		}
		return line;
	}

	private String removeCharacterFromLine(int i, String line) {
		StringBuilder sb = new StringBuilder(line);
		sb.deleteCharAt(i);
		return sb.toString();
	}

	private boolean isLonelyLetter(int i, String line, String filterRegex) {
		String current = line.charAt(i) + "";
		if (current.matches(filterRegex)) {
			boolean openToRight = line.length() > i + 1;
			boolean openToLeft = i > 0;
			if (openToRight && openToLeft) {
				if (isWhitespace(i - 1, line) && isWhitespace(i + 1, line)) {
					return true;
				}
			} else if (openToRight && isWhitespace(i + 1, line)) {
				return true;
			} else if (openToLeft && isWhitespace(i - 1, line)) {
				return true;
			}
		}
		
		return false;
	}

	private boolean isWhitespace(int index, String line) {
		return ("" + line.charAt(index)).matches(" ");
	}
	
	String parseSupplier(String line) {
		String[] words = line.split("\\s+");
		String supplier = "";
		if (words.length > 3) {
			if (!words[2].matches("[IVX]+")) {
				supplier += words[2];
			}
			for (int i = 3; i < words.length; i++) {
				String current = words[i];
				if (current.endsWith(":")) {
					supplier += " " + current.substring(0, current.length() - 1);
					break;
				}
				supplier += " " + current;
			}
		}

		return supplier;
	}

}
