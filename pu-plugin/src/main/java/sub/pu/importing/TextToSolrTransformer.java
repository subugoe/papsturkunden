package sub.pu.importing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class TextToSolrTransformer {
	
	private int lineNumber = 0;
	private int pageNumber = -31;
	private String bookName = "Germ. Pont. I";
	
	public void transform(File inputFile, OutputStream outStream) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(outStream));

		String first = reader.readLine();
		String second = "";
		
		writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		writer.println("<add>");
		
		while((second = reader.readLine()) != null) {
			int countOfFormFeeds = second.length() - second.replace("\u000C", "").length();
			if (countOfFormFeeds > 0) {
				reader.readLine();
				second = reader.readLine();
			}
			
			print(first, second, writer);
			
			if (countOfFormFeeds > 0) {
				pageNumber += countOfFormFeeds;
			}

			first = second;
		}
		
		print(first, "", writer);

		writer.println("</add>");

		
		writer.flush();	
		reader.close();
	}
	
	private void print(String first, String second, PrintWriter writer) throws Exception {
		if (first == null)
			return;
		if (second == null)
			second = "";
		
		writer.println("<doc>");
		lineNumber++;
		writer.println("  <field name=\"id\">" + lineNumber + "</field>");
		writer.println("  <field name=\"book\">" + bookName + "</field>");
		writer.println("  <field name=\"page\">" + pageNumber + "</field>");
		writer.println("  <field name=\"line\"><![CDATA[" + removeReverseAlphabet(first) + "]]></field>");
		writer.println("  <field name=\"line_spaceless\"><![CDATA[" + removeReverseAlphabet(first).replaceAll("\\s", "") + "]]></field>");
		writer.println("  <field name=\"next_line\"><![CDATA[" + second.replaceAll("\\s", "") + "]]></field>");
		writer.println("</doc>");

		writer.flush();
	}

	private String removeReverseAlphabet(String s) {
		return s.replaceAll("zyx[a-wA-Z]*", "");
	}
}
