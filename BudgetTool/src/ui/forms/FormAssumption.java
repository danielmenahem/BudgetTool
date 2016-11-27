package ui.forms;

import interfaces.AssumptionsMangerIF;

public class FormAssumption extends Form{
	
	private AssumptionsMangerIF manager;

	public FormAssumption(AssumptionsMangerIF manager, boolean isPlanning){
		super(isPlanning);
		this.manager = manager;
		if(isPlanning)
			this.setStyle("-fx-background-color: yellow;");
		else
			this.setStyle("-fx-background-color: blue;");
	}
}
