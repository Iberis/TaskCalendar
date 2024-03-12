package TaskEditor;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import util.Task;

public class NewTaskInputWindow extends Dialog<Task> {

	private TextField descriptionTextField;
	private DateTimePicker startTimePicker;
	private DateTimePicker endTimePicker;
	
	public NewTaskInputWindow(Task selected) {
		this(selected.startTime, selected.endTime, selected.description);
	}
	
	public NewTaskInputWindow(LocalDate date) {
		this(date.atTime(9, 0), date.atTime(10, 0), "");
	}
	
	private NewTaskInputWindow(LocalDateTime startTime, LocalDateTime endTime, String description) {
		setTitle("New Task");
		setResizable(false);
		getDialogPane().getButtonTypes().add(ButtonType.OK);
		getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		setResultConverter(ev -> {
			if (ev == ButtonType.OK) {
				return new Task(
						startTimePicker.getDateTimeValue(), 
						endTimePicker.getDateTimeValue(), 
						descriptionTextField.getText());
			}
			
			return null;
		} );
		getDialogPane().setContent(buildMainPane(startTime, endTime, description));
	}

	private Parent buildMainPane(LocalDateTime startTime, LocalDateTime endTime, String description) {
		VBox vOuter = new VBox(10);
		
		VBox start = new VBox();
		startTimePicker = new DateTimePicker(startTime);
		start.getChildren().addAll(new Label("Start Time:"), startTimePicker);
		
		VBox end = new VBox();
		endTimePicker = new DateTimePicker(endTime);
		end.getChildren().addAll(new Label("End Time:"), endTimePicker);
		
		VBox notes = new VBox();
		descriptionTextField = new TextField(description);
		notes.getChildren().addAll(new Label("Notes:"), descriptionTextField);
		
		vOuter.getChildren().addAll(start, end, notes);
		return vOuter;
	}
}
