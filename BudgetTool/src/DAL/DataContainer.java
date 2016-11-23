package DAL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import BL.*;

public class DataContainer {
	
	private String budgetYear;
	private HashMap<Integer,Assumption> assumptions;
	private HashMap<Integer,Column> columns;
	private HashMap<Integer,Table> tables;
	private BudgetDAL dal;
	
	public DataContainer(boolean isPlanning) throws Exception{
		if(isPlanning)
			this.dal = new BudgetDAL();
		else
			this.dal = new ActingBudgetDAL();
		this.assumptions = new HashMap<>();
		this.columns = new HashMap<>();
		this.tables = new HashMap<>();
	}
	
	public void readAllData(String budgetYear) throws Exception{
		this.budgetYear = budgetYear;
		dal.readAll(this);
	}
	
	public String getBudgetYear() {
		return budgetYear;
	}
	
	public void setBudgetYear(String budgetYear) {
		this.budgetYear = budgetYear;
	}
	
	public HashMap<Integer, Assumption> getAssumptions() {
		return assumptions;
	}

	public void setAssumptions(HashMap<Integer, Assumption> assumptions) {
		this.assumptions = assumptions;
	}

	public HashMap<Integer, Column> getColumns() {
		return columns;
	}

	public void setColumns(HashMap<Integer, Column> columns) {
		this.columns = columns;
	}

	public HashMap<Integer, Table> getTables() {
		return tables;
	}

	public void setTables(HashMap<Integer, Table> tables) {
		this.tables = tables;
	}

	public void addAssumption(Assumption assumption) throws Exception{
		assumption.setId(saveToDB(assumption));
		assumptions.put(assumption.getId(), assumption);
	}
	
	public void addColumn(Column column) throws Exception{
		column.setId(saveToDB(column));
		columns.put(column.getId(), column);
	}
	
	public void addTable(Table table) throws Exception{
		table.setId(saveToDB(table));
		tables.put(table.getId(), table);
	}
	
	public void updateObject(Object item) throws Exception{
		dal.update(item);
	}
	
	public void deleteTable(Table table, boolean deleteInner) throws Exception{
		deleteObject(table, deleteInner);
		tables.remove(table.getId());
		removeColumnFromConteiner(table.getSumColumn(), false);
		if(deleteInner){
			for(Entry <Integer, Column> e : table.getColumns().entrySet()){
				removeColumnFromConteiner(e.getValue(), deleteInner);
			}
		}
		if(table instanceof TrainingTable){
			TrainingTable tTable = (TrainingTable)table;
			for(Entry <Integer, Column> e : tTable.getFixedColumns().entrySet()){
				removeColumnFromConteiner(e.getValue(), true);
			}
			
			for(HashMap<Integer, QuantityColumn> map : tTable.getFactorColumns()){
				for(Entry<Integer, QuantityColumn> e : map.entrySet()){					
					removeColumnFromConteiner(e.getValue(), true);
				}
			}
			
			for(HashMap<Integer, MultColumn> map : tTable.getMultColumns()){
				for(Entry<Integer,MultColumn> e : map.entrySet()){					
					removeColumnFromConteiner(e.getValue(), true);
				}
			}
		}
		
		if(table instanceof ReviewTable){
			ReviewTable rTable = (ReviewTable)table;
			for(HashMap<Integer, MultColumn> map : rTable.getMultColumns()){
				for(Entry<Integer,MultColumn> e : map.entrySet()){					
					removeColumnFromConteiner(e.getValue(), false);
				}
			}	
		}
	}
	
	public void deleteColumn(Column column, boolean deleteInner) throws Exception{
		deleteObject(column, deleteInner);
		removeColumnFromConteiner(column, deleteInner);
	}
	
	public ArrayList<String> getBudgetYears() throws SQLException{
		return dal.getAllBudgetYears();
	}
	
	public ArrayList<String> getAllDepartments() throws SQLException{
		return dal.getAllDepartments();
	}
	
	public ArrayList<String> getAllSubDepartments() throws SQLException{
		return dal.getAllSubDepartments();
	}
	
	public ArrayList<String> addBudgetYear(String budgetYear) throws SQLException{
		dal.createBudgetYear(budgetYear);
		return getBudgetYears();
	}
	
	public ArrayList<String> addDepartment(String department) throws Exception{
		ArrayList<String> departments  = getAllDepartments();
		for(String d : departments){
			if(d == department)
				throw new Exception("Department already exist");
		}
		dal.createDepartment(department);
		departments.add(department);
		return departments;
	}
	
	public ArrayList<String> addSubDepartment(String subDepartment) throws Exception{
		ArrayList<String> subDepartments  = getAllDepartments();
		for(String sd : subDepartments){
			if(sd == subDepartment)
				throw new Exception("Sub Department already exist");
		}
		dal.createDepartment(subDepartment);
		subDepartments.add(subDepartment);
		return subDepartments;
	}
	
	public void copyPlanningToActualDB() throws Exception{
		if(this.dal instanceof ActingBudgetDAL){
			ActingBudgetDAL actingDal = (ActingBudgetDAL)dal;
			actingDal.copySeasonBudget(this.budgetYear);
			actingDal.readAll(this);
		}
		else
			throw new Exception("Copying to actual DB can be done "
					+ "only via the acting budget data container");
	}
	
	private void removeColumnFromConteiner(Column column, boolean deleteInner){
		columns.remove(column.getId());
		removeColumnInformers(column);
		if(deleteInner){
			if(column instanceof MultColumn){
				MultColumn mc = (MultColumn)column;
				for(Column c : mc.getColumns()){
					removeColumnFromConteiner(c, deleteInner);
				}
			}
			else if(column instanceof SummaryColumn){
				SummaryColumn sc = (SummaryColumn)column;
				for(Column c : sc.getColumns()){
					removeColumnFromConteiner(c, deleteInner);
				}
			}
		}
	}
	
	private void removeColumnInformers(Column column) {
		if(column instanceof CalculatedColumn){
			CalculatedColumn cc = (CalculatedColumn)column;
			for(Assumption a : cc.getAssumptions()){
				try {
					a.removeListeners(cc);
				} catch (Exception e) {}
			}
		}
		else if(column instanceof MultColumn){
			MultColumn mc = (MultColumn)column;
			try {
				mc.getAssumption().removeListeners(mc);
			} catch (Exception e) {}
			for(Column c : mc.getColumns()){
				try {
					c.removeListeners(mc);
				} catch (Exception e) {}
			}
		}
		else if (column instanceof SummaryColumn){
			SummaryColumn sc = (SummaryColumn)column;
			for(Column c : sc.getColumns()){
				try {
					c.removeListeners(sc);
				} catch (Exception e) {}
			}
		}
	}

	private void deleteObject(Object item, boolean deleteInner) throws Exception{
		dal.delete(item, deleteInner);
	}
	
	private int saveToDB(Object item) throws Exception{
		return dal.create(item);
	}
	
}
