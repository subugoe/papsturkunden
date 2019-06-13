package sub.pu;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import sub.pu.util.Regex;

public class XmlRegestWriter {
	
	private DateBuilder dateBuilder = new DateBuilder();

	public void write(List<Regest> regests, PrintWriter out) {
		out.println("<?xml version=\"1.0\"?>");
		out.println("<body>");
		
		for (Regest regest : regests) {
			out.println("<text type=\"charter\">");
			//out.println("  <!-- " + regest.date + " -->");
			out.println("  <date_sort value=\"" + dateBuilder.getDate(regest.date) + "\">" + dateBuilder.getYear(regest.date) + "</date_sort>");
			String idno = regest.bookName + ", S. " + regest.page + " n. " + extractDigits(regest.number);
			out.println("  <idno>" + idno + "</idno>");
			out.println("  <pont_bd>" + regest.bookName + "</pont_bd>");
			out.println("  <pont_no>" + regest.page + " n. " + extractDigits(regest.number) + "</pont_no>");
			if (!regest.popeIsAlsoPontifikat) {
				//out.println("  <!--ZEILE1: " + regest.textLines.get(1) + "-->");
			}
			out.println("  <supplier>" + getPopeAndSupplier(regest) + "</supplier>");
			//out.println("  <!--HEADER: " + regest.textLines.get(0).trim() + "-->");
			out.println("  <date_table>" + getPlaceAndDate(regest).replace("<", " ") + "</date_table>");
			out.println("  <initium>" + getInitium(regest) + "</initium>");
			out.println("  <recepit_inst>" + regest.subchapter + "</recepit_inst>");
			out.println("  <diocese>" + regest.chapter + "</diocese>");
			out.println("  <jaffe2>" + regest.jaffe +"</jaffe2>");
			out.println("  <regimp></regimp>");
			int pdfPage = Integer.parseInt(regest.page) + 32;
			out.println("  <doc_regest name=\"Germania Pontificia I (nur OCR).pdf#page=" + pdfPage + "\">" + idno + "</doc_regest>");
			out.println("</text>");
		}
		
		out.println("</body>");
	}
	
	private String getPlaceAndDate(Regest regest) {
		String headerLine = regest.textLines.get(0).trim();
		if (headerLine.length() > 5) {
			return headerLine.substring(5).replaceAll("\\s+", " ").trim();
		}
		return "";
	}

	private String getInitium(Regest regest) {
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
	
	private String extractDigits(String regestNumber) {
		return regestNumber.replaceAll("[†*]", "");
	}

	private String getPopeAndSupplier(Regest regest) {
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
