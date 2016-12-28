package bl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import bl.Column.ColumnType;

public class ReviewTable extends Table {
	
	TrainingTable trainingTable;
	private ArrayList<HashMap<Integer,MultColumn>> multColumns;

	public ReviewTable(Classification classification, String budgetYear) {
		super(classification, budgetYear);
		multColumns = new ArrayList<>();

	}
	
	public ReviewTable(int id, Classification classification, String budgetYear, SummaryColumn column, int colIndex){
		super(id, classification, budgetYear, column, colIndex);
		multColumns = new ArrayList<>();
	}
	
	public void setTrainingTable(TrainingTable trainingTable) throws Exception{
		if(this.trainingTable!=null)
			throw new Exception("Training table already exist!");
		
		this.trainingTable = trainingTable;
		ArrayList<HashMap<Integer, MultColumn>> trainingCostColumns  = trainingTable.getMultColumns();
		multColumns = new ArrayList<>();
		int num = 0;
		
		for(int i = 0;i<trainingCostColumns.size();i++){
			HashMap<Integer, MultColumn> reviewCosts = new HashMap<>(); 
			HashMap<Integer, MultColumn> trainingCosts = trainingCostColumns.get(i);
			
			for(int j=1;i<=trainingCostColumns.size();j++){
				MultColumn mc = new MultColumn(trainingCosts.get(j+num).getTitle(), this.getClassification(), ColumnType.cost_for_Summary, true, this.getBudgetYear());
				
				for(int k=0;k<trainingCosts.get(j+num).getColumns().size();k++){
					mc.addColumn(trainingCosts.get(j+num).getColumns().get(k));
				}
				
				this.increaseNumberOfColumns();
				this.getSumColumn().addColumn(mc);
				reviewCosts.put(this.getColIdInTable(), mc);
			}
			num += trainingCostColumns.size();
			multColumns.add(reviewCosts);
		}
	}
	
	public void addPeriodCostAssumption(Assumption assumption, int period) throws Exception{
		if (assumption==null)
			throw new Exception("Assumption can't be null");
		if(trainingTable==null)
			throw new Exception("Can't insert review on training assumption before training table");
		if(assumption.getType().getType()!=AssumptionType.Type.Costs)
			throw new Exception("Assumption type must be costs");
		if(period>trainingTable.getNumberOfPeriods()||period<1)
			throw new Exception("Period must be a value between 1 to "+trainingTable.getNumberOfPeriods());
		for(Entry<Integer,MultColumn> e: multColumns.get(period-1).entrySet()){
			try{
				e.getValue().addAssumption(assumption);
			}
			catch(Exception ex){
				e.getValue().removeAssumption();
				e.getValue().addAssumption(assumption);
			}
		}
	}
	
	public void trainingTable(TrainingTable trainingTable){
		this.trainingTable = trainingTable;
		multColumns = new ArrayList<>();
		for(int i = 0;i<trainingTable.getNumberOfPeriods();i++){
			multColumns.add(new HashMap<>());
		}
	}
	
	public TrainingTable getTrainingTable() {
		return trainingTable;
	}

	public ArrayList<HashMap<Integer, MultColumn>> getMultColumns() {
		return multColumns;
	}
}
