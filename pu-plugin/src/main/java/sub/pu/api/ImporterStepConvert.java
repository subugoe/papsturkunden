package sub.pu.api;

import java.io.File;
import java.util.Map;

import sub.ent.api.ImporterStep;
import sub.ent.backend.FileAccess;
import sub.ent.backend.Xslt;

public class ImporterStepConvert extends ImporterStep {

	private Xslt xslt = new Xslt();
	private FileAccess fileAccess = new FileAccess();

	@Override
	public void execute(Map<String, String> params) throws Exception {
		String gitDir = params.get("gitDir");
		String solrXmlDir = params.get("solrXmlDir");
		File outputDir = new File(solrXmlDir);
		File inputDir = new File(gitDir);

		fileAccess.cleanDir(outputDir);
		out.println("    Converting XML to index file.");

	}

	@Override
	public String getStepDescription() {
		return "Konvertierung";
	}

}
