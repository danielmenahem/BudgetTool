package ui.forms;

import javafx.scene.layout.Pane;
import ui.supports.StylePatterns;

public abstract class Form  extends Pane{
	public static final int VALUE_COLUMN_WIDTH  = 50;
	public static final int STRING_COLUMN_WIDTH  = 100;
	
	private boolean isPlanning;
	private double formWidth;
	public Form(boolean isPlanning, double width){
		this.isPlanning = isPlanning;
		this.formWidth = width;
		if(isPlanning)
			this.setStyle(StylePatterns.PLANNING_BACKGROUND_CSS);
		else
			this.setStyle(StylePatterns.ACTUAL_BACKGROUND_CSS);
	}
	
	public double getFormWidth() {
		return formWidth;
	}


	public void setFormWidth(double width) {
		this.formWidth = width;
	}



	public boolean isPlanning() {
		return isPlanning;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isPlanning ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Form other = (Form) obj;
		if (isPlanning != other.isPlanning)
			return false;
		return true;
	}
}
