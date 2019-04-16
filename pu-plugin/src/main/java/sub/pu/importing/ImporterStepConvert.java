package sub.pu.importing;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

import sub.ent.api.ImporterStep;
import sub.ent.backend.FileAccess;

public class ImporterStepConvert extends ImporterStep {

	private TextToSolrTransformer transformer = new TextToSolrTransformer();
	private FileAccess fileAccess = new FileAccess();

	@Override
	public void execute(Map<String, String> params) throws Exception {
		String gitDir = params.get("gitDir");
		String solrXmlDir = params.get("solrXmlDir");
		File outputDir = new File(solrXmlDir);
		File inputDir = new File(gitDir);
		File inputText = new File(inputDir, "out-pdftotext.txt");

		fileAccess.cleanDir(outputDir);
		out.println("    Converting text file to index XML file.");

		OutputStream fileOs = fileAccess.createOutputStream(outputDir, "solr-output.xml");
		transformer.transform(inputText, fileOs);
	}

	@Override
	public String getStepDescription() {
		return "Konvertierung";
	}

}
