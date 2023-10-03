package eyePatterns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main {
	public static void main(String[] args) throws Exception {
		String inputFile = "C:\\Users\\soria\\Desktop\\output_not_collapsed.txt";
		File file = new File(inputFile);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		// Get rid of the first header line
		String line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			String pattern = line.replaceAll("[^a-zA-Z]+", "");
			System.out.println(line);
		}
	}
}
