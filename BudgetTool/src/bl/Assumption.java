package bl;

public class Assumption extends Item {
	
	private AssumptionType type;
	private String assumptionType;
	

	public String getAssumptionType() {
		return assumptionType;
	}

	public void setAssumptionType(String assumptionType) {
		this.assumptionType = assumptionType;
	}

	public Assumption(String title, double value, Classification classification, AssumptionType type, String budgetYear) {
		super(title, classification, budgetYear);
		this.setValues(value);
		this.type = type;
	}
	
	public Assumption(){
		super();
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
