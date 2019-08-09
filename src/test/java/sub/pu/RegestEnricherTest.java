package sub.pu;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sub.pu.data.Regest;

public class RegestEnricherTest {
	
	private RegestEnricher enricher = new RegestEnricher();
	private String[] chapterLines = {"SALZBURG;1;",
			";;",
			";Archiepiscopatus;4",
			";;",
			";Ecclesia;11",
			";;",
			"GURK;13;",
			";;",
			";Gurcensis;15",
			";;",
			";Bla;17",
			};

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void page1() {
		List<Regest> regests = createRegestWithPage(1);
				
		enricher.addChapters(regests, chapterLines);
		
		assertEquals("", regests.get(0).chapter);
		assertEquals("", regests.get(0).subchapter);
	}
	
	@Test
	public void page4() {
		List<Regest> regests = createRegestWithPage(4);
				
		enricher.addChapters(regests, chapterLines);
		
		assertEquals("SALZBURG", regests.get(0).chapter);
		assertEquals("Archiepiscopatus", regests.get(0).subchapter);
	}
	
	@Test
	public void page10() {
		List<Regest> regests = createRegestWithPage(10);
				
		enricher.addChapters(regests, chapterLines);
		
		assertEquals("SALZBURG", regests.get(0).chapter);
		assertEquals("Archiepiscopatus", regests.get(0).subchapter);
	}
	
	@Test
	public void page11() {
		List<Regest> regests = createRegestWithPage(11);
				
		enricher.addChapters(regests, chapterLines);
		
		assertEquals("SALZBURG", regests.get(0).chapter);
		assertEquals("Ecclesia", regests.get(0).subchapter);
	}
	
	@Test
	public void page12() {
		List<Regest> regests = createRegestWithPage(12);
				
		enricher.addChapters(regests, chapterLines);
		
		assertEquals("SALZBURG", regests.get(0).chapter);
		assertEquals("Ecclesia", regests.get(0).subchapter);
	}
	
	@Test
	public void page15() {
		List<Regest> regests = createRegestWithPage(15);
				
		enricher.addChapters(regests, chapterLines);
		
		assertEquals("GURK", regests.get(0).chapter);
		assertEquals("Gurcensis", regests.get(0).subchapter);
	}
	
	@Test
	public void page17() {
		List<Regest> regests = createRegestWithPage(17);
				
		enricher.addChapters(regests, chapterLines);
		
		assertEquals("GURK", regests.get(0).chapter);
		assertEquals("Bla", regests.get(0).subchapter);
	}
	
	@Test
	public void page100() {
		List<Regest> regests = createRegestWithPage(100);
				
		enricher.addChapters(regests, chapterLines);
		
		assertEquals("GURK", regests.get(0).chapter);
		assertEquals("Bla", regests.get(0).subchapter);
	}
	
	@Test
	public void appendix() {
		String[] chapterLines = {"SALZBURG;;",
				";;",
				";Archiepiscopatus;4",
				";;",
				";Appendix;"
				};
		List<Regest> regests = createRegestWithPage(1);
				
		enricher.addChapters(regests, chapterLines);
		// used to cause an Exception
	}
	
	private List<Regest> createRegestWithPage(int page) {
		Regest regest = new Regest();
		regest.page = page + "";
		List<Regest> list = new ArrayList<>();
		list.add(regest);
		return list;
	}

}
