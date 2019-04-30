package sub.pu;

import java.util.Comparator;

public class Milestone {

	public RegestInfo regestInfo;
	public int lineNumber;
	public boolean isRegestBeginning = true;

	public String lineContents;
	public String solrQuery;
	
	public static class BookOrderComparator implements Comparator<Milestone> {

		@Override
		public int compare(Milestone m1, Milestone m2) {
			Integer i1 = m1.lineNumber;
			
			int lineComparison = i1.compareTo(m2.lineNumber);
			if (lineComparison != 0)
				return lineComparison;
			
			Integer tocIndex1 = m1.regestInfo.tocIndex;
			return tocIndex1.compareTo(m2.regestInfo.tocIndex);
		}
		
	}
}
