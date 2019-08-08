package sub.pu;

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
	
	public String get2aCoreRegest(List<String> regestLines) {
		String mainPart = cutOutMainPart(regestLines).replace("\n", " ");
		String coreRegest = Regex.extractFirst("^(.*\\.) —", mainPart);
		if ("".equals(coreRegest))
			return mainPart.trim();
		
		return coreRegest;
	}
	
	public String get2bInfoForWriting(List<String> regestLines) {
		String mainPart = cutOutMainPart(regestLines).replace("\n", " ");
		String coreRegest = get2aCoreRegest(regestLines);
		return mainPart.replace(coreRegest, "").trim();
	}
	
	public String get2b3originalDating(List<String> regestLines) {
		String infoForWriting = get2bInfoForWriting(regestLines);
		int i1 = infoForWriting.indexOf("Dat.");
		int i2 = infoForWriting.indexOf("Scr. p. m.");
		String originalDating = "";
		if (i1 == -1 && i2 == -1) {
			return "";
		} else if (i1 != -1) {
			originalDating = infoForWriting.substring(i1);
		} else if (i2 != -1) {
			originalDating =  infoForWriting.substring(i2);
		} else {
			originalDating =  infoForWriting.substring(Math.min(i1, i2));
		}
		return originalDating.trim();
	}
	
	public String get2b2subscriptions(List<String> regestLines) {
		String infoForWriting = get2bInfoForWriting(regestLines);
		String originalDating = get2b3originalDating(regestLines);
		String withoutDating = infoForWriting.replace(originalDating, "");
		int i1 = withoutDating.indexOf("Ego");
		int i2 = withoutDating.indexOf("Subscr.");
		String subscriptions = "";
		if (i1 == -1 && i2 == -1) {
			return "";
		} else if (i1 != -1) {
			subscriptions = withoutDating.substring(i1);
		} else if (i2 != -1) {
			subscriptions =  withoutDating.substring(i2);
		} else {
			subscriptions =  withoutDating.substring(Math.min(i1, i2));
		}
		return subscriptions.trim();
	}
	
	public String get2b1initium(List<String> regestLines) {
		String infoForWriting = get2bInfoForWriting(regestLines);
		String originalDating = get2b3originalDating(regestLines);
		String subscriptions = get2b2subscriptions(regestLines);
		String withoutTail = infoForWriting.replace(originalDating, "").replace(subscriptions, "");
		return Regex.extractFirst("—([^\\.]+)\\.", withoutTail).trim();
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
