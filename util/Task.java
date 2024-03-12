package util;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Task implements Comparable<Task> {
	
	public final LocalDateTime startTime;
	public final LocalDateTime endTime;
	public final Duration duration;
	public final String description;
	// Not functionally implemented:
	boolean archived;
	
	
	public Task(LocalDateTime startTime, LocalDateTime endTime, String description, boolean archived) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
		this.archived = archived;
		duration = Duration.between(startTime, endTime);
	}
	
	public Task(LocalDateTime startTime, LocalDateTime endTime, String description) {
		this(startTime, endTime, description, false);
	}

	/**
	 * Parses CSV data into a Task object.
	 * @param line A single line of CSV data.
	 * @param separator The CSV-separator used in the data.
	 * @return returns a Task object parsed from the provided line. Returns null if unable.
	 */
	public static Task parseFromCSV(String line, String separator) {
		String[] values = line.split(separator, 4);  
		
		try {
			LocalDateTime startTime = LocalDateTime.parse(values[0]);
			LocalDateTime endTime = LocalDateTime.parse(values[1]);
			boolean archived = Boolean.parseBoolean(values[2]);
			String desc = "" + values[3];
			
			return new Task(startTime, endTime, desc, archived);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	/**
	 * Converts this Task into a CSV representation.
	 * @param separator The separator to use for the CSV formating.
	 * @return a String representation of the Task in CSV format.
	 */
	String toCSV(String separator) {	
		return (startTime.toString() + separator + endTime.toString() 
			+ separator + archived + separator + description);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!other.getClass().equals(Task.class))
			return false;
		Task task = (Task)other;
		
		return startTime.equals(task.startTime) && endTime.equals(task.endTime)
				&& description.equals(task.description) && archived == task.archived;
	}
	
	@Override
	public int compareTo(Task other) {
		return startTime.compareTo(other.startTime) * 1000 + other.endTime.compareTo(endTime) * 10
				+ description.compareTo(other.description) + (((Boolean)archived).compareTo(other.archived));
	}
}
