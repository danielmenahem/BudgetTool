package ui.forms;

import interfaces.TablesManagerIF;

public class FormTable extends Form {
	
	private TablesManagerIF manager;
	
	public FormTable(TablesManagerIF manager, boolean isPlanning, double width) {
		super(isPlanning, width);
		this.manager = manager;
	}

}
