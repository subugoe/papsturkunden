package sub.pu;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class RegestSplitterTest {

	private RegestSplitter splitter = new RegestSplitter();
	
	private List<String> regestLines(boolean hasRecord, boolean hasComment) {
		List<String> lines = new ArrayList<>();
		lines.add("1                                                                     746 iul. 1");
		lines.add("Z a c h a r i a s Bonifatio");
		lines.add("viros apud");
		if (hasRecord) {
			lines.add("      Laud. in");
			lines.add("Reg.");
		}
		if (hasComment) {
			lines.add("            v. Mainz,");
			lines.add("      bücher");
		}
		return lines;
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void withoutWrittenRecord() {
		String mainPart = splitter.cutOutMainPart(regestLines(false, true));
		assertEquals("Zacharias Bonifatio\nviros apud\n", mainPart);

		String writtenRecord = splitter.cutOutWrittenRecord(regestLines(false, true));
		assertEquals("", writtenRecord);
		
		String comment = splitter.cutOutComment(regestLines(false, true));
		assertEquals("v. Mainz,\nbücher\n", comment);
	}

	@Test
	public void allParts() {
		String mainPart = splitter.cutOutMainPart(regestLines(true, true));
		assertEquals("Zacharias Bonifatio\nviros apud\n", mainPart);
		
		String writtenRecord = splitter.cutOutWrittenRecord(regestLines(true, true));
		assertEquals("Laud. in\nReg.\n", writtenRecord);
		
		String comment = splitter.cutOutComment(regestLines(true, true));
		assertEquals("v. Mainz,\nbücher\n", comment);
	}

	@Test
	public void combineTwoLetters() {
		String result = splitter.combineWhitespacedWords("a b bla");	
		assertEquals("ab bla", result);
	}

	@Test
	public void combineThreeLetters() {
		String result = splitter.combineWhitespacedWords("a b c bla");	
		assertEquals("abc bla", result);
	}

	@Test
	public void combineFourLetters() {
		String result = splitter.combineWhitespacedWords("a b c d bla");	
		assertEquals("abcd bla", result);
	}

	@Test
	public void dontCombineWords() {
		String result = splitter.combineWhitespacedWords("aa bc");	
		assertEquals("aa bc", result);
	}

	@Test
	public void realWorldExample() {
		String result = splitter.combineWhitespacedWords("A l e x a n d r o I I I Conradus");	
		assertEquals("Alexandro III Conradus", result);
	}
	
	@Test
	public void onlyOneRomanDigit() {
		String result = splitter.combineWhitespacedWords("A l e x a n d r o I Conradus");	
		assertEquals("Alexandro I Conradus", result);
	}
	
	@Test
	public void parseSupplier() {
		String result = splitter.parseSupplier("Leoni III [Arno archiep.]: cum dispositum");	
		assertEquals("[Arno archiep.]", result);
	}
	
}
