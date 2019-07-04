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
		String result = regestWriter.combineWhitespacedWords("a b", "[a-z]");	
		assertEquals("ab", result);
	}

	@Test
	public void combineThreeLetters() {
		String result = regestWriter.combineWhitespacedWords("a b c", "[a-z]");	
		assertEquals("abc", result);
	}

	@Test
	public void combineFourLetters() {
		String result = regestWriter.combineWhitespacedWords("a b c d", "[a-z]");	
		assertEquals("abcd", result);
	}

	@Test
	public void dontCombineWords() {
		String result = regestWriter.combineWhitespacedWords("aa bc", "[a-z]");	
		assertEquals("aa bc", result);
	}

	@Test
	public void realWorldExample() {
		String result = regestWriter.combineWhitespacedWords("A l e x a n d r o I I I Conradus", "[IVX]");	
		result = regestWriter.combineWhitespacedWords(result, "[a-zA-Z]");
		assertEquals("Alexandro III Conradus", result);
	}
	
	@Test
	public void parseSupplier() {
		String result = regestWriter.parseSupplier("Leoni III [Arno archiep.]: cum dispositum");	
		assertEquals("[Arno archiep.]", result);
	}
	

}
