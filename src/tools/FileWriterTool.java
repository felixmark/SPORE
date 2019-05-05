package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class FileWriterTool {
	public static void writeFile(String folder, String fileName, String content) {
		String absoluteFile = folder+fileName;
		File file = new File(absoluteFile);
	    if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }

	    try {
			PrintWriter out = new PrintWriter(absoluteFile);
			out.println(content);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}