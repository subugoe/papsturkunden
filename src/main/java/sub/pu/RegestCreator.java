package sub.pu;

import sub.pu.data.Milestone;
import sub.pu.data.Regest;
import sub.pu.util.SolrSearcher;

public class RegestCreator {

	public Regest makeFromMilestones(Milestone starting, Milestone ending) throws Exception {
		
		SolrSearcher searcher = new SolrSearcher();
		
		Regest regest = new Regest();
		regest.pope = starting.regestInfo.pope;
		regest.popeIsAlsoPontifikat = starting.regestInfo.popeIsAlsoPontifikat;
		regest.date = starting.regestInfo.date;
		regest.number = starting.regestInfo.number.replace("\\", "");
		regest.page = starting.regestInfo.page;
		regest.bookOrder = starting.lineNumber;
		regest.pope = starting.regestInfo.pope;
		regest.fromLine = starting.lineNumber;
		regest.toLine = ending.lineNumber;
		regest.csvLine = starting.regestInfo.csvLine;
		
		regest.textLines = searcher.cutOutRegestText(starting.lineNumber, ending.lineNumber);
		
		return regest;
	}
}
