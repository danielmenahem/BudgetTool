package interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import bl.Assumption;
import bl.AssumptionType.Type;
import bl.CalculatedAssumption.Action;

public interface AssumptionsManagerIF {
	
	void saveAssumptionToActual(Assumption assumption) throws Exception;
	void saveAssumtionToPlanning(Assumption assumption) throws Exception;
	HashMap<Integer, Assumption> getActualAssumptions();
	HashMap<Integer, Assumption> getPlanningAssumptions();
	ArrayList<String> getBudgetYears();
	ArrayList<String> getDepartments();
	ArrayList<String> getSubDepartments();
	void updateAssumptionInActual(Assumption assumption);
	void updateAssumptionInPlanning(Assumption assumption);
	Assumption createAtomAssumptionInPlanning(String title, String department, String subDepartment, double value, Type type) throws Exception;
	Assumption createAtomAssumptionInActual(String title, String department, String subDepartment, double value, Type type) throws Exception;
	Assumption createClaculatedAssumptionInPlanning(String title, String department, String subDepartment, Action action, Type type) throws Exception;
	Assumption createCalculatedAssumptionInActual(String title, String department, String subDepartment, Action action, Type type) throws Exception;
}
