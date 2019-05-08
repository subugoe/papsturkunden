package sub.pu;

public class RegestCreator {

	public Regest makeFromMilestones(Milestone starting, Milestone ending) {
		
		Regest regest = new Regest();
		regest.bookOrder = starting.lineNumber;
		regest.textLines.add(starting.lineContents);
		regest.pope = starting.regestInfo.pope;
		
		return regest;
	}
}
