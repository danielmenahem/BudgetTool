package ui.supports;

import java.util.regex.Pattern;

import bl.AtomAssumption;
import javafx.application.Platform;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import ui.interfaces.FormListener;
import bl.Item;

public class DoubleEditingCell<T extends Item> extends TableCell<T, Double> {

    private final TextField textField = new TextField();
    private final Pattern intPattern = Pattern.compile("\\d+\\.?\\d*");
    private final int index;
    private FormListener<T> listener;
    public DoubleEditingCell(int index, FormListener<T> listener) {
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (! isNowFocused) {
                processEdit();
            }
        });
        textField.setOnAction(event -> processEdit());
        this.index = index;
        this.listener = listener;
    }

    private void processEdit() {
        String text = textField.getText();
        if (intPattern.matcher(text).matches()) {
            commitEdit(Double.parseDouble(text));
        } else {
            cancelEdit();
        }
    }

    @Override
    public void updateItem(Double value, boolean empty) {
        super.updateItem(value, empty);
        if (empty) {
        	Platform.runLater(()->{            		
        		setText(null);
        		setGraphic(null);
        	});
        } else if (isEditing()) {
        	Platform.runLater(()->{            		
        		setText(null);
        		textField.setText(value.toString());
        		setGraphic(textField);
        	});
        } else {
        	Platform.runLater(()->{            		
        		setText(value.toString());
        		setGraphic(null);
        	});
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        Number value = getItem();
        if (value != null && this.getTableRow().getItem() instanceof AtomAssumption) {
        	Platform.runLater(()->{            		
        		textField.setText(value.toString());
        		setGraphic(textField);
        		setText(null);
        	});
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        Platform.runLater(()->{            	
        	setText(getItem().toString());
        	setGraphic(null);
        });
    }
    
    @Override
    public void commitEdit(Double value) {
        super.commitEdit(value);
        ((T)this.getTableRow().getItem()).setValue(value,index);
        listener.actionOnEvent(new FormEvent<T>((T)this.getTableRow().getItem()));
    }
}
