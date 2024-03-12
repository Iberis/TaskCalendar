package util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FileManager {
	
	static final String FILE_PATH = "H:\\SAE\\Workspace\\Calendar\\data.csv";
	static final String CSV_SEPARATOR = ";";
	
	public static List<Task> loadFromFile() {
		List<Task> list = new LinkedList<>();
		try (BufferedReader is = new BufferedReader(new FileReader(new File(FILE_PATH)))) {
			String line;			
			while ((line = is.readLine()) != null) {
				Task task = Task.parseFromCSV(line, CSV_SEPARATOR);
				if (task != null)
					list.add(task);
			}
			
			return list;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static void writeToFile(List<Task> data) {
		try (BufferedWriter os = new BufferedWriter(new FileWriter(new File(FILE_PATH)))) {
			for (Task task : data) {
				os.write(task.toCSV(CSV_SEPARATOR));
				os.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
