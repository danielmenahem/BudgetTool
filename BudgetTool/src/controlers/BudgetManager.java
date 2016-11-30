package controlers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import bl.*;
import dal.DataContainer;
import interfaces.*;

public class BudgetManager implements EnteranceManagerIF, AssumptionsMangerIF  {
		
	private static BudgetManager singleton;
	
	private DataContainer planningData;
	private DataContainer actualData;
	private ArrayList<String> budgetYears;
	private ArrayList<String> departments;
	private ArrayList<String> subDepartments;
	
	private BudgetManager(){
		try {
			loadInitialInformation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static BudgetManager createBudgetManager() throws Exception{
		if (singleton != null) {
			throw new Exception("You have already initialized a BudgetManager");
		}
		singleton = new BudgetManager();
		return singleton;
	}
	
	public void readAllData(String budgetYear) throws Exception{
		planningData.readAllData(budgetYear);
		actualData.readAllData(budgetYear);
	}
	
	public HashMap<Integer, Assumption> getPlanningAssumptions(){
		return planningData.getAssumptions();
	}
	
	public HashMap<Integer, Column> getPlanningColumns(){
		return planningData.getColumns();
	}
	
	public HashMap<Integer, Table> getPlanningTables(){
		return planningData.getTables();
	}
	
	public HashMap<Integer, Assumption> getActualAssumptions(){
		return actualData.getAssumptions();
	}
	
	public HashMap<Integer, Column> getActualColumns(){
		return actualData.getColumns();
	}
	
	public HashMap<Integer, Table> getActualTables(){
		return actualData.getTables();
	}
	
	private void loadInitialInformation() throws Exception{
		planningData = new DataContainer(true);
		actualData = new DataContainer(false);
		budgetYears = planningData.getBudgetYears();
		departments = planningData.getAllDepartments();
		subDepartments = planningData.getAllSubDepartments();
	}
	
	public ArrayList<String> getBudgetYears() {
		return budgetYears;
	}

	public ArrayList<String> getDepartments() {
		return departments;
	}

	public ArrayList<String> getSubDepartments() {
		return subDepartments;
	}
	
	public void saveAssumtionToPlanning(Assumption assumption) throws Exception{
		planningData.addAssumption(assumption);
	}
	
	public void saveAssumptionToActual(Assumption assumption) throws Exception{
		actualData.addAssumption(assumption);
	}
	
	public void saveColumnToPlanning(Column column) throws Exception{
		planningData.addColumn(column);
	}
	
	public void saveColumnToActual(Column column) throws Exception{
		actualData.addColumn(column);
	}
	
	public void saveTableToPlanning(Table table) throws Exception{
		planningData.addTable(table);
	}
	
	public void saveTableToActual(Table table) throws Exception{
		actualData.addTable(table);
	}
	
	public void updateAssumptionInPlanning(Assumption assumption){
		try {
			planningData.updateObject(assumption);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateAssumptionInActual(Assumption assumption){
		try {
			actualData.updateObject(assumption);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createNewBudgetYear(String bYear) throws SQLException{
		this.budgetYears = actualData.addBudgetYear(bYear);
	}
	
}


