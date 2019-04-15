package sub.pu.importing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
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
		File inputText = new File(inputDir, "out-pdftotext.txt");

		fileAccess.cleanDir(outputDir);
		out.println("    Converting text file to index XML file.");

//		InputStream xsltStream = ImporterStepConvert.class.getResourceAsStream("/indexer.xslt");
//		xslt.setXsltScript(xsltStream);
//		xslt.setErrorOut(out);
//
//		OutputStream fileOs = fileAccess.createOutputStream(outputDir, "solr-output.xml");
//		xslt.transform(inputText.getAbsolutePath(), fileOs);

	}

	@Override
	public String getStepDescription() {
		return "Konvertierung";
	}

}
