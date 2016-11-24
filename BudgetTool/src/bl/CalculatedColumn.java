package bl;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class CalculatedColumn extends Column {
	
	private ArrayList <Assumption> assumptions;
	

	public CalculatedColumn(String title, Classification classification, ColumnType columnType,boolean isVisible,String budgetYear) {
		super(title, classification, columnType, isVisible,budgetYear);
		assumptions = new ArrayList<>();
	}
	
	public CalculatedColumn(int id){
		super(id);
		assumptions = new ArrayList<>();
	}
	
	public void addAssumptionToColumn(Assumption assumption) throws Exception{
		
		if(assumption==null)
			throw new Exception("Assumption can't be null");
		
		if(!this.getClassification().equals(assumption.getClassification()))
			throw new Exception("Assumption Calssification must be equal to column Classification");
		
		assumptions.add(assumption);
		assumption.addListener(this);
		recalculateValues(assumption, true);
	}
	
	public void removeAssumption(Assumption assumption) throws Exception{
		boolean isExist = assumptions.remove(assumption);
		if(isExist){
			recalculateValues(assumption, false);
			assumption.removeListeners(this);
		}
		else
			throw new Exception("Selected assumption does not exist in column");
	}
	
	private void recalculateValues(Assumption assumption, boolean isAdded) {
		int mult = 1;
		
		if(!isAdded)
			mult = -1;
		for(int i=1; i<=NUMBER_OF_MONTHS; i++)
			this.setValue(getValue(i) + mult*assumption.getValue(i), i);
		
		setUpdated(false);
		processEvent();
	}
	
	private void calculateValues(){
		for(int i=1; i<=NUMBER_OF_MONTHS;i++){
			double startValue = 0;
			for(int j=0;j<assumptions.size();j++){
				startValue+=assumptions.get(j).getValue(i);
			}
			this.setValue(startValue, i);
		}
		setUpdated(false);
		processEvent();
	}
	
	public void actionPerformed(ActionEvent e){
		calculateValues();
	}
	
	public ArrayList<Assumption> getAssumptions() {
		return assumptions;
	}
}
