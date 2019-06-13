package sub.pu;

import java.util.ArrayList;
import java.util.List;

public class Regest {

	public List<String> textLines = new ArrayList<>();;
	public int bookOrder;
	public int tocOrder;
	
	public String bookName;
	public String pope;
	public String date;
	public String number;
	public String page;
	public boolean popeIsAlsoPontifikat;
	
	public String chapter = "";
	public String subchapter = "";
	public String jaffe = "";
	
	public String queryForDebugging;
	public int fromLine;
	public int toLine;
	public String csvLine;
}
