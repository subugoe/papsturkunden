package sub.pu;

import java.io.PrintWriter;
import java.util.List;

import sub.pu.data.Regest;

public class SolrXmlRegestWriter {

	public void write(List<Regest> regests, String pdfFileName, String bookName, int pageOffset, PrintWriter out) {
		out.println("<?xml version=\"1.0\"?>");
		out.println("<add>");

		for (Regest regest : regests) {
			out.println("<doc>");
			
			String idno = bookName + ", S. " + regest.page + " n. " + regest.number;
			out.println("<field name='id'>" + idno + "</field>");
			out.println("<field name='page'>" + regest.page + "</field>");
			int pdfPage = Integer.parseInt(regest.page) + pageOffset;
			out.println("<field name='page_pdf'>" + pdfPage + "</field>");
			
			for (String line : regest.textLines) {
				out.println("<field name='orig_text'><![CDATA[" + line + "]]></field>");
			}

			out.println("</doc>");
		}
		
		out.println("</add>");
	}
}
