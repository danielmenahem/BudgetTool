package ui.forms;

import javafx.scene.layout.Pane;

public abstract class Form  extends Pane{
	private boolean isPlanning;
	
	public Form(boolean isPlanning){
		this.isPlanning = isPlanning;
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
