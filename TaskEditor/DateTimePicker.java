/***
 * This code is now part of TornadoFX Controls 
 * and you can have a look at the latest version of DateTimePicker.java in the GitHub Repo.
 * The control is available in Maven Central as well
 * @author Edvin Syse (modified by Tobias Magyar)
 * @source https://stackoverflow.com/questions/28493097/is-there-any-date-and-time-picker-available-for-javafx
 */
package TaskEditor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A DateTimePicker with configurable datetime format where both date and time can be changed
 * via the text field and the date can additionally be changed via the JavaFX default date picker.
 */
class DateTimePicker extends DatePicker {
    public static final String DefaultFormat = "HH:mm dd-MM-yyyy";

    private DateTimeFormatter formatter;
    private ObjectProperty<LocalDateTime> dateTimeValue;
    private ObjectProperty<String> format = new SimpleObjectProperty<String>() {
    	@Override
        public void set(String newValue) {
            super.set(newValue);
            formatter = DateTimeFormatter.ofPattern(newValue);
        }
    };
    
    public DateTimePicker() {
    	this(LocalDateTime.now());
    }

    public DateTimePicker(LocalDateTime dateTime) {
        getStyleClass().add("datetime-picker");
        setFormat(DefaultFormat);
        setConverter(new InternalConverter());
        dateTimeValue = new SimpleObjectProperty<>(dateTime);
        getEditor().setText(dateTime.format(formatter));
        
        // Synchronize changes to the underlying date value back to the dateTimeValue
        valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                dateTimeValue.set(null);
            } else {
                if (dateTimeValue.get() == null) {
                    dateTimeValue.set(LocalDateTime.of(newValue, LocalTime.now()));
                } else {
                    LocalTime time = dateTimeValue.get().toLocalTime();
                    dateTimeValue.set(LocalDateTime.of(newValue, time));
                }
            }
        });

        // Synchronize changes to dateTimeValue back to the underlying date value
        dateTimeValue.addListener((observable, oldValue, newValue) -> {
            setValue(newValue == null ? null : newValue.toLocalDate());
        });

		/*
		 * // Persist changes onblur
		 * getEditor().focusedProperty().addListener((observable, oldValue, newValue) ->
		 * { if (!newValue) simulateEnterPressed(); });
		 */

    }

	/*
	 * private void simulateEnterPressed() { getEditor().fireEvent(new
	 * KeyEvent(getEditor(), getEditor(), KeyEvent.KEY_PRESSED, null, null,
	 * KeyCode.ENTER, false, false, false, false)); }
	 */

    public LocalDateTime getDateTimeValue() {
        return dateTimeValue.get();
    }

    public void setDateTimeValue(LocalDateTime dateTimeValue) {
        this.dateTimeValue.set(dateTimeValue);
    }

    public ObjectProperty<LocalDateTime> dateTimeValueProperty() {
        return dateTimeValue;
    }

    public String getFormat() {
        return format.get();
    }

    public ObjectProperty<String> formatProperty() {
        return format;
    }

    public void setFormat(String format) {
        this.format.set(format);
    }

    class InternalConverter extends StringConverter<LocalDate> {
        public String toString(LocalDate object) {
            LocalDateTime value = getDateTimeValue();
            return (value != null) ? value.format(formatter) : "";
        }

        public LocalDate fromString(String value) {
            try {
            	dateTimeValue.set(LocalDateTime.parse(value, formatter));
            	return dateTimeValue.get().toLocalDate();
            } catch(DateTimeParseException e) {
            	e.printStackTrace();
            	dateTimeValue.set(null);
            	return null;
            }
        }
    }
}