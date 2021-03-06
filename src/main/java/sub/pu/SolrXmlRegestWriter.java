package sub.pu;

import java.io.PrintWriter;
import java.util.List;

import sub.pu.data.Regest;
import sub.pu.util.DateBuilder;
import sub.pu.util.Regex;

public class SolrXmlRegestWriter {

	private DateBuilder dateBuilder = new DateBuilder();
	private RegestSplitter splitter = new RegestSplitter();

	public void write(List<Regest> regests, PrintWriter out) {
		out.println("<?xml version=\"1.0\"?>");
		out.println("<add>");

		for (Regest regest : regests) {
			out.println("<doc>");
			
			out.println("<field name='bd'>" + regest.bookShortName.replaceAll("[ \\.]+", "").toLowerCase() + "</field>");
			out.println("<field name='page'>" + regest.page + "</field>");
			out.println("<field name='num'>" + Regex.extractFirst("([0-9]+)", regest.number) + "</field>");
			out.println("<field name='page_pdf'>" + regest.pdfPage + "</field>");
			
			String idno = regest.bookShortName + ", S. " + regest.page + " n. " + regest.number;
			out.println("<field name='idno'>" + idno + "</field>");
			out.println("<field name='date_sort'>" + dateBuilder.getYear(regest.date) + "</field>");
			out.println("<field name='date_sort_value'>" + dateBuilder.getDate(regest.date) + "</field>");
			out.println("<field name='pont_bd'>" + regest.bookShortName + "</field>");
			out.println("<field name='pont_no'>" + regest.page + " n. " + regest.number.replaceAll("[†*]", "") + "</field>");
			out.println("<field name='supplier'><![CDATA[" + splitter.getPopeAndSupplier(regest) + "]]></field>");
			out.println("<field name='date_table'><![CDATA[" + splitter.getPlaceAndDate(regest) + "]]></field>");
			out.println("<field name='initium'><![CDATA[" + splitter.get2b1initium(regest.textLines) + "]]></field>");
			out.println("<field name='recepit_inst'>" + regest.subchapter + "</field>");
			out.println("<field name='diocese'>" + regest.chapter + "</field>");
			out.println("<field name='jaffe2'>" + regest.jaffe + "</field>");
			String doc_regest = regest.bookShortName + ", S. " + regest.page + " n. " + regest.number.replaceAll("[†*]", "");
			out.println("<field name='doc_regest'>" + doc_regest + "</field>");
			out.println("<field name='doc_regest_name'>" + regest.pdfFileName + "#page=" + regest.pdfPage + "</field>");

			for (String line : regest.textLines) {
				out.println("<field name='orig_text'><![CDATA[" + line + "]]></field>");
			}
			
			out.println("<field name='part1'><![CDATA[" + splitter.cutOutHeader(regest.textLines) + "]]></field>");
			String[] mainPart = splitter.cutOutMainPart(regest.textLines).split("\n");
			for (String mainPartLine : mainPart) {
				out.println("<field name='part2'><![CDATA[" + mainPartLine + "]]></field>");
			}
			out.println("<field name='part2a_regest'><![CDATA[" + splitter.get2aCoreRegest(regest.textLines) + "]]></field>");
			out.println("<field name='part2b_angaben'><![CDATA[" + splitter.get2bInfoForWriting(regest.textLines) + "]]></field>");
			out.println("<field name='part2b1_initium'><![CDATA[" + splitter.get2b1initium(regest.textLines) + "]]></field>");
			out.println("<field name='part2b2_unterschriften'><![CDATA[" + splitter.get2b2subscriptions(regest.textLines) + "]]></field>");
			out.println("<field name='part2b3_datierung'><![CDATA[" + splitter.get2b3originalDating(regest.textLines) + "]]></field>");
			String[] writtenRecord = splitter.cutOutWrittenRecord(regest.textLines).split("\n");
			for (String writtenRecordLine : writtenRecord) {
				out.println("<field name='part3'><![CDATA[" + writtenRecordLine + "]]></field>");
			}
			out.println("<field name='part3a_handschriften'><![CDATA[" + splitter.get3aHandWritings(regest.textLines) + "]]></field>");
			out.println("<field name='part3b_editionen'><![CDATA[" + splitter.get3bEditions(regest.textLines) + "]]></field>");
			out.println("<field name='part3c_regesten'><![CDATA[" + splitter.get3cRegests(regest.textLines) + "]]></field>");
			out.println("<field name='part3c2_jaffe'><![CDATA[" + splitter.get3c2jaffe(regest.textLines) + "]]></field>");
			String[] comment = splitter.cutOutComment(regest.textLines).split("\n");
			for (String commentLine : comment) {
				out.println("<field name='part4'><![CDATA[" + commentLine + "]]></field>");
			}

			out.println("</doc>");
		}
		
		out.println("</add>");
	}
}
