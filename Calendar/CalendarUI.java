package Calendar;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import util.Task;


public class CalendarUI extends Application {

	private static final String BUTTON_DEFAULT_CSS = "";
	private static final String BUTTON_TASK_CSS = "-fx-color: Yellow";
	@SuppressWarnings("unused")
	private Stage primaryStage;
	private Calendar runtime;
	private BorderPane mainPane;
	// Contains all active buttons representing days in the currently selected month;
	private List<Button> days;
	Label currentDateLabel;
	
	// Accessed by Calendar.java
	TableView<Task> taskView;
	ComboBox<Month> monthSelector;
	ComboBox<Integer> yearSelector;
	
	// ¯\_(ツ)_/¯
	public CalendarUI() {
		super();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setOnCloseRequest(arg0 -> runtime.saveData());
		primaryStage.setResizable(false);
		runtime = new Calendar(this);
		mainPane = buildBorderPane();
		Scene scene = new Scene(mainPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Calendar");
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	private BorderPane buildBorderPane() {
		BorderPane mainPane = new BorderPane();
		mainPane.setTop(buildTopPane());
		mainPane.setCenter(buildCenterPane());
		
		return mainPane;
	}
	
	// Section TopPane
	private Node buildTopPane() {
		// Section: Layout
		VBox vOuter = new VBox();
		vOuter.setAlignment(Pos.TOP_LEFT);
		
		VBox vInternalTop = new VBox();
		HBox hInternalBottom = new HBox();
		vOuter.getChildren().addAll(vInternalTop, hInternalBottom);
		
		VBox rightBox = new VBox();
		rightBox.setAlignment(Pos.TOP_RIGHT);
		
		// Section: Contents
		currentDateLabel = new Label();
		currentDateLabel.setAlignment(Pos.BOTTOM_CENTER);
		vInternalTop.getChildren().addAll(generateSelectors(), currentDateLabel);
		
		rightBox.getChildren().addAll(generateButtons());
		rightBox.setPrefWidth(61); //Button Width
		hInternalBottom.getChildren().addAll(generateTasksTable(), rightBox);
		
		
		return vOuter;
	}

	private Node generateSelectors() {
		HBox selectors = new HBox(5);
		
		monthSelector = new ComboBox<>();
		monthSelector.getItems().addAll(Month.values());
		monthSelector.setValue(runtime.getSelectedDate().getMonth());
		monthSelector.setOnAction(ev -> { mainPane.setCenter(buildCenterPane()); });
		monthSelector.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> { System.out.println(newValue); });
		
		yearSelector = new ComboBox<>();
		int currentYear = runtime.getSelectedDate().getYear();
		Integer[] years = new Integer[4];
		for (int i = 0; i < years.length; i++) {
			years[i] = (currentYear - 2) + i;
		}
		yearSelector.getItems().addAll(years);
		yearSelector.setValue(currentYear);
		yearSelector.setOnAction(ev -> { mainPane.setCenter(buildCenterPane()); });
		
		selectors.getChildren().addAll(monthSelector, yearSelector);
		return selectors;
	}
	
	private List<Node> generateButtons() {
		List<Node> list = new LinkedList<Node>();
		double width = 60;
		
		Button n = new Button("New");
		n.setMinWidth(width);
		n.setOnAction(ev -> { runtime.ButtonNew(ev); } );
		Button edit = new Button("Edit");
		edit.setMinWidth(width);
		edit.setOnAction(ev -> { runtime.ButtonEdit(ev, taskView.getSelectionModel().getSelectedItem()); } );
		Button delete = new Button("Delete");
		delete.setMinWidth(width);
		delete.setOnAction(ev -> { runtime.ButtonDelete(ev, taskView.getSelectionModel().getSelectedItem()); } );
		
		
		list.add(n);
		list.add(edit);
		list.add(delete);
		
		return list;
	}

	private Node generateTasksTable() {
		taskView = new TableView<Task>();
		taskView.setPrefHeight(200);
		// 350.0 is the minSize of the day buttons * 7
		// 61.0 is the minSize of the control-buttons for the TasksTable
		taskView.setMinWidth(350.0 - 61.0);
		taskView.setEditable(false);
		taskView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		taskView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		taskView.setSortPolicy(new Callback<TableView<Task>, Boolean>() {
			
			@Override
			public Boolean call(TableView<Task> tView) {
				ObservableList<Task> tasks = tView.getItems();
				try { 
					tasks.sort(null);
					tView.setItems(tasks);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		});
		
		/* // Attempting to do some CSS here, but too much effort.
		 * final PseudoClass strikethrough = PseudoClass.getPseudoClass("strikethrough");
		 * taskView.setRowFactory(taskView -> new TableRow<Task>() {
		 * 
		 * 	@Override
		 * 	protected void updateItem(Task task, boolean empty) {
		 * 		super.updateItem(task, empty);
		 * 		pseudoClassStateChanged(strikethrough, task != null && task.archived); 
		 * 	} 
		 * });
		 */
		
		
		TableColumn<Task, String> time = new TableColumn<>("Start Time");
		time.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Task, String> task) {
				LocalDateTime startTime = task.getValue().startTime;
				Integer minute = startTime.getMinute();
				String minuteString = minute < 10 ? "0" + minute.toString() : minute.toString();
				return new SimpleStringProperty(startTime.getHour() + ":" + minuteString);
			}
		});
		TableColumn<Task, String> duration = new TableColumn<>("Duration");
		duration.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Task, String> task) {
				Duration duration = task.getValue().duration;
				return new SimpleStringProperty(((int)duration.getSeconds() / 60) + " min");
			}
		});
		TableColumn<Task, String> description = new TableColumn<>("Description");
		description.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Task, String> task) {
				return new SimpleStringProperty(task.getValue().description);
			}
		});
		
		taskView.getColumns().add(time);
		taskView.getColumns().add(duration);
		taskView.getColumns().add(description);
		
		runtime.populateTableView();
		
		return taskView;
	}

	// Section End TopPane
	// Section CenterPane
	private Node buildCenterPane() {
		// Day View
		int year = yearSelector.getValue();
		Month month = monthSelector.getValue();
		boolean isLeapYear = year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
		int daysInMonth = month.length(isLeapYear);
		int firstMonday = 0;
		for (int i = 1; i <= 7; i++) {
			LocalDate lDate = LocalDate.of(year, month, i);
			if (lDate.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
				firstMonday = i;
				break;
			}
		}
		
		//This updates only the month and year, keeping the same day for purpose of the Task-Table
		runtime.changeSelectedDay(0);
		
		List<Integer> eventDays = runtime.findTaskDays(month, year);
		List<HBox> weekList = new LinkedList<>();
		HBox firstWeek = new HBox();
		firstWeek.getChildren().addAll(generatePrologueButtons(8 - firstMonday, month, isLeapYear));
		weekList.add(firstWeek);
		
		this.days = new LinkedList<Button>();
		for(int i = 1; i <= daysInMonth; i++ ) {
			Integer day = i; //needs to be final for Action Event
			Button btn = new Button();
			btn.setText(day.toString());
			btn.setOnAction(ev -> { 
				runtime.changeSelectedDay(day); 
			});
			if (eventDays.contains(day)) {
				btn.setStyle(BUTTON_TASK_CSS);
			}
			this.days.add(btn);
			
			if (LocalDate.of(year, month, i).getDayOfWeek().equals(DayOfWeek.MONDAY)) {
				weekList.add(new HBox());
			}
			weekList.get(weekList.size()-1).getChildren().add(btn);
		}
		
		HBox lastWeek = weekList.get(weekList.size()-1);
		lastWeek.getChildren().addAll(fillWithButtons(new Button[7 - lastWeek.getChildren().size()], 1));
		
		for(HBox hbox : weekList) {
			for(Node node : hbox.getChildren()) {
				Button btn = (Button)node;
				btn.setMinSize(50, 50);
			}
		}
		
		VBox vbox = new VBox();
		vbox.getChildren().addAll(weekList);
		return vbox;
	}

	private Button[] generatePrologueButtons(int firstMonday, Month month, boolean isLeapYear) {
		int prevMonthLength = month.minus(1).length(isLeapYear);
		Integer startDay = (prevMonthLength - firstMonday) + 1;
		Button[] fillerButtons = new Button[firstMonday];
		
		return fillWithButtons(fillerButtons, startDay);
	}
	
	private Button[] fillWithButtons(Button[] toFill, Integer startDay) {
		for (int i = 0; i < toFill.length; i++) {
			Button btn = new Button();
			btn.setText(startDay.toString());
			btn.setDisable(true);
			toFill[i] = btn;
			startDay++;
		}
		return toFill;
	}
	
	/**
	 * enables the css for days with existing tasks.
	 * @param day the int representation
	 */
	void toogleButtonColored(int day) {
		days.get(day - 1).setStyle(BUTTON_TASK_CSS);
	}
	
	/**
	 * disables the css for days with existing tasks.
	 * @param day the int representation
	 */
	void toogleButtonDefault(int day) {
		days.get(day - 1).setStyle(BUTTON_DEFAULT_CSS);
	}
	// Section End CenterPane
}
