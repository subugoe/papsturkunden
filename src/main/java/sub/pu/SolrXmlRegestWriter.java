package sub.pu;

import java.io.PrintWriter;
import java.util.List;

import sub.pu.data.Regest;
import sub.pu.util.DateBuilder;

public class SolrXmlRegestWriter {

	private DateBuilder dateBuilder = new DateBuilder();
	private RegestSplitter splitter = new RegestSplitter();

	public void write(List<Regest> regests, PrintWriter out) {
		out.println("<?xml version=\"1.0\"?>");
		out.println("<add>");

		for (Regest regest : regests) {
			out.println("<doc>");
			
			out.println("<field name='bd'>" + regest.bookShortName.replaceAll("[ \\.]+", "").toLowerCase() + "</field>");
			String idno = regest.bookShortName + ", S. " + regest.page + " n. " + regest.number;
			out.println("<field name='idno'>" + idno + "</field>");
			out.println("<field name='date_sort'>" + dateBuilder.getYear(regest.date) + "</field>");
			out.println("<field name='date_sort_value'>" + dateBuilder.getDate(regest.date) + "</field>");
			out.println("<field name='pont_bd'>" + regest.bookShortName + "</field>");
			out.println("<field name='pont_no'>" + regest.page + " n. " + regest.number.replaceAll("[†*]", "") + "</field>");
			out.println("<field name='supplier'><![CDATA[" + splitter.getPopeAndSupplier(regest) + "]]></field>");
			out.println("<field name='date_table'><![CDATA[" + splitter.getPlaceAndDate(regest) + "]]></field>");
			out.println("<field name='initium'><![CDATA[" + splitter.getInitium(regest) + "]]></field>");
			out.println("<field name='recepit_inst'>" + regest.subchapter + "</field>");
			out.println("<field name='diocese'>" + regest.chapter + "</field>");
			out.println("<field name='jaffe2'>" + regest.jaffe + "</field>");
			String doc_regest = regest.bookShortName + ", S. " + regest.page + " n. " + regest.number.replaceAll("[†*]", "");
			out.println("<field name='doc_regest'>" + doc_regest + "</field>");
			out.println("<field name='doc_regest_name'>" + regest.pdfFileName + "#page=" + regest.pdfPage + "</field>");

			out.println("<field name='page'>" + regest.page + "</field>");
			out.println("<field name='page_pdf'>" + regest.pdfPage + "</field>");
			
			for (String line : regest.textLines) {
				out.println("<field name='orig_text'><![CDATA[" + line + "]]></field>");
			}
			
			out.println("<field name='part1'><![CDATA[" + splitter.cutOutHeader(regest.textLines) + "]]></field>");
			String[] mainPart = splitter.cutOutMainPart(regest.textLines).split("\n");
			for (String mainPartLine : mainPart) {
				out.println("<field name='part2'><![CDATA[" + mainPartLine + "]]></field>");
			}
			String[] writtenRecord = splitter.cutOutWrittenRecord(regest.textLines).split("\n");
			for (String writtenRecordLine : writtenRecord) {
				out.println("<field name='part3'><![CDATA[" + writtenRecordLine + "]]></field>");
			}
			String[] comment = splitter.cutOutComment(regest.textLines).split("\n");
			for (String commentLine : comment) {
				out.println("<field name='part4'><![CDATA[" + commentLine + "]]></field>");
			}

			out.println("</doc>");
		}
		
		out.println("</add>");
	}
}
