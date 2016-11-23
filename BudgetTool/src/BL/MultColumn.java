package BL;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class MultColumn extends Column {
	
	private ArrayList<Column> columns;
	private Assumption assumption;
	
	public ArrayList<Column> getColumns() {
		return columns;
	}

	public MultColumn(String title, Classification classification, ColumnType columnType, boolean isVisible,String budgetYear) {
		super(title, classification, columnType, isVisible,budgetYear);
		columns = new ArrayList<>();
	}
	
	public MultColumn(int id){
		super(id);
		columns = new ArrayList<>();
	}
	
	public void addColumn (Column column) throws Exception{
		if(column==null)
			throw new Exception("Added column can't be null!");
		if(!this.getClassification().equals(column.getClassification()))
			if(!column.getClassification().getSubDepartment().equals("Training"))
				throw new Exception("Inserted column classification must be equal to column classification ");
		
		columns.add(column);
		column.addListener(this);
		calculateValues();
	}
	
	public void removeColumn(Column column) throws Exception{
		boolean isExist = columns.remove(column);
		if(!isExist)
			throw new Exception("Column does not exist in list");
		else
			column.removeListeners(this);
		calculateValues();
	}
	
	public void addAssumption(Assumption assumption) throws Exception{
		if(this.assumption!=null)
			throw new Exception("This column allready have an assumption");
		if(assumption==null)
			throw new Exception("Added assumption can't be null");
		
		this.assumption = assumption;
		assumption.addListener(this);
		calculateValues();
	}
	
	public void removeAssumption() throws Exception{
		this.assumption.removeListeners(this);
		this.assumption = null;
		
		calculateValues();
	}
	
	private void calculateValues() {
		
		double startValues [] = new double[NUMBER_OF_MONTHS];
		if(assumption!=null){
			for(int i=1;i<=NUMBER_OF_MONTHS;i++){
				startValues[i-1] = assumption.getValue(i);
				this.setValue(startValues[i-1], i);
			}
		}
		else{
			for(int i=0;i<NUMBER_OF_MONTHS;i++)
				startValues[i] = 1;
		}
		
		for(int i=1;i<=NUMBER_OF_MONTHS;i++){
			for(int j=0;j<columns.size();j++){
				startValues[i-1] *= columns.get(j).getValue(i);
			}
			this.setValue(startValues[i-1], i);
		}
		setUpdated(false);
		processEvent();
	}
	
	public Assumption getAssumption() {
		return assumption;
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		calculateValues();
	}

}
