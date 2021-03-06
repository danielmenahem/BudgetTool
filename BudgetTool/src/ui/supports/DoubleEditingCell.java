package ui.supports;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

import bl.Assumption;
import bl.AtomAssumption;
import javafx.application.Platform;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
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
        this.itemProperty().addListener((obs, oldValue, newValue) -> {
            @SuppressWarnings("unchecked")
			TableRow<Assumption> row = this.getTableRow();
            if (row == null) {
                this.setEditable(false);
            }
            else {
                Assumption a = (Assumption) this.getTableRow().getItem();
                if(a == null)
                    this.setEditable(false);
                else if(a instanceof AtomAssumption)
                	this.setEditable(true);
                else
                	this.setEditable(false);
            }
            if(this.isEditable()){
            	this.setStyle(StylePatterns.EDITABLE_TABLE_CELL_CSS);
            }
            else{
            	this.setStyle(StylePatterns.NOT_EDITABLE_TABLE_CELL_CSS);
            }
        });
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
        } 
        else if (isEditing()) {
        	Platform.runLater(()->{            		
        		setText(null);
        		textField.setText(new DecimalFormat("##.##").format(value));
        		setGraphic(textField);
        	});
        } else {
        	Platform.runLater(()->{            		
        		setText(new DecimalFormat("##.##").format(value));
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
    
    public TextField getTextField() {
		return textField;
	}


	@SuppressWarnings("unchecked")
    @Override
    public void commitEdit(Double value) {
        super.commitEdit(value);
        ((T)this.getTableRow().getItem()).setValue(value,index);
        listener.actionOnEvent(new FormEvent<T>((T)this.getTableRow().getItem()));
    }
}
