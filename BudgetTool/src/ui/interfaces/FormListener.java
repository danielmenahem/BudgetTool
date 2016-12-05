package ui.interfaces;

import ui.supports.FormEvent;
import bl.Item;

public interface FormListener<T extends Item> {

	void actionOnEvent(FormEvent<T> e);
	
}