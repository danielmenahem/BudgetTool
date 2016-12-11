package ui;

import bl.Assumption;
import interfaces.AssumptionsManagerIF;
import javafx.stage.Stage;

public class BuildComplexAssumption extends Stage{
	
	private AssumptionsManagerIF manager;
	private Assumption assumption;
	
	public BuildComplexAssumption(AssumptionsManagerIF manager, Assumption assumption) {
		super();
		this.manager = manager;
		this.assumption = assumption;
		this.setAlwaysOnTop(true);
	}
}
