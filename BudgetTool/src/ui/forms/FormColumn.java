package ui.forms;

import bl.Column;
import interfaces.ColumnsManagerIF;
import ui.interfaces.FormListener;
import ui.supports.FormEvent;
import ui.supports.StylePatterns;

public class FormColumn extends Form implements FormListener<Column>{

	private ColumnsManagerIF manager;
	
	
	public FormColumn(ColumnsManagerIF manager, boolean isPlanning, double formWidth) {
		super(isPlanning, formWidth);
		this.manager = manager;
	}
	
	@Override
	public void actionOnEvent(FormEvent<Column> e) {
		// TODO Auto-generated method stub
	}
}
