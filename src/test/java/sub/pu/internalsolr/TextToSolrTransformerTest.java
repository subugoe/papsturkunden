package sub.pu.internalsolr;

import static org.custommonkey.xmlunit.XMLAssert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sub.pu.internalsolr.TextToSolrTransformer;

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
	public void emptyFile_producesEmptyDocument() throws Exception {
		String result = transform("empty.txt");
		assertXpathEvaluatesTo("0", "count(//field[@name='line'])", result);
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
		assertXpathEvaluatesTo("myline1", "//doc[1]/field[@name='line_spaceless']", result);
		assertXpathEvaluatesTo("myline2", "//doc[1]/field[@name='next_line']", result);
		assertXpathEvaluatesTo("2", "//doc[2]/field[@name='id']", result);
		assertXpathEvaluatesTo("myline 2", "//doc[2]/field[@name='line']", result);
		assertXpathEvaluatesTo("", "//doc[2]/field[@name='next_line']", result);
	}
	
	@Test
	public void twoLinesWithPageBreak() throws Exception {
		String result = transform("twoLines_withPageBreak.txt");
		assertXpathEvaluatesTo("myline 1", "//doc[1]/field[@name='line']", result);
		assertXpathEvaluatesTo("1", "//doc[1]/field[@name='page']", result);
		assertXpathEvaluatesTo("myline 2", "//doc[2]/field[@name='line']", result);
		assertXpathEvaluatesTo("2", "//doc[2]/field[@name='page']", result);
	}

	@Test
	public void twoFormFeeds_skipsOnePage() throws Exception {
		String result = transform("twoFormFeeds.txt");
		assertXpathEvaluatesTo("myline 1", "//doc[1]/field[@name='line']", result);
		assertXpathEvaluatesTo("1", "//doc[1]/field[@name='page']", result);
		assertXpathEvaluatesTo("myline 2", "//doc[2]/field[@name='line']", result);
		assertXpathEvaluatesTo("3", "//doc[2]/field[@name='page']", result);
	}
	
	@Test
	public void reverseAlphabetRubbish() throws Exception {
		String result = transform("zyxRubbish.txt");
		assertXpathEvaluatesTo("myline 1. End of line", "//doc[1]/field[@name='line']", result);
		assertXpathEvaluatesTo("myline1.Endofline", "//doc[1]/field[@name='line_spaceless']", result);
		assertXpathEvaluatesTo("myline 2", "//doc[2]/field[@name='line']", result);
	}
	
	private String transform(String fileName) throws Exception {
		transformer.transform(new File("src/test/resources/" + fileName), 0, outputBaos);
		return outputBaos.toString();
	}

}
