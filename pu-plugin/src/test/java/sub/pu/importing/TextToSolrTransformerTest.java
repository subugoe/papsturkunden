package sub.pu.importing;

import static org.junit.Assert.*;
import static org.custommonkey.xmlunit.XMLAssert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TextToSolrTransformerTest {

	private TextToSolrTransformer transformer;
	private OutputStream outputBaos;

	@Before
	public void beforeEachTest() throws Exception {
		transformer = new TextToSolrTransformer();
		outputBaos = new ByteArrayOutputStream();
	}

	@After
	public void afterEachTest() {
		 System.out.println(outputBaos.toString());
	}

	@Test
	public void emptyFile_producesNullLine() throws Exception {
		String result = transform("empty.txt");
		assertXpathEvaluatesTo("null", "//field[@name='line']", result);
	}
	
	@Test
	public void oneLine() throws Exception {
		String result = transform("oneLine.txt");
		assertXpathEvaluatesTo("1", "//field[@name='id']", result);
		assertXpathEvaluatesTo("myline", "//field[@name='line']", result);
		assertXpathEvaluatesTo("", "//field[@name='next_line']", result);
	}

	@Test
	public void oneLineWithFormFeed() throws Exception {
		String result = transform("oneLine_withFormFeed.txt");
		assertXpathEvaluatesTo("myline", "//field[@name='line']", result);
		assertXpathEvaluatesTo("", "//field[@name='next_line']", result);
	}

	@Test
	public void twoLines() throws Exception {
		String result = transform("twoLines.txt");
		assertXpathEvaluatesTo("1", "//doc[1]/field[@name='id']", result);
		assertXpathEvaluatesTo("myline 1", "//doc[1]/field[@name='line']", result);
		assertXpathEvaluatesTo("myline2", "//doc[1]/field[@name='next_line']", result);
		assertXpathEvaluatesTo("2", "//doc[2]/field[@name='id']", result);
		assertXpathEvaluatesTo("myline 2", "//doc[2]/field[@name='line']", result);
		assertXpathEvaluatesTo("", "//doc[2]/field[@name='next_line']", result);
	}
	
	@Test
	public void twoLinesWithPageBreak() throws Exception {
		String result = transform("twoLines_withPageBreak.txt");
		assertXpathEvaluatesTo("myline 1", "//doc[1]/field[@name='line']", result);
		assertXpathEvaluatesTo("-31", "//doc[1]/field[@name='page']", result);
		assertXpathEvaluatesTo("myline 2", "//doc[2]/field[@name='line']", result);
		assertXpathEvaluatesTo("-30", "//doc[2]/field[@name='page']", result);
	}

	@Test
	public void twoFormFeeds_skipsOnePage() throws Exception {
		String result = transform("twoFormFeeds.txt");
		assertXpathEvaluatesTo("myline 1", "//doc[1]/field[@name='line']", result);
		assertXpathEvaluatesTo("-31", "//doc[1]/field[@name='page']", result);
		assertXpathEvaluatesTo("myline 2", "//doc[2]/field[@name='line']", result);
		assertXpathEvaluatesTo("-29", "//doc[2]/field[@name='page']", result);
	}
	
	//@Test
	public void test2() throws Exception {
		File inFile = new File("/home/dennis/papsturkunden/out-pdftotext.txt");
		 
		transformer.transform(inFile, new FileOutputStream("/home/dennis/papsturkunden/bla.xml"));
	}
	
	private String transform(String fileName) throws Exception {
		transformer.transform(new File("src/test/resources/" + fileName), outputBaos);
		return outputBaos.toString();
	}

}
