package sub.pu;

import java.io.PrintWriter;
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

}
