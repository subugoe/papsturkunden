package sub.pu;

import java.io.File;

public class MainAllBooks {

	public static void main(String[] args) throws Exception {
		new MainAllBooks().execute();
	}

	private void execute() throws Exception {
		File configFolder = new File("config-all-books");
		MainOneBook currentBook = null;
		for (File confFile : configFolder.listFiles()) {
			System.out.println();
			System.out.println("    ### Processing " + confFile.getName() + " ###");
			System.out.println();
			currentBook = new MainOneBook();
			currentBook.execute(new String[] {confFile.getAbsolutePath()});
		}
		currentBook.importCompleteFolderIntoResultsSolr();
	}

}
