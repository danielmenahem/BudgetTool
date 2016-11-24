package bl;

public abstract class Column extends Item {
	
	public enum ColumnType{
		costs, quantity
	};
	
	private ColumnType columnType;
	private boolean isVisible;
	
	public Column(String title, Classification classification, ColumnType columnType, boolean isVisible, String budgetYear) {
		super(title,classification, budgetYear);
		this.columnType = columnType;
		this.setVisible(isVisible);
		this.setValues(0);
	}
	
	public Column(int id){
		super(id);
	}

	public ColumnType getColumnType() {
		return columnType;
	}

	public void setColumnType(ColumnType columnType){
		this.columnType = columnType;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	
}

