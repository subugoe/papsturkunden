package sub.pu;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class RegestEnricherTest {
	
	private RegestEnricher enricher = new RegestEnricher();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		Regest regest = new Regest();
		regest.page = "10";
		
		String[] chapterLines = {"SALZBURG;;",
				";;",
				";Archiepiscopatus;4",
				";;",
				";Ecclesia;46"};
		
		enricher.addChaptersToRegest(regest, chapterLines);
		System.out.println(regest.chapter);
		System.out.println(regest.subchapter);
	}

}
