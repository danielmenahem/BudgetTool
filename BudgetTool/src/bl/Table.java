package bl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import bl.Column.ColumnType;

public class Table {
	
	public final String SUM_COLUMN_NAME = "Total";
	
	private int id;
	
	private Classification classificaion;
	private int colIdInTable;
	private String budgetYear;

	private HashMap<Integer,Column> columns;
	private SummaryColumn sumColumn;
	
	public Table(Classification classification, String budgetYear){
		this.classificaion = classification;
		this.budgetYear = budgetYear;
		columns = new HashMap<>();
		colIdInTable = 0;
		sumColumn = new SummaryColumn(SUM_COLUMN_NAME, classification,true, true, this.budgetYear);
	}
	
	public Table(int id, Classification classification, String budgetYear,SummaryColumn column, int colIndex){
		this.id = id;
		this.classificaion = classification;
		this.budgetYear = budgetYear;
		this.sumColumn = column;
		this.colIdInTable = colIndex;
	}
	
	public void addColumnToTable(Column column, boolean isVisible) throws Exception{
		if(column==null)
			throw new Exception("Column can't be null");
		if(!this.getClassificaion().equals(column.getClassification()))
			throw new Exception("Column classification must be equal to table classification");
		if(columns.containsKey(column))
			throw new Exception("Column already exist");
		
		colIdInTable++;
		columns.put(colIdInTable, column);
		if(column.getColumnType()==ColumnType.cost_for_Summary)
			this.sumColumn.addColumn(column);
	}
	
	public void removeColumnFromTable(int columnNumber) throws Exception{
		Column column = columns.remove(columnNumber);
		if(column==null)
			throw new Exception("Column does not Exist");
		
		if(column.getColumnType()==ColumnType.costs)
			this.sumColumn.removeColumn(column);
	}
	
	public void changeColumnVisibility(int columnNumber, boolean isVisible) throws Exception{
		boolean isExist = columns.containsKey(columnNumber);
		if(!isExist)
			throw new Exception("Column does not exist in table");
	
		columns.get(columnNumber).setVisible(isVisible);
	}
	
	public ArrayList<Column> getVisibleColumns(){
		
		ArrayList<Column> columnsList = new ArrayList<>();
		
		for( Entry<Integer,Column> e : columns.entrySet()){
			if(e.getValue().isVisible()==true)
				columnsList.add(e.getValue());
		}
		
		return columnsList; 
	}
	

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Classification getClassificaion() {
		return classificaion;
	}
	
	public int getColIdInTable() {
		return colIdInTable;
	}

	public ArrayList<Column> getColumnsAsArrayList() {
		return (ArrayList<Column>) columns.values();
	}
	
	public HashMap<Integer, Column> getColumns(){
		return columns;
	}

	public SummaryColumn getSumColumn() {
		return sumColumn;
	}
	
	public void increaseNumberOfColumns(){
		this.colIdInTable++;
	}
	
	public String getBudgetYear() {
		return budgetYear;
	}

	public void setBudgetYear(String budgetYear) {
		this.budgetYear = budgetYear;
	}
}
