package bl;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class CalculatedAssumption extends Assumption {
	public enum Action{
		add, sub, mult
	};
	
	public enum SpecialOperation{
		none, dev, sub
	};
	
	private Action action;
	private ArrayList<Assumption> assumptions;
	
	private Assumption specialOperationAssumption;
	private SpecialOperation specialOperationAction;

	public CalculatedAssumption(String title, Classification classification, AssumptionType type,
			Action action, String budgetYear) {
		super(title, 0, classification, type, budgetYear);
		this.action = action;
	
		assumptions = new ArrayList<>();
		setSpecialOperationAction(SpecialOperation.none); 
	}
	
	public CalculatedAssumption(int id){
		super(id);
		assumptions = new ArrayList<>();
		setSpecialOperationAction(SpecialOperation.none); 
	}
	
	public void addAssumption(Assumption assumption) throws Exception{
		if(assumption.getClassification().getDepartment().equals(this.getClassification().getDepartment())){
			if(assumption.getType().getType() == AssumptionType.Type.Percentage && this.action != Action.mult)
				throw new Exception("Insertion of a percentage assumption allowed only when action = mult ");
			assumptions.add(assumption);
			assumption.addListener(this);
			calculateValues();
		}
		else
			throw new Exception("Insertion of an assumption with a different department is not allowed");
	}
	
	public void removeAssumption(Assumption assumption) throws Exception{
		assumptions.remove(assumption);
		assumption.removeListeners(this);
		calculateValues();
	}
	
	private void calculateValues(){
		if(action==Action.add)
			addValues();
		else if (action==Action.sub)
			subValues();
		else if (action==Action.mult)
			multValues();
		
		if(specialOperationAction == SpecialOperation.dev){
			for(int i=1;i<=NUMBER_OF_MONTHS;i++){
				this.setValue(this.getValue(i)/specialOperationAssumption.getValue(i), i);
			}
		}
		
		else if(specialOperationAction == SpecialOperation.sub){
			for(int i=1;i<=NUMBER_OF_MONTHS;i++){
				this.setValue(this.getValue(i)-specialOperationAssumption.getValue(i), i);
			}
		}
		setUpdated(false);
		processEvent();
	}

	private void multValues() {
		for(int i=1;i<=NUMBER_OF_MONTHS;i++){
			double startValue = 0;
			if(assumptions.size()>0){
				startValue = 1;
				for(int j=0;j<assumptions.size();j++){
					startValue *= assumptions.get(j).getValue(i);
					if(assumptions.get(j).getType().getType() == AssumptionType.Type.Percentage)
						startValue /=100;
				}
			}
			super.setValue(startValue, i);
			startValue = 1;
		}
	}

	private void subValues() {
		for(int i=1;i<=NUMBER_OF_MONTHS;i++){
			double startValue = 0;
			for(int j=0;j<assumptions.size();j++){
				startValue -= assumptions.get(j).getValue(i);
			}
			super.setValue(startValue, i);
			startValue = 0;
		}
	}

	private void addValues() {
		for(int i=1;i<=NUMBER_OF_MONTHS;i++){
			double startValue = 0;
			for(int j=0;j<assumptions.size();j++){
				startValue += assumptions.get(j).getValue(i);
			}
			super.setValue(startValue, i);
			startValue = 0;
		}
	}

	public SpecialOperation getSpecialOperationAction() {
		return specialOperationAction;
	}

	private void setSpecialOperationAction(SpecialOperation specialOperationAction) {
		this.specialOperationAction = specialOperationAction;
		setUpdated(false);
	}

	public Assumption getSpecialOperationAssumption() {
		return specialOperationAssumption;
	}

	public void setSpecialOperationAssumption(Assumption specialOperationAssumption, SpecialOperation specialOperationAction ) throws Exception{
		if(specialOperationAction==null)
			throw new Exception("Special operation can't be null");
		
		if(specialOperationAction==null || specialOperationAction == SpecialOperation.none)
			throw new Exception("Special operation action can't be none or null");
		 
		if(this.specialOperationAction == SpecialOperation.none){
			if(specialOperationAction == SpecialOperation.dev){
				for(int i = 1; i<=NUMBER_OF_MONTHS;i++){
					if(specialOperationAssumption.getValue(i)==0)
						throw new Exception("a Special operation with dev action can't contain zero values");
				}
			}
			this.specialOperationAction = specialOperationAction;
			this.specialOperationAssumption = specialOperationAssumption;
			specialOperationAssumption.addListener(this);
			calculateValues();
			
		}
		else
			throw new Exception("Special operation already exists");
	}
	
	public void removeSpecialOperation() throws Exception{
		if(specialOperationAction != null){
			specialOperationAssumption.removeListeners(this);
			this.specialOperationAction = null;
			setSpecialOperationAction(SpecialOperation.none);
			calculateValues();
		}
	}
	
	public Action getAction() {
		return action;
	}

	public ArrayList<Assumption> getAssumptions() {
		return assumptions;
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		calculateValues();
	}
	
	public void setAction(Action action){
		this.action = action;
		calculateValues();
		processEvent();
	}
}
