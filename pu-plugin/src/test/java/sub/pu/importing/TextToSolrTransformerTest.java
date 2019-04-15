package sub.pu.importing;

import static org.junit.Assert.*;

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
	public void test() throws Exception {
		File inFile = new File("src/test/resources/bla.txt");
		 
		transformer.transform(inFile, outputBaos);
	}

	@Test
	public void test2() throws Exception {
		File inFile = new File("/home/dennis/papsturkunden/out-pdftotext.txt");
		 
		transformer.transform(inFile, new FileOutputStream("/home/dennis/papsturkunden/bla.xml"));
	}

}
