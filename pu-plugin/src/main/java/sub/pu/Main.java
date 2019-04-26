package sub.pu;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		new Main().execute(args);
	}
	
	private void execute(String[] args) throws Exception {
		PrintWriter writer = new PrintWriter(new FileWriter("/home/dennis/papsturkunden/search-results.txt"));
		String file = FileUtils.readFileToString(new File("/home/dennis/papsturkunden/ownClowd/Regesta_pontificum_Romanorum_Germania_Germ_I_Export.csv"), "UTF8");
		String[] lines = file.split("\\n");
		
		RegestExtractor extractor = new RegestExtractor();
		List<String> results = extractor.processLines(lines);
		
		Collections.sort(results);
		for (int i = 0; i < results.size(); i++) {
			String res = results.get(i);
			System.out.println(res + "    -- " + i);
			writer.println(res);
		}
		writer.close();
	}

}
