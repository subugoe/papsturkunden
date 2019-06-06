package sub.pu;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class XmlRegestWriter {
	
	private DateBuilder dateBuilder = new DateBuilder();

	public void write(List<Regest> regests, PrintWriter out) {
		out.println("<?xml version=\"1.0\"?>");
		out.println("<body>");
		
		for (Regest regest : regests) {
			out.println("<text type=\"charter\">");
			out.println("  <!-- " + regest.date + " -->");
			out.println("  <date_sort value=\"" + dateBuilder.getDate(regest.date) + "\">" + dateBuilder.getYear(regest.date) + "</date_sort>");
			out.println("  <idno>" + regest.bookName + ", S. " + regest.page + " n. " + extractDigits(regest.number) + "</idno>");
			out.println("  <pont_bd>" + regest.bookName + "</pont_bd>");
			out.println("  <pont_no>" + regest.page + " n. " + extractDigits(regest.number) + "</pont_no>");
			out.println("  <supplier>" + getPopeAndSupplier(regest) + "</supplier>");
			out.println("  <date_table></date_table>");
			out.println("  <initium></initium>");
			out.println("  <recepit_inst></recepit_inst>");
			out.println("  <diocese></diocese>");
			out.println("  <jaffe2></jaffe2>");
			out.println("  <regimp></regimp>");
			out.println("  <doc_regest></doc_regest>");
			out.println("</text>");
		}
		
		out.println("</body>");
	}
	
	private String extractDigits(String regestNumber) {
		return regestNumber.replaceAll("[†*]", "");
	}

	private String getPopeAndSupplier(Regest regest) {
		if (regest.popeIsAlsoPontifikat) {
			return regest.pope + " (" + regest.pope + ")";
		} else {
			String line = regest.textLines.get(1);
			return "== " + line;
		}
	}
	
//	String combineWhitespacedWords(String line) {
//		String result = line.replaceAll("(([^a-z]|^)[a-z]) ([a-z]([^a-z]|$))", "$1" + "x" + "$3");
//		//result = result.replaceAll("(x[a-z]) ([a-z]x?)", "$1" + "x" + "$2");
//		return result;
//		//return result.replace("x", "");
//	}
	
	String combineWhitespacedWords(String line) {
		List<Integer> lonelyLetterIndexes = new ArrayList<>();
		for (int i = 0; i < line.length(); i++) {
			if (isLonelyLetter(i, line)) {
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
		System.out.println(lonelyLetterIndexes);
		return line;
	}

	private String removeCharacterFromLine(int i, String line) {
		StringBuilder sb = new StringBuilder(line);
		sb.deleteCharAt(i);
		return sb.toString();
	}

	private boolean isLonelyLetter(int i, String line) {
		String current = line.charAt(i) + "";
		if (current.matches("[a-z]")) {
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

}
