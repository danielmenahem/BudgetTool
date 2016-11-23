package BL;

public abstract class Assumption extends Item {
	
	private AssumptionType type;

	public Assumption(String title, double value, Classification classification, AssumptionType type, String budgetYear) {
		super(title, classification, budgetYear);
		this.setValues(value);
		this.type = type;
	}
	
	public Assumption (int id){
		super(id);
	}

	public AssumptionType getType() {
		return type;
	}

	public void setType(AssumptionType type) {
		this.type = type;
		setUpdated(false);
	}
}
