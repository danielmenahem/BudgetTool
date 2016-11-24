package bl;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class SummaryColumn extends Column {
	
	ArrayList <Column> columns;
	

	public SummaryColumn(String title, Classification classification, boolean isVisible, String budgetYear) {
		super(title, classification, ColumnType.costs, isVisible, budgetYear);
		columns = new ArrayList<>();
	}
	
	public SummaryColumn(int id){
		super(id);
		columns = new ArrayList<>();
	}
	
	public void addColumn (Column column) throws Exception{
		if(column==null)
			throw new Exception("Inserted column can't be null");
		if(!column.getClassification().equals(this.getClassification()))
			throw new Exception("Inserted column classification must be equal to column classification ");
		if(!(column.getColumnType() == ColumnType.costs))
			throw new Exception("Inserted column type must be costs ");

		columns.add(column);
		column.addListener(this);
		recalculateValues(column, true);
		
	}
	
	public void removeColumn(Column column) throws Exception{
		boolean isExist = columns.remove(column);
		if(isExist){
			recalculateValues(column, false);
			column.removeListeners(this);
		}
		else
			throw new Exception("Selected assumption does not exist in column");
	}
	
	private void recalculateValues(Column column, boolean isAdded) {
		int mult = 1;
		if(!isAdded)
			mult = -1;
		for(int i=1 ; i<NUMBER_OF_MONTHS ;i++)
			this.setValue(getValue(i) + mult*column.getValue(i), i);
		
		setUpdated(false);
		processEvent();
	}
	


	@Override
	public void setColumnType(ColumnType columnType){

	}
	
	public void calculateValues(){
		for(int i=1; i<=NUMBER_OF_MONTHS;i++){
			double startValue = 0;
			for(int j=0;j<columns.size();j++){
				startValue+=columns.get(j).getValue(i);
			}
			this.setValue(startValue, i);
		}
		
		setUpdated(false);
		processEvent();
	}
	
	public ArrayList<Column> getColumns() {
		return columns;
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		calculateValues();
	}
}
