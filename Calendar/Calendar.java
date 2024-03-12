package Calendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import TaskEditor.NewTaskInputWindow;
import javafx.event.ActionEvent;
import util.FileManager;
import util.Task;


class Calendar  {
	
	final CalendarUI UI;
	/**
	 * This list is assumed sorted, 
	 * any changes should be done preserving the sequence.
	 */
	final List<Task> tasks;
	private LocalDate selectedDate;
	
	public Calendar(CalendarUI caller) {
		UI = caller;
		selectedDate = LocalDate.now();
		tasks = FileManager.loadFromFile();
		if (tasks.isEmpty()) {
			//TODO: Couldn't Read Data, create new file?
			System.out.println("No save data found.");
		}
	}
	
	LocalDate getSelectedDate() {
		return selectedDate;
	}
	
	String getSelectedDateString() {
		return selectedDate.format(DateTimeFormatter.ofPattern("cccc dd.LL.yyyy"));
	}
	
	void populateTableView() {
		UI.taskView.getItems().clear();
		for (Task t : tasks) {
			if (t.startTime.isBefore(selectedDate.atStartOfDay()))
				continue;
			if (t.startTime.isAfter(selectedDate.plusDays(1).atStartOfDay()))
				break;
			
			UI.taskView.getItems().add(t);	
		}
		UI.taskView.sort();
	}

	/**
	 * This method sets which date is in use by the user interactions 
	 * for reference by other methods.
	 * This method is being called mostly by button events which do not have full information.
	 * This is why additional information has to be collected from the UI.
	 * @param day The day of month integer representation of the day 
	 * to be affected by the user going forward. 
	 */
	void changeSelectedDay(int day) {
		int year = UI.yearSelector.getValue();
		Month month = UI.monthSelector.getValue();
		
		if (day < 1 || day > month.maxLength()) 
			day = selectedDate.getDayOfMonth();
		
		selectedDate = LocalDate.of(year, month, day);
		UI.currentDateLabel.setText(getSelectedDateString());
		populateTableView();
	}
	
	/**
	 * Finds and returns all days for a given month that have a task created for them.
	 * @param month The month to look through
	 * @param year The year the month is associated with
	 * @return a List of integers representing the days of the month.
	 */
	List<Integer> findTaskDays(Month month, int year) {
		List<Integer> list = new ArrayList<>();
		LocalDateTime nextMonth = LocalDateTime.of(year, month.getValue() + 1, 1, 0, 0);
		
		for(Task t : tasks) {
			if (t.startTime.isAfter(nextMonth))
				break;
			
			if (t.startTime.getYear() == year && t.startTime.getMonth().equals(month))
				list.add(t.startTime.getDayOfMonth());
		}
		return list;
	}
	
	void ButtonNew(ActionEvent ev) {
		createNewTask(new NewTaskInputWindow(selectedDate), null);
	}

	void ButtonEdit(ActionEvent ev, Task selectedTask) {
		if (selectedTask == null)
			return;
		createNewTask(new NewTaskInputWindow(selectedTask), selectedTask);
	}

	// editBase can and will be null some of the time.
	private void createNewTask(NewTaskInputWindow dialogue, Task editBase) {
		Optional<Task> returnValue = dialogue.showAndWait();
		if (!returnValue.isPresent()) 
			return;
		Task newTask = returnValue.get();
		
		deleteTask(editBase);		
		tasks.add(newTask);
		tasks.sort(null);
		UI.toogleButtonColored(newTask.startTime.getDayOfMonth());
		populateTableView();
	}

	void ButtonDelete(ActionEvent ev, Task selectedTask) {
		deleteTask(selectedTask);
	}
	
	private void deleteTask(Task toDelete) {
		if (toDelete == null)
			return;
		
		tasks.remove(toDelete);
		UI.taskView.getItems().remove(toDelete);
		if (!dayHasTasks(toDelete.startTime.toLocalDate())) {
			UI.toogleButtonDefault(toDelete.startTime.getDayOfMonth());
		}
			
	}

	private boolean dayHasTasks(LocalDate day) {
		for (Task t : tasks) {
			if (t.startTime.isBefore(day.atStartOfDay()))
				continue;
			if (t.startTime.isAfter(day.plusDays(1).atStartOfDay()))
				break;
				
			if (t.startTime.toLocalDate().equals(day)) {
				return true;
			}
		}
		return false;
	}

	public void saveData() {
		System.out.println("Attemping save...");
		FileManager.writeToFile(tasks);
		System.out.println("Save completed");
	}
}
