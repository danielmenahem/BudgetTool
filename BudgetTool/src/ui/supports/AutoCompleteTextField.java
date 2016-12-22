package ui.supports;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class is a TextField which implements an "autocomplete" functionality,
 * based on a supplied list of entries.
 * 
 * @author Caleb Brinkman
 */
public class AutoCompleteTextField extends TextField {
	/** The existing autocomplete entries. */
	private final SortedSet<String> entries;
	/** The popup used to select an entry. */
	private ContextMenu entriesPopup;
	/**The maximum number of items in the menu*/
	private static final int MAX_ENTRIES = 15;
	
	private static final Comparator<String> IGNORE_CASE = new Comparator<String>() {
	    public int compare(String s1, String s2) {
	        return s1.compareToIgnoreCase(s2);
	    }
	};

	/** Construct a new AutoCompleteTextField. */
	public AutoCompleteTextField() {
		super();
		entries = new TreeSet<>(IGNORE_CASE);
		entriesPopup = new ContextMenu();
		entriesPopup.setAutoFix(true);
		textProperty().addListener(new ChangeListener<String>() {
			private int currentSize = 0;
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				if (getText().length() == 0) {
					entriesPopup.hide();
				}
				else {
					if(getText().length() == 1){
						if(Character.isAlphabetic(getText().toCharArray()[0])){
							char c = Character.toUpperCase(getText().toCharArray()[0]);
							setText(String.valueOf(c));
						}
					}
					LinkedList<String> searchResult = new LinkedList<>();
					searchResult.addAll(entries.subSet(getText(), getText() + Character.MAX_VALUE));
					if (entries.size() > 0) {
						populatePopup(searchResult);
						if(searchResult.size() != currentSize){
							currentSize = searchResult.size();
							entriesPopup.hide();
						}
						if(!entriesPopup.isShowing())
							entriesPopup.show(AutoCompleteTextField.this, Side.RIGHT, 0, 0);
			
					} else {
						entriesPopup.hide();
					}
				}
			}
		});

		focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean,
					Boolean aBoolean2) {
				entriesPopup.hide();
			}
		});

	}

	/**
	 * Get the existing set of autocomplete entries.
	 * 
	 * @return The existing autocomplete entries.
	 * 
	 */
	public SortedSet<String> getEntries() {
		return entries;
	}

	/**
	 * Populate the entry set with the given search results. Display is limited
	 * to 10 entries, for performance.
	 * 
	 * @param searchResult
	 *            The set of matching strings.
	 */
	private void populatePopup(List<String> searchResult) {
		List<CustomMenuItem> menuItems = new LinkedList<>();
		int count = Math.min(searchResult.size(), MAX_ENTRIES);
		for (int i = 0; i < count; i++) {
			final String result = searchResult.get(i);
			Label entryLabel = new Label(result);
			CustomMenuItem item = new CustomMenuItem(entryLabel, true);
			item.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					setText(result);
					entriesPopup.hide();
				}
			});
			menuItems.add(item);
		}
		entriesPopup.getItems().clear();
		entriesPopup.getItems().addAll(menuItems);

	}
}