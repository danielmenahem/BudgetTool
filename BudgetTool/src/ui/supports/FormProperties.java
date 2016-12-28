package ui.supports;

public class FormProperties {
	Class<?> c;
	boolean isPlanning;

	public FormProperties(Class<?> c, boolean isPlanning) {
		this.c = c;
		this.isPlanning = isPlanning;
	}
	public Class<?> getC() {
		return c;
	}
	public boolean isPlanning() {
		return isPlanning;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((c == null) ? 0 : c.hashCode());
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
		FormProperties other = (FormProperties) obj;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		if (isPlanning != other.isPlanning)
			return false;
		return true;
	}

}
