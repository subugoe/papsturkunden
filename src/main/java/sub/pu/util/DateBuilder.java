package sub.pu.util;

public class DateBuilder {

	public String getYear(String date) {
		return Regex.extractFirst("([0-9]{3,4})", date);
	}

	public String getDate(String date) {
		return getYear(date) + getMonth(date) + getDay(date);
	}
	
	private String getMonth(String date) {
		for (String monthName : Regex.extractAll("([a-z]{3,5})", date)) {
			switch (monthName) {
			case "ian":
				return "01";
			case "feb":
				return "02";
			case "mart":
				return "03";
			case "april":
				return "04";
			case "mai":
				return "05";
			case "iun":
				return "06";
			case "iul":
				return "07";
			case "aug":
				return "08";
			case "sept":
				return "09";
			case "oct":
				return "10";
			case "nov":
				return "11";
			case "dec":
				return "12";
			}
		}
		return "99";
	}

	private String getDay(String date) {
		String day = Regex.extractFirst("[ \\.]([123]?[0-9])($|[^0-9])", date);
		switch (day.length()) {
		case 1:
			return "0" + day;
		case 2:
			return day;
		default:
			return "99";
		}
	}
	
}
