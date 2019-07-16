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
	public void test() {
		String mainPart = splitter.cutOutMainPart(regestLines());
		System.out.println(mainPart);
		
		String writtenRecord = splitter.cutOutWrittenRecord(regestLines());
		System.out.println(writtenRecord);
		
		String comment = splitter.cutOutComment(regestLines());
		System.out.println(comment);
	}

	private List<String> regestLines() {
		List<String> lines = new ArrayList<>();
		lines.add("1                                                                     746 iul. 1");
		lines.add("Z a c h a r i a s Bonifatio");
		lines.add("viros apud");
		lines.add("      Laud. in");
		lines.add("Reg.");
		lines.add("            v. Mainz,");
		lines.add("      bücher");
		return lines;
	}

}
