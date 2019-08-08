package sub.pu;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class RegestSplitterTest {

	private RegestSplitter splitter = new RegestSplitter();
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void getsPart3aHandWritings() {
		String mainPart = splitter.get3aHandWritings(standardRegest(true, true));
		assertEquals("Handschrift.", mainPart);
	}

	@Test
	public void getsPart3bEditions() {
		String mainPart = splitter.get3bEditions(standardRegest(true, true));
		assertEquals("Ed. bla.", mainPart);
	}

	@Test
	public void getsPart3c2_jaffe() {
		String mainPart = splitter.get3c2jaffe(standardRegest(true, true));
		assertEquals("JL. —.", mainPart);
	}

	@Test
	public void getsPart3cRegests() {
		String mainPart = splitter.get3cRegests(standardRegest(true, true));
		assertEquals("Regg. blub. JL. —.", mainPart);
	}

	@Test
	public void getsPart2b1initium() {
		String mainPart = splitter.get2b1initium(standardRegest(true, true));
		assertEquals("Ne mireris", mainPart);
	}

	@Test
	public void getsPart2b2() {
		String mainPart = splitter.get2b2subscriptions(standardRegest(true, true));
		assertEquals("Ego bla.", mainPart);
	}

	@Test
	public void getsPart2b3() {
		String mainPart = splitter.get2b3originalDating(standardRegest(true, true));
		assertEquals("Scr. p. m. blub.", mainPart);
	}

	@Test
	public void getsEmptyPart2b() {
		String mainPart = splitter.get2bInfoForWriting(regestWithoutPart2b());
		assertEquals("", mainPart);
	}

	@Test
	public void getsPart2b() {
		String mainPart = splitter.get2bInfoForWriting(standardRegest(true, true));
		assertEquals("— Ne mireris. Ego bla. Scr. p. m. blub.", mainPart);
	}

	@Test
	public void getsCoreRegestWithoutPart2b() {
		String mainPart = splitter.get2aCoreRegest(regestWithoutPart2b());
		assertEquals("Zacharias Bonifatio viros.", mainPart);
	}

	@Test
	public void getsCoreRegest() {
		String mainPart = splitter.get2aCoreRegest(standardRegest(true, true));
		assertEquals("Zacharias Bonifatio viros. — apud.", mainPart);
	}

	@Test
	public void withoutWrittenRecord() {
		String mainPart = splitter.cutOutMainPart(standardRegest(false, true));
		assertEquals("Zacharias Bonifatio\nviros. — apud. — Ne mireris. Ego bla. Scr. p. m. blub.\n", mainPart);

		String writtenRecord = splitter.cutOutWrittenRecord(standardRegest(false, true));
		assertEquals("", writtenRecord);
		
		String comment = splitter.cutOutComment(standardRegest(false, true));
		assertEquals("v. Mainz,\nbücher\n", comment);
	}

	@Test
	public void allParts() {
		String mainPart = splitter.cutOutMainPart(standardRegest(true, true));
		assertEquals("Zacharias Bonifatio\nviros. — apud. — Ne mireris. Ego bla. Scr. p. m. blub.\n", mainPart);
		
		String writtenRecord = splitter.cutOutWrittenRecord(standardRegest(true, true));
		assertEquals("Handschrift. — Ed. bla.\n— Regg. blub. JL. —.\n", writtenRecord);
		
		String comment = splitter.cutOutComment(standardRegest(true, true));
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
	
	private List<String> standardRegest(boolean hasRecord, boolean hasComment) {
		List<String> lines = new ArrayList<>();
		lines.add("1                                                                     746 iul. 1");
		lines.add("Z a c h a r i a s Bonifatio");
		lines.add("viros. — apud. — Ne mireris. Ego bla. Scr. p. m. blub.");
		if (hasRecord) {
			lines.add("      Handschrift. — Ed. bla.");
			lines.add("— Regg. blub. JL. —.");
		}
		if (hasComment) {
			lines.add("            v. Mainz,");
			lines.add("      bücher");
		}
		return lines;
	}

	private List<String> regestWithoutPart2b() {
		List<String> lines = new ArrayList<>();
		lines.add("1                                                                     746 iul. 1");
		lines.add("Z a c h a r i a s Bonifatio");
		lines.add("viros.");
		return lines;
	}

}
