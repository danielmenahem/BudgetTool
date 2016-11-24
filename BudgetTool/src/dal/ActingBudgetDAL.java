package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ActingBudgetDAL extends BudgetDAL {
	private static final String DROP_LINK_TABLE  = "Drop table link";
	private static final String SELECT_LINK_TABLE_ROWS = " SELECT * FROM LINK";
	private static final String COPY_STATEMENT_START = "CREATE LINKED TABLE LINK ('','jdbc:h2:";
	private static final String APP_PATH = System.getProperty("user.dir").replace("\\", "/");
	private static final String MAIN_DB_NAME = "/BudgetDB',";
	private static final String USER_NAME_AND_PASSWORD = "'Daniel_m','',";
	private static final String SELECT_STATEMENT_FOR_COPY = "'(SELECT * FROM ";
	private static final String COPY_STATEMENT_END = ")')";
	
	public ActingBudgetDAL(){
		super();
		dbURL = ACTING_DB_URL;
	}
	
	public void copySeasonBudget(String budgetYear) throws SQLException{
		
		try{
			createBudgetYear(budgetYear);
		}
		catch(Exception e){}
		
		Connection connection  = getConnection();
		copyDepartment(connection);
		copySubDepartment(connection);
		copyClassification(connection);
		
		//Copy table Item
		copyToLinkTable(budgetYear, "ITEM", "*", connection);
		copyFromLinkTable("ITEM", connection);
		dropLinkTable(connection);
		
		//Copy table Item_Values
		copyToLinkTable(budgetYear, "ITEM_VALUES INNER JOIN ITEM ON ITEM_VALUES.ITEM_ID  = ITEM.ID", 
				"ITEM_ID ,POS_INDEX ,VALUE ", connection);
		copyFromLinkTable("ITEM_VALUES", connection);
		dropLinkTable(connection);
		
		//Copy table Assumption
		copyToLinkTable(budgetYear, "ASSUMPTION INNER JOIN ITEM ON ASSUMPTION.ID = ITEM.ID", 
				"ASSUMPTION.ID, ASSUMPTION_TYPE", connection);
		copyFromLinkTable("ASSUMPTION", connection);
		dropLinkTable(connection);
		
		//Copy table Atom_Assumption
		copyToLinkTable(budgetYear, "ATOM_ASSUMPTION INNER JOIN ITEM ON ASSUMPTION_ID = ITEM.ID", 
				"ASSUMPTION_ID , IS_PERIODICAL ", connection);
		copyFromLinkTable("ATOM_ASSUMPTION", connection);
		dropLinkTable(connection); 
		
		//Copy table Calculated_Assumption
		copyToLinkTable(budgetYear, "CALCULATED_ASSUMPTION INNER JOIN ITEM ON ASSUMPTION_ID = ITEM.ID", 
				"ASSUMPTION_ID , \"Action\" , SPECIAL_ASSUMPTION_ID , SPECIAL_OP ", connection);
		copyFromLinkTable("CALCULATED_ASSUMPTION", connection);
		dropLinkTable(connection);
		
		//Copy table Column
		copyToLinkTable(budgetYear, "\"Column\" INNER JOIN ITEM ON \"Column\".ID = ITEM.ID", 
				"\"Column\".ID, COLUMN_TYPE , IS_VISIBLE", connection);
		copyFromLinkTable("\"Column\"", connection);
		dropLinkTable(connection);
		
		//Copy table Quantity_Column
		copyToLinkTable(budgetYear, "QUANTITY_COLUMN INNER JOIN ITEM ON QUANTITY_COLUMN.ID = ITEM.ID",
				"QUANTITY_COLUMN.ID", connection);
		copyFromLinkTable("QUANTITY_COLUMN", connection);
		dropLinkTable(connection);
		
		//Copy table CALCULATED_COLUMN
		copyToLinkTable(budgetYear, "CALCULATED_COLUMN INNER JOIN ITEM ON CALCULATED_COLUMN.ID = ITEM.ID", 
				"CALCULATED_COLUMN.ID", connection);
		copyFromLinkTable("CALCULATED_COLUMN", connection);
		dropLinkTable(connection);
		
		//Copy table Mult_Column
		copyToLinkTable(budgetYear, "MULT_COLUMN INNER JOIN ITEM ON MULT_COLUMN.ID = ITEM.ID", 
				"MULT_COLUMN.ID, ASSUMPTION_ID ", connection);
		copyFromLinkTable("MULT_COLUMN", connection);
		dropLinkTable(connection);
		
		//Copy table Sum_Column
		copyToLinkTable(budgetYear, "SUM_COLUMN INNER JOIN ITEM ON COLUMN_ID = ITEM.ID", 
				"COLUMN_ID", connection);
		copyFromLinkTable("SUM_COLUMN", connection);
		dropLinkTable(connection);
		
		//Copy table Regular_Table
		copyToLinkTable(budgetYear, "REGULAR_TABLE", "*", connection);
		copyFromLinkTable("REGULAR_TABLE", connection);
		dropLinkTable(connection);
		
		//Copy table Training_Table
		copyToLinkTable(budgetYear, "TRAINING_TABLE INNER JOIN REGULAR_TABLE ON TRAINING_TABLE.TABLE_ID = REGULAR_TABLE.TABLE_ID",
				"TRAINING_TABLE.TABLE_ID, NUM_OF_PERIODS", connection);
		copyFromLinkTable("TRAINING_TABLE", connection);
		dropLinkTable(connection);
		
		//Copy table Review_Table
		copyToLinkTable(budgetYear, "REVIEW_TABLE INNER JOIN REGULAR_TABLE ON REVIEW_TABLE.TABLE_ID = REGULAR_TABLE.TABLE_ID",
				"REVIEW_TABLE.TABLE_ID, TRAINING_TABLE_ID", connection);
		copyFromLinkTable("REVIEW_TABLE", connection);
		dropLinkTable(connection);

		//Copy table Assumption_To_Assumption
		copyToLinkTable(budgetYear, "ASSUMPTIONS_TO_ASSUMPTION INNER JOIN ITEM ON CALCULATED_ASSUMPTION_ID = ITEM.ID", 
				"CALCULATED_ASSUMPTION_ID , ASSUMPTION_ID", connection);
		copyFromLinkTable("ASSUMPTIONS_TO_ASSUMPTION", connection);
		dropLinkTable(connection);
		
		//Copy table ASSUMPTIONS_TO_CALC_COLUMN
		copyToLinkTable(budgetYear, "ASSUMPTIONS_TO_CALC_COLUMN INNER JOIN ITEM ON COLUMN_ID = ITEM.ID", 
				"COLUMN_ID, ASSUMPTION_ID", connection);
		copyFromLinkTable("ASSUMPTIONS_TO_CALC_COLUMN", connection);
		dropLinkTable(connection);
		
		//Copy table COLUMNS_TO_MULT_COLUMN
		copyToLinkTable(budgetYear, "COLUMNS_TO_MULT_COLUMN INNER JOIN ITEM ON MULT_COLUMN_ID = ITEM.ID", 
				"MULT_COLUMN_ID, INNER_COLUMN_ID", connection);
		copyFromLinkTable("COLUMNS_TO_MULT_COLUMN", connection);
		dropLinkTable(connection);
		
		//Copy table COLUMNS_TO_SUM_COLUMN
		copyToLinkTable(budgetYear, "COLUMNS_TO_SUM_COLUMN INNER JOIN ITEM ON SUM_COLUMN_ID = ITEM.ID", 
				"SUM_COLUMN_ID, INNER_COLUMN_ID", connection);
		copyFromLinkTable("COLUMNS_TO_SUM_COLUMN", connection);
		dropLinkTable(connection);
		
		//Copy table LISTENERS
		copyToLinkTable(budgetYear, "LISTENERS INNER JOIN ITEM ON ITEM_ID = ITEM.ID", 
				"ITEM_ID, LISTENER_ID", connection);
		copyFromLinkTable("LISTENERS", connection);
		dropLinkTable(connection);
		
		//Copy table COLUMNS_TO_TABLE
		copyToLinkTable(budgetYear, "COLUMNS_TO_TABLE INNER JOIN REGULAR_TABLE ON COLUMNS_TO_TABLE.TABLE_ID = REGULAR_TABLE.TABLE_ID", 
				"COLUMNS_TO_TABLE.TABLE_ID, COLUMN_ID, COLID_IN_TABLE", connection);
		copyFromLinkTable("COLUMNS_TO_TABLE", connection);
		dropLinkTable(connection);
		
		//Copy table FIXED_COLUMNS_TO_TABLE
		copyToLinkTable(budgetYear, "FIXED_COLUMNS_TO_TABLE INNER JOIN REGULAR_TABLE ON FIXED_COLUMNS_TO_TABLE.TABLE_ID = REGULAR_TABLE.TABLE_ID", 
				"FIXED_COLUMNS_TO_TABLE.TABLE_ID, COLUMN_ID, COL_ID_IN_TABLE", connection);
		copyFromLinkTable("FIXED_COLUMNS_TO_TABLE", connection);
		dropLinkTable(connection);
		
		//Copy table FACTOR_COLUMNS_TO_TABLE
		copyToLinkTable(budgetYear, "FACTOR_COLUMNS_TO_TABLE INNER JOIN REGULAR_TABLE ON FACTOR_COLUMNS_TO_TABLE.TABLE_ID = REGULAR_TABLE.TABLE_ID", 
				"FACTOR_COLUMNS_TO_TABLE.TABLE_ID, COLUMN_ID, COL_ID_IN_TABLE, PERIOD", connection);
		copyFromLinkTable("FACTOR_COLUMNS_TO_TABLE", connection);
		dropLinkTable(connection);
		
		//Copy table MULT_COLUMNS_TO_TABLE
		copyToLinkTable(budgetYear, "MULT_COLUMNS_TO_TABLE INNER JOIN REGULAR_TABLE ON MULT_COLUMNS_TO_TABLE.TABLE_ID = REGULAR_TABLE.TABLE_ID", 
				"MULT_COLUMNS_TO_TABLE.TABLE_ID, COLUMN_ID, COL_ID_IN_TABLE, PERIOD", connection);
		copyFromLinkTable("MULT_COLUMNS_TO_TABLE", connection);
		dropLinkTable(connection);
		
		//Copy table DURATION_TO_PERIOD
		copyToLinkTable(budgetYear, "DURATION_TO_PERIOD INNER JOIN REGULAR_TABLE ON DURATION_TO_PERIOD.TABLE_ID = REGULAR_TABLE.TABLE_ID", 
				"DURATION_TO_PERIOD.TABLE_ID, PERIOD_NUM, PERIOD_DURATION", connection);
		copyFromLinkTable("DURATION_TO_PERIOD", connection);
		dropLinkTable(connection);
		
		connection.close();
	}
	
	private void copyToLinkTable(String budgetYear, String tables, String columns, Connection connection) throws SQLException{
		String sql = COPY_STATEMENT_START + APP_PATH + MAIN_DB_NAME + USER_NAME_AND_PASSWORD
				+ SELECT_STATEMENT_FOR_COPY + tables + " WHERE B_YEAR = "+ budgetYear + COPY_STATEMENT_END;
		sql = sql.replace("*",columns);
		executeSQL(connection, sql);
	}
	
	private void copyFromLinkTable(String tableName, Connection connection) throws SQLException{
		String sql = "MERGE INTO "+ tableName + SELECT_LINK_TABLE_ROWS;
		executeSQL(connection, sql);
	}
	
	private void dropLinkTable(Connection connection) throws SQLException{
		executeSQL(connection, DROP_LINK_TABLE);
	}
	
	private void executeSQL(Connection connection, String sql) throws SQLException{
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.execute();
	}
	
	private void copyDepartment(Connection connection) throws SQLException{
		String sql = COPY_STATEMENT_START + APP_PATH + MAIN_DB_NAME + USER_NAME_AND_PASSWORD
				+ SELECT_STATEMENT_FOR_COPY + "DEPARTMENT" + COPY_STATEMENT_END;
		executeSQL(connection, sql);
		copyFromLinkTable("DEPARTMENT", connection);
		dropLinkTable(connection);
	}
	
	private void copySubDepartment(Connection connection) throws SQLException{
		String sql = COPY_STATEMENT_START + APP_PATH + MAIN_DB_NAME + USER_NAME_AND_PASSWORD
				+ SELECT_STATEMENT_FOR_COPY + "SUB_DEPARTMENT" + COPY_STATEMENT_END;
		executeSQL(connection, sql);
		copyFromLinkTable("SUB_DEPARTMENT", connection);
		dropLinkTable(connection);
	}
	
	private void copyClassification(Connection connection) throws SQLException{
		String sql = COPY_STATEMENT_START + APP_PATH + MAIN_DB_NAME + USER_NAME_AND_PASSWORD
				+ SELECT_STATEMENT_FOR_COPY + "CLASSIFICATION" + COPY_STATEMENT_END;
		executeSQL(connection, sql);
		copyFromLinkTable("CLASSIFICATION", connection);
		dropLinkTable(connection);
	}
	
	/*	public void test(){
	test1("16/17", "ASSUMPTION INNER JOIN ITEM ON ASSUMPTION.ID = ITEM.ID", 
			"ASSUMPTION.ID, ASSUMPTION_TYPE");
	}
	
	private void test1(String budgetYear, String tables, String columns){
		String sql = COPY_STATEMENT_START + APP_PATH + MAIN_DB_NAME + USER_NAME_AND_PASSWORD
				+ SELECT_STATEMENT_FOR_COPY + tables + " WHERE ITEM.B_YEAR = ?"  + COPY_STATEMENT_END;
		sql = sql.replace("*",columns);
		System.out.println(sql);
	}*/
	
	/*	public void printQuery(){
	System.out.println(COPY_STATEMENT_START + APP_PATH + MAIN_DB_NAME + USER_NAME_AND_PASSWORD
			+ SELECT_STATEMENT_FOR_COPY + "DEPARTMENT" + COPY_STATEMENT_END);
	}*/
}
