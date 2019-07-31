package sub.pu;

import java.io.File;

public class Main_allBooks {

	public static void main(String[] args) throws Exception {
		new Main_allBooks().execute(args);
	}

	private void execute(String[] args) throws Exception {
		File configFolder = new File("config-all-books");
		Main_oneBook currentBook = null;
		for (File confFile : configFolder.listFiles()) {
			System.out.println();
			System.out.println("    ### Processing " + confFile.getName() + " ###");
			System.out.println();
			currentBook = new Main_oneBook();
			currentBook.execute(new String[] {confFile.getAbsolutePath()});
		}
		currentBook.importCompleteFolderIntoResultsSolr();
	}

}
