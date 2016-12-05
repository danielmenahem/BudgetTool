package bl;

public class Assumption extends Item implements Comparable{
	
	private AssumptionType type;
	

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

	@Override
	public int compareTo(Object o) {
		if(o instanceof Assumption){
			Assumption other = (Assumption)o;
			if(this.getId()==other.getId())
				return 0;
			if(this.getClassification().equals(other.getClassification())){
				if(this instanceof CalculatedAssumption && other instanceof AtomAssumption)
					return 1;
				if(this instanceof AtomAssumption && other instanceof CalculatedAssumption)
					return -1;
				return this.getId()-this.getId();
			}
			return this.getClassification().compareTo(other.getClassification());
		}
		return 1;
	}
}
