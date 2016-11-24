package bl;

public class AssumptionType {
	
	public enum Type {
		Costs, Quantity, Percentage
	};
	
	private Type type;

	public AssumptionType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
