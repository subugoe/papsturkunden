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

	public String cutOutHeader(List<String> regestLines) {
		String header = regestLines.get(0);
		header = header.replaceAll("\\s+", " ").trim();
		header = header.replace("er.", "cr.");
		header = header.replace("Borna", "Roma");
		return header;
	}
	
	public String cutOutMainPart(List<String> regestLines) {
		String mainPart = "";
		for (int i = 1; i < regestLines.size(); i++) {
			String line = regestLines.get(i);
			if (beginsWrittenRecord(line) || beginsComment(line)) {
				break;
			}
			if (i == 1) {
				line = combineWhitespacedWords(line);
			}
			mainPart += prepareLine(line);
		}
		return mainPart;
	}
	
	private String prepareLine(String line) {
		line = line.replaceAll("\\s+", " ").trim();
		line = line.replace("— Heg.", "— Reg.");
		line = line.replace("Land, in", "Laud. in");
		line = line.replace("Begg.", "Regg.");
		line = line.replace(". — Edel.", ". — Edd.");
		line = line.replace(". — Beg.", ". — Reg.");
		line = line.replace(" E g o ", " Ego ");
		line = line.replace(" Mon. Genn. ", " Mon. Germ. ");
		line = line.replace("Urkimdenbuch", "Urkundenbuch");
		if (line.endsWith("-")) {
			line = line.replaceFirst("-$", "");
		} else {
			line += "\n";
		}
		return line;
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
				writtenRecord += prepareLine(line);
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
				comment += prepareLine(line);
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
		String header = cutOutHeader(regest.textLines);
		int firstWhitespace = header.indexOf(" ");
		if (firstWhitespace > 0) {
			return header.substring(firstWhitespace).replace("<", " ").trim();
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
			line = combineWhitespacedWords(line);
						
//			System.out.println(line);
//			System.out.println(regest.textLines.get(2));
//			System.out.println();
			return parseSupplier(line) + " (" + regest.pope + ")";
		}
	}
	
	String combineWhitespacedWords(String line) {
		String result = "";
		if (line.length() <= 3) {
			return line;
		}
		for (int i = 0; i < line.length(); i++) {
			String currentChar = "" + line.charAt(i);
			result += currentChar;

			if (i + 3 >= line.length()) {
				continue;
			}
			
			boolean nextIsWhitespace = ("" + line.charAt(i + 1)).matches(" ");
			String candidate = "" + line.charAt(i + 2);
			boolean afterCandidateIsWhitespace = ("" + line.charAt(i + 3)).matches(" ");
			
			boolean acceptedChars = currentChar.matches("[a-zA-Z\\[(]") && candidate.matches("[a-zA-Z\\])]");
			boolean forbiddenOrder = currentChar.matches("[a-z]") && candidate.matches("[A-Z]");
			
			if (nextIsWhitespace && afterCandidateIsWhitespace && acceptedChars && !forbiddenOrder) {
				i++;
			}
		}
		return result;
	}
	
//	String combineWhitespacedWords(String line, String filterRegex) {
//		List<Integer> lonelyLetterIndexes = new ArrayList<>();
//		for (int i = 0; i < line.length(); i++) {
//			if (isLonelyLetter(i, line, filterRegex)) {
//				lonelyLetterIndexes.add(i);
//			}
//		}
//		for (int i = lonelyLetterIndexes.size() - 1; i > 0; i--) {
//			int currentIndex = lonelyLetterIndexes.get(i);
//			int precedingIndex = lonelyLetterIndexes.get(i - 1);
//			if (currentIndex - precedingIndex == 2) {
//				line = removeCharacterFromLine(currentIndex - 1, line);
//			}
//		}
//		return line;
//	}
//
//	private String removeCharacterFromLine(int i, String line) {
//		StringBuilder sb = new StringBuilder(line);
//		sb.deleteCharAt(i);
//		return sb.toString();
//	}
//
//	private boolean isLonelyLetter(int i, String line, String filterRegex) {
//		String current = line.charAt(i) + "";
//		if (current.matches(filterRegex)) {
//			boolean openToRight = line.length() > i + 1;
//			boolean openToLeft = i > 0;
//			if (openToRight && openToLeft) {
//				if (isWhitespace(i - 1, line) && isWhitespace(i + 1, line)) {
//					return true;
//				}
//			} else if (openToRight && isWhitespace(i + 1, line)) {
//				return true;
//			} else if (openToLeft && isWhitespace(i - 1, line)) {
//				return true;
//			}
//		}
//		
//		return false;
//	}
//
//	private boolean isWhitespace(int index, String line) {
//		return ("" + line.charAt(index)).matches(" ");
//	}
	
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
