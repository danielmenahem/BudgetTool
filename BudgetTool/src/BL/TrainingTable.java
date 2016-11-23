package BL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import BL.Column.ColumnType;

public class TrainingTable extends Table {
	public static final String SUB_DEPARTMENT = "Training";
	public static final String [] WEEK_TITLES = {"28-05","06-12","13-19","20-27"};
	public static final String [] PREV_MONTH_WEEK_TITLE = {" - Previus Month"," - 2 Months Before"," - 3 Months Before", " - 4 Months Before"};
	public static final String FACTOR_TITLE = " - factor";
	public static final String COST_TITLE = " - costs";
	public static final int NUMBER_OF_WEEKS = 4;
	
	private int numberOfPeriods;
	private int totalPeriodDuration;
	private int periodsDuration [];
	private HashMap<Integer, Column> fixedColumns;
	private ArrayList<HashMap<Integer,QuantityColumn>> factorColumns;
	private ArrayList<HashMap<Integer,MultColumn>> multColumns;
	
	public TrainingTable(String department,int periodsDuration [], String budgetYear) throws Exception {
		super(new Classification(department,SUB_DEPARTMENT),budgetYear);
		this.numberOfPeriods = periodsDuration.length;
		this.periodsDuration = periodsDuration;
		setTotalPeriodDuration();
		initiateCollections();
		
		createBasicFixedColumns(department);
		createAditionalFixedColumns(department);
		createFactorAndCostsColumns(department);
	}
	
	public TrainingTable(int id, Classification classification, String budgetYear, SummaryColumn column, int colIndex){
		super(id, classification, budgetYear, column, colIndex);
		initiateCollections();
	}
	
	private void createBasicFixedColumns(String department){
		for(int i=0;i<WEEK_TITLES.length;i++){
			increaseNumberOfColumns();
			fixedColumns.put(this.getColIdInTable(),new QuantityColumn(WEEK_TITLES[i], new Classification(department, SUB_DEPARTMENT),true,this.getBudgetYear()));
		}
	}
	
	private void setTotalPeriodDuration(){
		totalPeriodDuration = 0;
		for(int i=0; i<periodsDuration.length;i++)
			totalPeriodDuration+= this.periodsDuration[i];
	}
	
	private void createAditionalFixedColumns(String department){
		for(int i=0;i<totalPeriodDuration-1;i++){
			int week = ((i%2==0?3:1)+i)%4;
			increaseNumberOfColumns();
			fixedColumns.put(this.getColIdInTable(),new QuantityColumn(WEEK_TITLES[week] + PREV_MONTH_WEEK_TITLE[i%4] 
					,new Classification(department, SUB_DEPARTMENT),false,this.getBudgetYear()));
		}
	}
	
	private int getWeeksBefore(int week){
		int weeksBefore = 0;
		for(int k=0;k<week;k++)
			weeksBefore += periodsDuration[k];
		return weeksBefore;
	}
	
	private double calculateFactor(int colNumber,int periodIndex, int weeksBefore ){
		if(colNumber<=NUMBER_OF_WEEKS)
			return Math.max(0, NUMBER_OF_WEEKS+1-(colNumber+weeksBefore))/this.periodsDuration[periodIndex];
		else
			return Math.max(Math.min(Math.min(periodsDuration[periodIndex], NUMBER_OF_WEEKS),
					periodsDuration[periodIndex]+NUMBER_OF_WEEKS-colNumber+weeksBefore),0)/this.periodsDuration[periodIndex];
	}
	
	private void initiateCollections(){
		this.fixedColumns = new HashMap<>();
		this.multColumns = new ArrayList<>();
		this.factorColumns = new ArrayList<>();
	}
	
	private void createFactorAndCostsColumns(String department) throws Exception{
		for(int i=0;i<numberOfPeriods;i++){
			HashMap<Integer, QuantityColumn> factors = new HashMap<>();
			HashMap<Integer, MultColumn> costs = new HashMap<>();
			int weeksBefore = getWeeksBefore(i);
			
			for (int j=1;j<=this.getColIdInTable();j++){
				double factor = calculateFactor(j,i,weeksBefore);	
				String colTitle = fixedColumns.get(j).getTitle();
				QuantityColumn qc = new QuantityColumn(colTitle + 
						FACTOR_TITLE, new Classification(department, SUB_DEPARTMENT), false,this.getBudgetYear());
				qc.setValues(factor);
				this.increaseNumberOfColumns();
				factors.put(this.getColIdInTable(), qc);
				MultColumn mc = new MultColumn(colTitle+COST_TITLE, new Classification(department, SUB_DEPARTMENT), ColumnType.costs, true,this.getBudgetYear());
				mc.addColumn(qc);
				mc.addColumn(this.fixedColumns.get(j));
				this.getSumColumn().addColumn(mc);
				this.increaseNumberOfColumns();
				costs.put(this.getColIdInTable(), mc);
			}
			factorColumns.add(factors);
			multColumns.add(costs);
		}	
	}
	
	public void addPeriodCostAssumption(Assumption assumption, int period) throws Exception{
		if (assumption==null)
			throw new Exception("Assumption can't be null");
		if(assumption.getType().getType()!=AssumptionType.Type.Costs)
			throw new Exception("Assumption type must be costs");
		if(period>numberOfPeriods||period<1)
			throw new Exception("Period must be a value between 1 to "+numberOfPeriods);
		
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
	
	public void insertNumberOfReqruits(int numOfReqruits,int month, int week) throws Exception{
		if (numOfReqruits<0)
			throw new Exception("Number of reqruits can't be negative");
		if(month>12 || month<1)
			throw new Exception("Month most be an integer between 1-12");
		if(week<1 || week>NUMBER_OF_WEEKS)
			throw new Exception("Week must be an integer between 1-4");
		
		fixedColumns.get(week).setValue(numOfReqruits, month);
		
		int startCol = NUMBER_OF_WEEKS*2 + 1 - week;
		int nextMonth = month+1;
		for(int i=startCol;i<=fixedColumns.size();i+=4){
			if(nextMonth<=12)
				fixedColumns.get(i).setValue(numOfReqruits,nextMonth);
			nextMonth++;
		}
	}
	
	public int getNumberOfPeriods() {
		return numberOfPeriods;
	}

	public void setNumberOfPeriods(int numberOfPeriods) {
		this.numberOfPeriods = numberOfPeriods;
	}

	public int getTotalPeriodDuration() {
		return totalPeriodDuration;
	}

	public int[] getPeriodsDuration() {
		return periodsDuration;
	}

	public void setPeriodsDuration(int[] periodsDuration) {
		this.periodsDuration = periodsDuration;
	}

	public HashMap<Integer, Column> getFixedColumns() {
		return fixedColumns;
	}

	public ArrayList<HashMap<Integer, QuantityColumn>> getFactorColumns() {
		return factorColumns;
	}

	public ArrayList<HashMap<Integer, MultColumn>> getMultColumns() {
		return multColumns;
	}

}
