package sub.pu;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import sub.pu.util.DateBuilder;

public class DateBuilderTest {

	private DateBuilder dateBuilder = new DateBuilder();
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void oneYearInParentheses() {
		String year = dateBuilder.getYear("(888)");
		
		assertEquals("888", year);
	}

	@Test
	public void twoYears() {
		String year = dateBuilder.getYear("888-999");
		
		assertEquals("888", year);
	}

	@Test
	public void july() {
		String year = dateBuilder.getDate("746 iul.");
		
		assertEquals("7460799", year);
	}

	@Test
	public void anteMay() {
		String year = dateBuilder.getDate("(873 ante mai.)");
		
		assertEquals("8730599", year);
	}

	@Test
	public void maySecond() {
		String year = dateBuilder.getDate("873 mai. 2");
		
		assertEquals("8730502", year);
	}

	@Test
	public void elevenHundred() {
		String year = dateBuilder.getDate("(cr. 1117)");
		
		assertEquals("11179999", year);
	}

	@Test
	public void dotBeforeDay() {
		String year = dateBuilder.getDate("(1125-29) feb.26");
		
		assertEquals("11250226", year);
	}

}
