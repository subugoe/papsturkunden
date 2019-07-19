package sub.pu;

import java.io.PrintWriter;
import java.util.List;

import sub.pu.data.Regest;
import sub.pu.util.DateBuilder;

public class XmlRegestWriter {
	
	private DateBuilder dateBuilder = new DateBuilder();
	private RegestSplitter splitter = new RegestSplitter();

	public void write(List<Regest> regests, PrintWriter out) {
		out.println("<?xml version=\"1.0\"?>");
		out.println("<body>");
		
		for (Regest regest : regests) {
			out.println("<text type=\"charter\">");
			//out.println("  <!-- " + regest.date + " -->");
			out.println("  <date_sort value=\"" + dateBuilder.getDate(regest.date) + "\">" + dateBuilder.getYear(regest.date) + "</date_sort>");
			String idno = regest.bookShortName + ", S. " + regest.page + " n. " + regest.number;
			out.println("  <idno>" + idno + "</idno>");
			out.println("  <pont_bd>" + regest.bookShortName + "</pont_bd>");
			out.println("  <pont_no>" + regest.page + " n. " + regest.number.replaceAll("[†*]", "") + "</pont_no>");
			if (!regest.popeIsAlsoPontifikat) {
				//out.println("  <!--ZEILE1: " + regest.textLines.get(1) + "-->");
			}
			out.println("  <supplier>" + splitter.getPopeAndSupplier(regest) + "</supplier>");
			//out.println("  <!--HEADER: " + regest.textLines.get(0).trim() + "-->");
			out.println("  <date_table>" + splitter.getPlaceAndDate(regest) + "</date_table>");
			out.println("  <initium>" + splitter.getInitium(regest) + "</initium>");
			out.println("  <recepit_inst>" + regest.subchapter + "</recepit_inst>");
			out.println("  <diocese>" + regest.chapter + "</diocese>");
			out.println("  <jaffe2>" + regest.jaffe +"</jaffe2>");
			out.println("  <regimp></regimp>");
			String doc_regest = regest.bookShortName + ", S. " + regest.page + " n. " + regest.number.replaceAll("[†*]", "");
			out.println("  <doc_regest name=\"" + regest.pdfFileName + "#page=" + regest.pdfPage + "\">" + doc_regest + "</doc_regest>");
			out.println("</text>");
		}
		
		out.println("</body>");
	}
	
}
