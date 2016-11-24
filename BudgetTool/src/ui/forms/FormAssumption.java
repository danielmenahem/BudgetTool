package ui.forms;

import interfaces.AssumptionsMangerIF;
import javafx.scene.layout.Pane;

public class FormAssumption extends Pane{
	
	private AssumptionsMangerIF manager;
	private boolean isPlanning;
	
	public FormAssumption(AssumptionsMangerIF manager, boolean isPlanning){
		this.manager = manager;
		this.isPlanning = isPlanning;
		if(isPlanning)
			this.setStyle("-fx-background-color: yellow;");
		else
			this.setStyle("-fx-background-color: blue;");
	}

}
