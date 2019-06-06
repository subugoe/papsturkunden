package sub.pu;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class XmlRegestWriterTest {

	private XmlRegestWriter regestWriter = new XmlRegestWriter();
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void combineTwoLetters() {
		String result = regestWriter.combineWhitespacedWords("a b");	
		assertEquals("ab", result);
	}

	@Test
	public void combineThreeLetters() {
		String result = regestWriter.combineWhitespacedWords("a b c");	
		assertEquals("abc", result);
	}

	@Test
	public void combineFourLetters() {
		String result = regestWriter.combineWhitespacedWords("a b c d");	
		assertEquals("abcd", result);
	}

	@Test
	public void dontCombineWords() {
		String result = regestWriter.combineWhitespacedWords("aa bc");	
		assertEquals("aa bc", result);
	}

}
