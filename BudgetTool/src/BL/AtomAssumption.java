package BL;

public class AtomAssumption extends Assumption {
	private boolean isPeriodical;
	
	
	public AtomAssumption(String title, double value, Classification classification, AssumptionType type, boolean isPeriodical, String budgetYear) {
		super(title, value, classification, type, budgetYear);
		this.isPeriodical = isPeriodical;
	}
	
	public AtomAssumption(int id){
		super(id);
	}
	
	public void setValueToMonth(double value, int month) throws Exception{
		if(month>=0 && month<=NUMBER_OF_MONTHS){
			super.setValue(value, month);
			processEvent();
		}
		else
			throw new Exception(MONTH_NOT_VALID);

	}
	
	public boolean isPerdiocal() {
		return isPeriodical;
	}

	public void setPerdiocal(boolean isPeriodical) {
		this.isPeriodical = isPeriodical;
		setUpdated(false);
	}
}
