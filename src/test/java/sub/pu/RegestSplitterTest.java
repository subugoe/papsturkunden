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
		assertEquals("Z a c h a r i a s Bonifatio viros apud ", mainPart);

		String writtenRecord = splitter.cutOutWrittenRecord(regestLines(false, true));
		assertEquals("", writtenRecord);
		
		String comment = splitter.cutOutComment(regestLines(false, true));
		assertEquals("            v. Mainz,       bücher ", comment);
	}

	@Test
	public void allParts() {
		String mainPart = splitter.cutOutMainPart(regestLines(true, true));
		assertEquals("Z a c h a r i a s Bonifatio viros apud ", mainPart);
		
		String writtenRecord = splitter.cutOutWrittenRecord(regestLines(true, true));
		assertEquals("      Laud. in Reg. ", writtenRecord);
		
		String comment = splitter.cutOutComment(regestLines(true, true));
		assertEquals("            v. Mainz,       bücher ", comment);
	}

}
