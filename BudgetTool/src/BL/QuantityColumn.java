package BL;

public class QuantityColumn extends Column {

	public QuantityColumn(String title, Classification classification, boolean isVisible,String budgetYear) {
		super(title, classification, ColumnType.quantity, isVisible, budgetYear);
	}
	
	public QuantityColumn(int id) {
		super(id);
	}
	
	@Override
	public void setColumnType(ColumnType columnType){
		
	}
	
}
