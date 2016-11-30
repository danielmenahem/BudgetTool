package interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import bl.Assumption;

public interface AssumptionsMangerIF {
	
	void saveAssumptionToActual(Assumption assumption) throws Exception;
	void saveAssumtionToPlanning(Assumption assumption) throws Exception;
	HashMap<Integer, Assumption> getActualAssumptions();
	HashMap<Integer, Assumption> getPlanningAssumptions();
	ArrayList<String> getBudgetYears();
	ArrayList<String> getDepartments();
	ArrayList<String> getSubDepartments();
	void updateAssumptionInActual(Assumption assumption);
	void updateAssumptionInPlanning(Assumption assumption);
}
