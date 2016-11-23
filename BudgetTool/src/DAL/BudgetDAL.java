package DAL;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import BL.*;

public class BudgetDAL {
	
	public static final String DB_URL = "jdbc:h2:./BudgetDB";
	public static final String ACTING_DB_URL = "jdbc:h2:./ActingBudgetDB";
	protected static final String DB_USERNAME = "Daniel_m";
	protected static final String DB_PASSWORD = "";
	public static final String DRIVER_URL = "org.h2.Driver";
	protected String dbURL;
	
	private static HashMap<Class<?>, Integer> itemType = new HashMap<>();
	private static HashMap<Class<?>, Integer> tableType = new HashMap<>();

	public BudgetDAL(){
		initItemTypes();
		initTableTypes();
		this.dbURL = DB_URL;
	}
	
	private void initItemTypes(){
		itemType.put(AtomAssumption.class, 1);
		itemType.put(CalculatedAssumption.class, 2);
		itemType.put(QuantityColumn.class,3);
		itemType.put(CalculatedColumn.class,4);
		itemType.put(MultColumn.class, 5);
		itemType.put(SummaryColumn.class, 6);
	}
	
	private void initTableTypes(){
		tableType.put(Table.class,1);
		tableType.put(TrainingTable.class,2);
		tableType.put(ReviewTable.class,3);
	}
	
	protected Connection getConnection() throws RuntimeException {
		try {
			Class.forName(DRIVER_URL);
			return DriverManager.getConnection(dbURL, DB_USERNAME, DB_PASSWORD);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("Error while connecting to database", ex);
		} catch (SQLException ex) {
			throw new RuntimeException("Error while connecting to database", ex);
		}
	}
	
	public int create(Object obj) throws Exception {
		Connection connection = getConnection();
		int id = -1;
		try{
			id = create(obj, connection);
		}
		catch(Exception e){
			connection.close();
			throw e;
		}
		connection.close();
		return id;
	}
	
	private int create(Object obj, Connection connection) throws Exception {
		PreparedStatement statement = null;
		int id = 0;
		//save item		
		if(obj instanceof Item){
			Item item = (Item)obj;
			statement = itemStatement(connection, item);
			id = executeCreateStatment(connection, statement);
			item.setId(id);
			
			//save item values
			for(int i = 1; i<=item.NUMBER_OF_MONTHS;i++){
				statement = itemValuesStatement(connection, item, i);
				executeCreateStatment(connection, statement);
			}

			//save item listeners
			saveItemListeners(item, connection);
			
			//save assumptions
			if(item instanceof Assumption){
				Assumption assumption = (Assumption)item;
				statement = assumptionStatement(connection, assumption);
				executeCreateStatment(connection, statement);
				
				//save atom assumptions
				if(obj instanceof AtomAssumption){
					AtomAssumption aAssumption = (AtomAssumption)assumption;
					statement = atomAssumptionStatement(connection, aAssumption);
					executeCreateStatment(connection, statement);
				}
				//save calculated assumption
				else{//instance of calculated assumption
					CalculatedAssumption ca = (CalculatedAssumption)assumption;
					saveSpecialOperationAssumption(ca, connection);
		
					//save inner assumptions
					saveInnerAssumptions(ca, connection);
					statement = calculatedAssumptionStatement(connection,ca);
					executeCreateStatment(connection, statement);
					//save inner assumptions to assumption
					saveInnerAssumptionToAssumption(ca, connection);
				}
			}
			//save column
			else{//instance of column
				Column column = (Column)item;
				statement = columnStatement(connection,column);
				executeCreateStatment(connection, statement);
				
				//save quantity column
				if(obj instanceof QuantityColumn){
					QuantityColumn qc = (QuantityColumn)column;
					statement = quantityColumnStatement(connection,qc);
					executeCreateStatment(connection, statement);
				}
				//save mult column
				else if(obj instanceof MultColumn){
					MultColumn mc = (MultColumn)column;
					
					//save inner assumption
					saveInnerAssumptionInColumn(mc, connection);
					//save inner columns
					saveInnerColumnsInMultColumn(mc, connection);
					//save mult column
					statement = multColumnStatement(connection,mc);
					executeCreateStatment(connection, statement);
					//save inner columns to mult column
					saveInnerColumnsToMultColumn(mc, connection);
				}
				//save calculated column
				else if(obj instanceof CalculatedColumn){
					CalculatedColumn cc = (CalculatedColumn)column;
					
					//save inner assumptions
					saveInnerAssumptionsInCalcColumn(cc, connection);
					//save calculated columns
					statement = calculatedColumnStatement(connection,cc);
					executeCreateStatment(connection, statement);
					//save assumptions to calculated column
					saveInnerAssumptionsToCalcColumn(cc, connection);
				}
				
				//save summary column
				else{//instance of summary column
					SummaryColumn sc = (SummaryColumn)column;				
					//save inner columns
					saveInnerColumnsInSumColumn(sc, connection);
					//save summary column
					statement = summaryColumnStatement(connection,sc);
					executeCreateStatment(connection, statement);
					//save columns to summary columns
					saveInnerColumnsToSumColumn(sc, connection);
				}
			}
			item.setUpdated(true);
		}
		//save table
		else if(obj instanceof Table){
			int innerID = 0;
			Table table = (Table)obj;
			
			//save table sum column
			innerID = create(table.getSumColumn());
			table.getSumColumn().setId(innerID);
			//save table
			statement = tableStatement(connection,table);
			id = executeCreateStatment(connection, statement);
			table.setId(id);
			
			//save table columns
			saveTableColumns(table, connection);
			saveColumnsToTable(table, connection);
			
			//save training table
			if(obj instanceof TrainingTable){
				TrainingTable tTable = (TrainingTable)table;
				statement = trainingTableStatement(connection, tTable);
				executeCreateStatment(connection, statement);
				
				//save periods duration
				savePeriodsDuration(tTable, connection);

				//save fixed columns
				saveFixedColumnsInTrainingTable(tTable, connection);
				//save fixed columns to training table
				saveFixedColumnsToTrainingTable(tTable, connection);
				//save factor columns
				saveFactorColumnsInTrainingTable(tTable, connection);
				//save factor columns to training table
				saveFactorColumnsToTrainingTable(tTable, connection);
				//save mult columns
				saveMultColumnsInTrainingTable(tTable, connection);
				//save mult columns to training table
				saveMultColumnsToTrainingTable(tTable, connection);
			}
			
			//save ReviewTable
			else if(obj instanceof ReviewTable){
				ReviewTable rTable = (ReviewTable)table;
				if(rTable.getTrainingTable()==null){
					statement = reviewTableWithoutTrainingStatement(connection, rTable);
					executeCreateStatment(connection, statement);
				}
				else{
					if(rTable.getTrainingTable().getId()==0){
						innerID = create(rTable.getTrainingTable());
						rTable.getTrainingTable().setId(innerID);
					}
					statement = reviewTableWithTrainingStatement(connection, rTable);
					//save mult columns to review tables
					saveMultColumnsInReviewTable(rTable, connection);
					//save mult columns to training table
					saveMultColumnsToReviewTable(rTable, connection);
				}
			}
		}
		//create new classification
		if(obj instanceof Classification){
			Classification classification = (Classification)obj;
			if(getDepartmentID(classification.getDepartment(), connection)==0){
				createDepartment(classification.getDepartment(), connection);
			}
			if(getSubDepartmentID(classification.getSubDepartment(), connection)==0){
				createSubDepartment(classification.getSubDepartment(), connection);
			}
			id = getClassificationID(connection,classification);
		}
		return id;
	}

	public DataContainer readAll(DataContainer data) throws Exception{
		Connection connection = getConnection();
		try{			
			readItems(data, connection);
			readAssumptions(data, connection);
			readColumns(data, connection);
			readTables(data, connection);
			readTablesColumns(data,connection);
			connection.close();
		}
		catch(Exception e){
			connection.close();
			throw e;
		}
		return data;
	}
	
	public void update(Object obj) throws Exception{
		Connection connection = getConnection();
		try{			
			update(obj, connection);
		}
		catch(Exception e){
			connection.close();
			throw e;
		}
		connection.close();
	}
	
	private void update(Object obj, Connection connection) throws Exception{
		if(obj instanceof Item){
			Item item = (Item)obj;
			if(!item.isUpdated()){
				updateItem(item, connection);
				updateItemValues(item, connection);
				updateItemListeners(item,connection);
				
				if(obj instanceof Assumption){
					Assumption assumption = (Assumption)item;
					updateAssumption(assumption,connection);
					if(assumption instanceof AtomAssumption){
						AtomAssumption atomAssumption = (AtomAssumption)assumption;
						updateAtomAssumption(atomAssumption, connection);
					}
					else{
						CalculatedAssumption ca = (CalculatedAssumption)assumption;
						
						saveSpecialOperationAssumption(ca, connection);
						
						updateCalculatedAssumption(ca, connection);
						
						saveInnerAssumptions(ca, connection);
						deleteInnerAssumptionToAssumption(ca, connection);
						saveInnerAssumptionToAssumption(ca, connection);
					}
				}
				//instance of Column
				else{
					Column column = (Column)item;
					updateColumn(column, connection);
					if(column instanceof MultColumn){
						MultColumn mc = (MultColumn)column;
						saveInnerAssumptionInColumn(mc, connection);
						updateMultColumn(mc, connection);
						saveInnerColumnsInMultColumn(mc, connection);
						deleteInnerColumnToMultColumn(mc, connection);
						saveInnerColumnsInMultColumn(mc, connection);
					}
					else if(column instanceof CalculatedColumn){
						CalculatedColumn cc = (CalculatedColumn)column;
						saveInnerAssumptionsInCalcColumn(cc, connection);
						deleteInnerAssumptionsToCalcColumn(cc, connection);
						saveInnerAssumptionsToCalcColumn(cc, connection);
					}
					else if(column instanceof SummaryColumn){
						SummaryColumn sc = (SummaryColumn)column;
						saveInnerColumnsInSumColumn(sc, connection);
						deleteInnerColumnsToSumColumn(sc, connection);
						saveInnerColumnsToSumColumn(sc, connection);
					}
				}
				item.setUpdated(true);
			}
		}
		else if(obj instanceof Table){
			Table table = (Table)obj;
			update(table.getSumColumn(), connection);
			updateTable(table, connection);
			saveTableColumns(table, connection);
			deleteColumnsToTable(table, connection);
			saveColumnsToTable(table, connection);
			if(obj instanceof TrainingTable){
				TrainingTable tTable = (TrainingTable)table;
				updateTrainingTable(tTable, connection);
				
				deletePeriodDuaration(tTable, connection);
				savePeriodsDuration(tTable, connection);

				saveFixedColumnsInTrainingTable(tTable, connection);
				deleteFixedColumnsToTrainingTable(tTable, connection);
				saveFixedColumnsToTrainingTable(tTable, connection);
				
				saveFactorColumnsInTrainingTable(tTable, connection);
				deleteFactorColumnsToTrainingTable(tTable, connection);
				saveFactorColumnsToTrainingTable(tTable, connection);
				
				saveMultColumnsInTrainingTable(tTable, connection);
				deleteMultColumnsToTrainingTable(tTable, connection);
				saveMultColumnsToTrainingTable(tTable, connection);
			}
			else if(obj instanceof ReviewTable){
				ReviewTable rTable = (ReviewTable)table;
				if(rTable.getTrainingTable()!=null){
					if(rTable.getTrainingTable().getId() == 0){
						int innerId = create(rTable.getTrainingTable());
						rTable.getTrainingTable().setId(innerId);
						updateReviewTable(rTable, connection);
					}
				}
				saveMultColumnsInReviewTable(rTable, connection);
				deleteMultColumnsToReviewTable(rTable, connection);
				saveMultColumnsInReviewTable(rTable, connection);
			}
		}
	}
	
	public void delete(Object obj, boolean deleteInner) throws Exception{
		Connection connection = getConnection();
		try{			
			delete(obj, connection, deleteInner);
		}
		catch(Exception e){
			connection.close();
			throw e;
		}
		connection.close();
	}


	private void delete(Object obj, Connection connection, boolean deleteInner) throws Exception {
		if(obj instanceof Table){
			Table table = (Table)obj;
			HashMap<Integer, Column> colToDelete = new HashMap<>();
			if(obj instanceof TrainingTable){
				TrainingTable tTable = (TrainingTable)table;
				deleteFixedColumnsToTrainingTable(tTable, connection);
				deleteFactorColumnsToTrainingTable(tTable, connection);
				deleteMultColumnsToTrainingTable(tTable, connection);
				deleteTrainingTable(tTable, connection);
				prepareSpecialColsInTrainingTableForDelete(tTable, colToDelete);
			}
			
			if(obj instanceof ReviewTable){
				ReviewTable rTable = (ReviewTable)table;
				deleteMultColumnsToReviewTable(rTable, connection);
				deleteReviewTable(rTable, connection);
				prepareMultColsInReviewTableForDelete(rTable, colToDelete);
			}
			
			deleteColumnsToTable(table, connection);
			deleteInnerColumnsToSumColumn(table.getSumColumn(), connection);
			deleteItemListeners(table.getSumColumn(), connection);
			deleteItemInformers(table.getSumColumn(), connection);
			deleteRegularTable(table, connection);
			prepareColsInTableToDelete(table, colToDelete);
			colToDelete.put(table.getSumColumn().getId(), table.getSumColumn());
			for(Entry<Integer, Column> e : colToDelete.entrySet()){
				delete(e.getValue(), connection, false);
			}
		}
		else if(obj instanceof Column){
			Column column = (Column)obj;
			deleteItemListeners(column, connection);
			deleteItemInformers(column, connection);
			if(column instanceof QuantityColumn){
				deleteQuantityColumn((QuantityColumn)column, connection);
			}
			
			else if(column instanceof CalculatedColumn){
				deleteAssumptionsToCalculateColumn((CalculatedColumn)column, connection);
				deleteCalculatedColumn((CalculatedColumn)column, connection);
			}
			
			else if(column instanceof SummaryColumn){
				SummaryColumn sc = (SummaryColumn)column;
				deleteColumnsToSumColumn(sc, connection);
				if(deleteInner){
					for(Column c : sc.getColumns()){
						delete(c, connection, deleteInner);
					}
				}
				deleteSumColumn(sc, connection);
			}

			else if(column instanceof MultColumn){
				MultColumn mc = (MultColumn)column;
				deleteColumnsToMultColumn(mc, connection);
				if(deleteInner){
					for(Column c : mc.getColumns()){
						delete(c, connection, deleteInner);
					}
				}
				deleteMultColumn(mc, connection);
			}
			deleteColumn(column, connection);
			deleteItem(column, connection);
		}
	}


	private void prepareColsInTableToDelete(Table table, HashMap<Integer, Column> colToDelete) {
		for(Entry<Integer, Column> e : table.getColumns().entrySet()){
			colToDelete.put(e.getValue().getId(), e.getValue());
			prepareColumnsForDeletion(e.getValue(), colToDelete);
		}
	}

	private void prepareSpecialColsInTrainingTableForDelete(TrainingTable tTable, HashMap<Integer, Column> colToDelete) {
		for(HashMap<Integer, MultColumn> colSet : tTable.getMultColumns()){
			for(Entry<Integer, MultColumn> e : colSet.entrySet()){
				colToDelete.put(e.getValue().getId(), e.getValue());
				prepareColumnsForDeletion(e.getValue(), colToDelete);
			}
		}
	}
	
	private void prepareMultColsInReviewTableForDelete(ReviewTable rTable, HashMap<Integer, Column> colToDelete) {
		for(HashMap<Integer, MultColumn> colSet : rTable.getMultColumns()){
			for(Entry<Integer, MultColumn> e : colSet.entrySet()){
				colToDelete.put(e.getValue().getId(), e.getValue());
			}
		}
	}
	
	private void prepareColumnsForDeletion(Column column, HashMap <Integer, Column> colToDelete){
		if(column instanceof SummaryColumn){
			SummaryColumn sumCol = (SummaryColumn)column;
			for(Column c : sumCol.getColumns()){
				colToDelete.put(c.getId(), c);
				prepareColumnsForDeletion(c, colToDelete);
			}
		}
		
		else if(column instanceof MultColumn){
			MultColumn multCol = (MultColumn)column;
			for(Column c : multCol.getColumns()){
				colToDelete.put(c.getId(), c);
				prepareColumnsForDeletion(c, colToDelete);
			}
		}
	}
	
	private void deleteItem(Item item, Connection connection) throws SQLException {
		String query = "DELETE FROM Item "
				+ "WHERE ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, item.getId());
		statement.executeUpdate();	
	}

	private void deleteColumn(Column column, Connection connection) throws SQLException {
		String query = "DELETE FROM \"Column\" "
				+ "WHERE ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, column.getId());
		statement.executeUpdate();	
	}
	
	private void deleteMultColumn(MultColumn mc, Connection connection) throws SQLException {
		String query = "DELETE FROM MULT_COLUMN "
				+ "WHERE ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, mc.getId());
		statement.executeUpdate();	
		
	}

	private void deleteColumnsToMultColumn(MultColumn mc, Connection connection) throws SQLException {
		String query = "DELETE FROM COLUMNS_TO_MULT_COLUMN "
				+ "WHERE MULT_COLUMN_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, mc.getId());
		statement.executeUpdate();
	}
	

	private void deleteSumColumn(SummaryColumn sc, Connection connection) throws SQLException {
		String query = "DELETE FROM SUM_COLUMN "
				+ "WHERE COLUMN_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, sc.getId());
		statement.executeUpdate();	
	}
	
	private void deleteColumnsToSumColumn(SummaryColumn column, Connection connection) throws SQLException {
		String query = "DELETE FROM COLUMNS_TO_SUM_COLUMN "
				+ "WHERE SUM_COLUMN_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, column.getId());
		statement.executeUpdate();
	}

	private void deleteCalculatedColumn(CalculatedColumn column, Connection connection) throws SQLException {
		String query = "DELETE FROM CALCULATED_COLUMN "
				+ "WHERE ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, column.getId());
		statement.executeUpdate();	
	}
	
	private void deleteAssumptionsToCalculateColumn(CalculatedColumn column, Connection connection) throws SQLException {
		String query = "DELETE FROM ASSUMPTIONS_TO_CALC_COLUMN "
				+ "WHERE COLUMN_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, column.getId());
		statement.executeUpdate();
	}
	
	private void deleteQuantityColumn(QuantityColumn column, Connection connection) throws SQLException {
		String query = "DELETE FROM QUANTITY_COLUMN "
				+ "WHERE ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, column.getId());
		statement.executeUpdate();		
	}

	private void deleteItemInformers(Item item, Connection connection) throws SQLException {
		String query = "DELETE FROM LISTENERS WHERE LISTENER_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, item.getId());
		statement.executeUpdate();
	}

	private void deleteRegularTable(Table table, Connection connection) throws SQLException {
		deleteTable(table, "REGULAR_TABLE", connection);	
	}

	private void deleteReviewTable(ReviewTable rTable, Connection connection) throws SQLException {
		deleteTable(rTable, "REVIEW_TABLE", connection);
	}

	private void deleteTrainingTable(TrainingTable tTable, Connection connection) throws SQLException {
		deleteTable(tTable, "TRAINING_TABLE", connection);
	}
	
	private void deleteTable(Table table,String tableName, Connection connection) throws SQLException {
		String query = "DELETE FROM " + tableName
				+ " WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, table.getId());
		statement.executeUpdate();
	}
	
	private void deleteMultColumnsToReviewTable(ReviewTable rTable, Connection connection) throws SQLException {
		 deleteMultColumnsFromTable(rTable, connection);
	}

	private void deleteMultColumnsToTrainingTable(TrainingTable tTable, Connection connection) throws SQLException {
		 deleteMultColumnsFromTable(tTable, connection);
	}
	
	private void deleteMultColumnsFromTable(Table table, Connection connection) throws SQLException{
		String query = "DELETE FROM MULT_COLUMNS_TO_TABLE "
				+ "WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, table.getId());
		statement.executeUpdate();
	}

	private void deleteFactorColumnsToTrainingTable(TrainingTable tTable, Connection connection) throws SQLException {
		String query = "DELETE FROM FACTOR_COLUMNS_TO_TABLE "
				+ "WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, tTable.getId());
		statement.executeUpdate();
	}

	private void deleteFixedColumnsToTrainingTable(TrainingTable tTable, Connection connection) throws SQLException {
		String query = "DELETE FROM FIXED_COLUMNS_TO_TABLE "
				+ "WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, tTable.getId());
		statement.executeUpdate();
	}

	private void deletePeriodDuaration(TrainingTable tTable, Connection connection) throws SQLException {
		String query = "DELETE FROM DURATION_TO_PERIOD "
				+ "WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, tTable.getId());
		statement.executeUpdate();
	}

	private void deleteColumnsToTable(Table table, Connection connection) throws SQLException {
		String query = "DELETE FROM COLUMNS_TO_TABLE "
				+ "WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, table.getId());
		statement.executeUpdate();
		
	}

	private void deleteInnerColumnsToSumColumn(SummaryColumn sc, Connection connection) throws SQLException {
		String query = "DELETE FROM COLUMNS_TO_SUM_COLUMN "
				+ "WHERE SUM_COLUMN_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, sc.getId());
		statement.executeUpdate();
	}

	private void deleteInnerAssumptionsToCalcColumn(CalculatedColumn cc, Connection connection) throws SQLException {
		String query = "DELETE FROM ASSUMPTIONS_TO_CALC_COLUMN WHERE COLUMN_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, cc.getId());
		statement.executeUpdate();
	}

	private void deleteInnerColumnToMultColumn(MultColumn mc, Connection connection) throws SQLException {
		String query = "DELETE FROM COLUMNS_TO_MULT_COLUMN "
				+ "WHERE MULT_COLUMN_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, mc.getId());
		statement.executeUpdate();
	}

	private void deleteInnerAssumptionToAssumption(CalculatedAssumption ca, Connection connection) throws SQLException {
		String query = "DELETE FROM ASSUMPTIONS_TO_ASSUMPTION "
				+ "WHERE CALCULATED_ASSUMPTION_ID  = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, ca.getId());
		statement.executeUpdate();
	}
	
	private void deleteItemListeners(Item item, Connection connection) throws SQLException {
		String query = "DELETE FROM LISTENERS WHERE ITEM_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, item.getId());
		statement.executeUpdate();
	}
	
	private void updateReviewTable(ReviewTable rTable, Connection connection) throws SQLException {
		String query = "UPDATE REVIEW_TABLE SET TRAINING_TABLE_ID = ? "
				+ "WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, rTable.getTrainingTable().getId());
		statement.setInt(2, rTable.getId());
		statement.executeUpdate();
	}
	
	private void updateTrainingTable(TrainingTable tTable, Connection connection) throws SQLException {
		String query = "UPDATE TRAINING_TABLE SET NUM_OF_PERIODS = ? "
				+ "WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, tTable.getNumberOfPeriods());
		statement.setInt(2, tTable.getId());
		statement.executeUpdate();
	}
	
	private void updateTable(Table table, Connection connection) throws SQLException{
		String query = "UPDATE REGULAR_TABLE SET CLASSIFICATION_ID = ?, COL_INDEX = ? "
				+ "WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, getClassificationID(connection, table.getClassificaion()));
		statement.setInt(2, table.getColIdInTable());
		statement.setInt(3, table.getId());
		statement.executeUpdate();
	}
	

	private void updateMultColumn(MultColumn mc, Connection connection) throws SQLException {
		String query = "UPDATE MULT_COLUMN SET ASSUMPTION_ID = ? WHERE ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		if(mc.getAssumption() == null){
			statement.setNull(1, java.sql.Types.INTEGER);
		}
		else{
			statement.setInt(1, mc.getAssumption().getId());
		}
		statement.setInt(2, mc.getId());
		statement.executeUpdate();
	}
	
	private void updateColumn(Column column, Connection connection) throws SQLException {
		String query = "UPDATE \"Column\" SET COLUMN_TYPE = ?, IS_VISIBLE = ? WHERE ID =?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, column.getColumnType().ordinal());
		statement.setBoolean(2, column.isVisible());
		statement.setInt(3, column.getId());
		statement.executeUpdate();
	}
	
	private void updateCalculatedAssumption(CalculatedAssumption ca, Connection connection) throws SQLException {
		String query = "UPDATE CALCULATED_ASSUMPTION SET \"Action\" = ?, SPECIAL_ASSUMPTION_ID = ?,  SPECIAL_OP = ? "
				+ "WHERE ASSUMPTION_ID =?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, ca.getAction().ordinal());
		if(ca.getSpecialOperationAssumption() == null){
			statement.setNull(2, java.sql.Types.INTEGER);
		}
		else{
			statement.setInt(2, ca.getSpecialOperationAssumption().getId());
		}
		statement.setInt(3, ca.getSpecialOperationAction().ordinal());
		statement.setInt(4, ca.getId());
		statement.executeUpdate();
	}

	private void updateAtomAssumption(AtomAssumption atomAssumption, Connection connection) throws SQLException {
		String query = "UPDATE ATOM_ASSUMPTION SET IS_PERIODICAL = ? WHERE ASSUMPTION_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setBoolean(1, atomAssumption.isPerdiocal());
		statement.setInt(2, atomAssumption.getId());
		statement.executeUpdate();
	}

	private void updateItemListeners(Item item, Connection connection) throws Exception {
		deleteItemListeners(item, connection);
		saveItemListeners(item, connection);
	}

	private void updateItemValues(Item item, Connection connection) throws SQLException {
		for(int i=1; i<item.getValues().length;i++){
			updateItemValue(item, connection, i);
		}
	}

	private void updateItemValue(Item item, Connection connection, int valueIndex) throws SQLException {
		String query = "UPDATE ITEM_VALUES SET VALUE = ? WHERE ITEM_ID = ? AND POS_INDEX = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setDouble(1, item.getValue(valueIndex));
		statement.setInt(2, item.getId());
		statement.setInt(3, valueIndex);
		statement.executeUpdate();
	}

	private void updateAssumption(Assumption assumption, Connection connection) throws SQLException {
		String query = "UPDATE ASSUMPTION SET ASSUMPTION_TYPE = ? WHERE ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, assumption.getType().getType().ordinal());
		statement.setInt(2, assumption.getId());
		statement.executeUpdate();
	}

	private void updateItem(Item item, Connection connection) throws SQLException{
		String query = "UPDATE ITEM SET TITLE = ? ,CLASS_ID = ? ,B_YEAR = ? ,ITEM_TYPE = ? "
				+ "WHERE ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, item.getTitle());
		statement.setInt(2, getClassificationID(connection,item.getClassification()));
		statement.setString(3, item.getBudgetYear());
		statement.setInt(4, itemType.get(item.getClass()));
		statement.setInt(5, item.getId());
		statement.executeUpdate();
	}

	private void readTablesColumns(DataContainer data, Connection connection) throws SQLException {
		for(Entry<Integer, Table> e : data.getTables().entrySet()){
			if(e.getValue() instanceof TrainingTable){
				readTrainingTable((TrainingTable)e.getValue(),connection, data);
			}
			
			if(e.getValue() instanceof ReviewTable){
				readReviewTable((ReviewTable)e.getValue(), connection, data);
			}
			
			readTableRegularColumns(e.getValue(), connection, data);
		}
	}
	
	private void readReviewTable(ReviewTable table, Connection connection, DataContainer data) throws SQLException {
		String query = "SELECT TRAINING_TABLE_ID FROM REVIEW_TABLE WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, table.getId());
		ResultSet rs = statement.executeQuery();
		rs.next();
		int id = rs.getInt(1);
		if(id != 0){
			table.trainingTable((TrainingTable)data.getTables().get(id));
			readMultColumnsToReviewTable(table, connection, data);
		}
	}

	private void readMultColumnsToReviewTable(ReviewTable table, Connection connection, DataContainer data) throws SQLException {
		String query = "SELECT * FROM MULT_COLUMNS_TO_TABLE WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, table.getId());
		ResultSet rs = statement.executeQuery();
		while(rs.next()){
			table.getMultColumns().get(rs.getInt(4)).put(rs.getInt(3), 
					(MultColumn)data.getColumns().get(rs.getInt(2)));
		}
	}

	private void readTrainingTable(TrainingTable table, Connection connection, DataContainer data) throws SQLException {
		String query = "SELECT NUM_OF_PERIODS FROM TRAINING_TABLE WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, table.getId());
		ResultSet rs = statement.executeQuery();
		rs.next();
		table.setNumberOfPeriods(rs.getInt(1));
		readPeriodDuration(table, connection);
		readFixedColumnsToTable(table, connection, data);
		readFactorColumnsToTable(table, connection, data);
		readMultColumnsToTrainingTable(table, connection, data);
	}

	private void readMultColumnsToTrainingTable(TrainingTable table, Connection connection, DataContainer data) throws SQLException {
		for(int i=0; i<table.getNumberOfPeriods(); i++){
			table.getMultColumns().add(new HashMap<>());
		}
		
		String query = "SELECT * FROM MULT_COLUMNS_TO_TABLE WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, table.getId());
		ResultSet rs = statement.executeQuery();
		while(rs.next()){
			table.getMultColumns().get(rs.getInt(4)).put(rs.getInt(3), 
					(MultColumn)data.getColumns().get(rs.getInt(2)));
		}
	}

	private void readFactorColumnsToTable(TrainingTable table, Connection connection, DataContainer data) throws SQLException {
		for(int i=0; i<table.getNumberOfPeriods(); i++){
			table.getFactorColumns().add(new HashMap<>());
		}
		
		String query = "SELECT * FROM FACTOR_COLUMNS_TO_TABLE WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, table.getId());
		ResultSet rs = statement.executeQuery();
		while(rs.next()){
			table.getFactorColumns().get(rs.getInt(4)).put(rs.getInt(3), 
					(QuantityColumn)data.getColumns().get(rs.getInt(2)));
		}
	}

	private void readFixedColumnsToTable(TrainingTable table, Connection connection, DataContainer data) throws SQLException {
		String query = "SELECT * FROM FIXED_COLUMNS_TO_TABLE WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, table.getId());
		ResultSet rs = statement.executeQuery();
		while(rs.next()){
			table.getFixedColumns().put(rs.getInt(3), data.getColumns().get(rs.getInt(2)));
		}
	}

	private void readPeriodDuration(TrainingTable table, Connection connection) throws SQLException {
		int periodDuration [] = new int [table.getNumberOfPeriods()];
		String query = "SELECT * FROM DURATION_TO_PERIOD WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, table.getId());
		ResultSet rs = statement.executeQuery();
		while(rs.next()){
			periodDuration[rs.getInt(2)-1] = rs.getInt(3);
		}
		table.setPeriodsDuration(periodDuration);
	}

	private void readTables(DataContainer data, Connection connection) throws SQLException {
		HashMap<Integer, Table> tables = new HashMap<>();
		String query = "SELECT * FROM REGULAR_TABLE WHERE B_YEAR = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, data.getBudgetYear());
		ResultSet rs = statement.executeQuery();
		while(rs.next()){
			if(rs.getInt(5)==1){
				Table table = new Table(rs.getInt(1),getClassification(connection, rs.getInt(2)),
						rs.getString(3), (SummaryColumn)data.getColumns().get(rs.getInt(4)), rs.getInt(6));
				tables.put(table.getId(),table);
			}
			if(rs.getInt(5)==2){
				TrainingTable tTable = new TrainingTable(rs.getInt(1),getClassification(connection, rs.getInt(2)),
						rs.getString(3), (SummaryColumn)data.getColumns().get(rs.getInt(4)), rs.getInt(6));
				tables.put(tTable.getId(),tTable);
			}
			
			if(rs.getInt(5)==3){
				ReviewTable rTable = new ReviewTable(rs.getInt(1),getClassification(connection, rs.getInt(2)),
						rs.getString(3), (SummaryColumn)data.getColumns().get(rs.getInt(4)), rs.getInt(6));
				tables.put(rTable.getId(),rTable);
			}
		}
	}
	
	private void readTableRegularColumns(Table table, Connection connection, DataContainer data) throws SQLException {
		String query = "SELECT * FROM COLUMNS_TO_TABLE WHERE TABLE_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, table.getId());
		ResultSet rs =  statement.executeQuery();
		while(rs.next()){
			table.getColumns().put(rs.getInt(3), data.getColumns().get(rs.getInt(2)));
		}
	}

	private void readColumns(DataContainer data, Connection connection) throws Exception {
		for(Entry<Integer,Column> e : data.getColumns().entrySet()){
			readColumn(e.getValue(),connection);
			if(e.getValue() instanceof CalculatedColumn){
				readCalculatedColumn((CalculatedColumn)e.getValue(), connection, data);
			}
			
			if(e.getValue() instanceof MultColumn){
				readMultColumn((MultColumn)e.getValue(), connection, data);
			}
			
			if(e.getValue() instanceof SummaryColumn){
				readSummaryColumn((SummaryColumn)e.getValue(), connection, data);
			}
			e.getValue().setUpdated(true);
		}
	}

	private void readSummaryColumn(SummaryColumn column, Connection connection, DataContainer data) throws Exception {
		String query = "SELECT INNER_COLUMN_ID FROM COLUMNS_TO_SUM_COLUMN "
				+ "WHERE SUM_COLUMN_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, column.getId());
		ResultSet rs =  statement.executeQuery();
		while(rs.next()){
			column.addColumn(data.getColumns().get(rs.getInt(1)));
		}
	}

	private void readMultColumn(MultColumn column, Connection connection, DataContainer data) throws Exception {
		String query = "SELECT ASSUMPTION_ID FROM MULT_COLUMN WHERE ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, column.getId());
		ResultSet rs =  statement.executeQuery();
		rs.next();
		column.addAssumption(data.getAssumptions().get(rs.getInt(1)));
		readInnerColumns(column, connection, data);
	}

	private void readInnerColumns(MultColumn column, Connection connection, DataContainer data) throws Exception {
		String query = "SELECT INNER_COLUMN_ID FROM COLUMNS_TO_MULT_COLUMN "
				+ "WHERE MULT_COLUMN_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, column.getId());
		ResultSet rs =  statement.executeQuery();
		while(rs.next()){
			column.addColumn(data.getColumns().get(rs.getInt(1)));
		}
	}

	private void readCalculatedColumn(CalculatedColumn column, Connection connection, DataContainer data) throws Exception {
		String query =  "SELECT ASSUMPTION_ID FROM ASSUMPTIONS_TO_CALC_COLUMN "
				+ "WHERE COLUMN_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, column.getId());
		ResultSet rs =  statement.executeQuery();
		while(rs.next()){
			column.addAssumptionToColumn(data.getAssumptions().get(rs.getInt(1)));
		}
		
	}

	private void readColumn(Column column, Connection connection) throws SQLException {
		String query = "SELECT * FROM \"Column\" WHERE ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, column.getId());
		ResultSet rs =  statement.executeQuery();
		rs.next();
		column.setColumnType(Column.ColumnType.values()[rs.getInt(2)]);
		column.setVisible(rs.getBoolean(3));
	}

	private void readAssumptions(DataContainer data, Connection connection) throws Exception {
		for(Entry<Integer, Assumption> e : data.getAssumptions().entrySet()){
			readAssumption(e.getValue(), connection);
			if(e.getValue() instanceof AtomAssumption){
				readAtomAssumption((AtomAssumption)e.getValue(), connection);
			}
			if(e.getValue() instanceof CalculatedAssumption){
				readCalculatedAssumption((CalculatedAssumption)e.getValue(), connection, data);
			}
			e.getValue().setUpdated(true);
		}
	}
	
	private void readCalculatedAssumption(CalculatedAssumption assumption, Connection connection, DataContainer data) throws Exception  {
		String query = "SELECT * FROM ATOM_ASSUMPTION "
				+ "WHERE ASSUMPTION_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, assumption.getId());
		ResultSet rs =  statement.executeQuery();
		rs.next();
		assumption.setAction(CalculatedAssumption.Action.values()[rs.getInt(2)]);
		int specialOp = rs.getInt(3);
		if(specialOp!=0)
			assumption.setSpecialOperationAssumption(data.getAssumptions().get(rs.getInt(4)), 
					CalculatedAssumption.SpecialOperation.values()[specialOp]);
		readInnerAssumptions(assumption, connection, data);
	}

	private void readInnerAssumptions(CalculatedAssumption assumption, Connection connection, DataContainer data) throws Exception{
		String query =  "SELECT ASSUMPTION_ID FROM ASSUMPTIONS_TO_ASSUMPTION "
				+ "WHERE CALCULATED_ASSUMPTION_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, assumption.getId());
		ResultSet rs =  statement.executeQuery();
		while(rs.next()){
			assumption.addAssumption(data.getAssumptions().get(rs.getInt(1)));
		}
	}

	private void readAtomAssumption(AtomAssumption assumption, Connection connection) throws SQLException {
		String query = "Select IS_PERIODICAL FROM ATOM_ASSUMPTION "
				+ "WHERE ASSUMPTION_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, assumption.getId());
		ResultSet rs =  statement.executeQuery();
		rs.next();
		assumption.setPerdiocal(rs.getBoolean(1));
	}

	private void readAssumption(Assumption assumption, Connection connection) throws SQLException{
		String query = "SELECT ASSUMPTION_TYPE FROM ASSUMPTION "
				+ "WHERE ID = ? ";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, assumption.getId());
		ResultSet rs =  statement.executeQuery();
		rs.next();
		assumption.setType(new AssumptionType(AssumptionType.Type.values()[rs.getInt(1)]));
	}

	private void readItems(DataContainer data, Connection connection) throws SQLException{
		HashMap<Integer, Assumption> assumptions = new HashMap<>();
		HashMap<Integer, Column> columns = new HashMap<>();
		String query = "SELECT * FROM ITEM WHERE ITEM.B_YEAR = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, data.getBudgetYear());
		ResultSet rs =  statement.executeQuery();
		
		while(rs.next()){
			int type = rs.getInt("ITEM_TYPE");
			int id = rs.getInt("ID");
			String title = rs.getString("TITLE");
			Classification classification = getClassification(connection, rs.getInt("CLASS_ID"));
			String bYear = rs.getString("B_YEAR");
			int values [] = getItemValues(connection,id);
			
			if(type == 1){
				AtomAssumption assumption = new AtomAssumption(id);
				initItem(assumption,title,classification,values,bYear);
				assumptions.put(assumption.getId(), assumption);
			}
			
			else if(type == 2){
				CalculatedAssumption assumption = new CalculatedAssumption(id);
				initItem(assumption, title, classification, values, bYear);
				assumptions.put(assumption.getId(), assumption);
			}
			
			else if(type == 3){
				QuantityColumn column = new QuantityColumn(id);
				initItem(column, title, classification, values, bYear);
				columns.put(column.getId(), column);
			}
			
			else if(type == 4){
				CalculatedColumn column = new CalculatedColumn(id);
				initItem(column, title, classification, values, bYear);
				columns.put(column.getId(), column);
			}
			
			else if(type == 5){
				MultColumn column = new MultColumn(id);
				initItem(column, title, classification, values, bYear);
				columns.put(column.getId(), column);
			}
			
			else if(type == 6){
				SummaryColumn column = new SummaryColumn(id);
				initItem(column, title, classification, values, bYear);
				columns.put(column.getId(), column);
			}
		}
		
		data.setAssumptions(assumptions);
		data.setColumns(columns);
	}
	
	
	private int executeCreateStatment(Connection connection,PreparedStatement statement) throws SQLException{
		int affectedRows = 0;
		try {
			affectedRows = statement.executeUpdate();
		} 
		catch (Exception e) {}
		if (affectedRows == 0) {
			throw new SQLException("Writing object failed, no rows affected.");
		}

		try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				return id;
			} else {
				throw new SQLException("Writing object failed, no ID obtained.");
			}
		}
	}
	
	private void initItem(Item item, String title, Classification classification, int values [], String bYear){
		item.setTitle(title);
		item.setBudgetYear(bYear);
		item.setClassification(classification);
		for(int i = 0; i<values.length; i++){
			item.setValue(i+1, values[i]);
		}
	}
	
	private Classification getClassification(Connection connection ,int id) throws SQLException{
		String query = ("Select DEPARTMENT.NAME, SUB_DEPARTMENT.NAME FROM CLASSIFICATION "
				+ "INNER JOIN DEPARTMENT ON DEPARTMENT.DEP_ID = CLASSIFICATION.DEP_ID "
				+ "INNER JOIN SUB_DEPARTMENT ON SUB_DEPARTMENT.SUB_DEP_ID = CLASSIFICATION.SUB_DEP_ID "
				+ "WHERE CLASSIFICATION.ID = ?");
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, id);
		ResultSet rs =  statement.executeQuery();
		rs.next();
		return new Classification(rs.getString(1), rs.getString(2));
	}
	
	private int[] getItemValues(Connection connection, int id) throws SQLException{
		String query = "SELECT POS_INDEX, VALUE FORM ITEM_VALUES "
				+ "WHERE ITEM_ID = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, id);
		ResultSet rs =  statement.executeQuery();
		int values [] = new int [12];
		while(rs.next()){
			values[rs.getInt("POS_INDEX")-1] = rs.getInt("VALUE");
		}
		return values;
	}
	
	
	private int getDepartmentID(String department, Connection connection) throws SQLException{
		return getDepartmentsID(department, "DEPARTMENT", connection);
	}
	
	private int getSubDepartmentID(String SubDepartment, Connection connection) throws SQLException{
		return getDepartmentsID(SubDepartment, "SUB_DEPARTMENT", connection);
	}
	
	private int getDepartmentsID(String name, String table, Connection connection) throws SQLException{
		int id = 0;
		String query = "SELECT * FROM " + table +" WHERE " + table + ".NAME = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, name);
		ResultSet rs =  statement.executeQuery();
		if(rs.next())
			id = rs.getInt(1);
		return id;
	}
	
	public int createDepartment(String departmentName) throws SQLException{
		Connection connection = getConnection();
		int id = 0;
		try{
			id = createDepartment(departmentName, connection);
		}
		catch(SQLException e){
			connection.close();
			throw new SQLException(e.getMessage());
		}
		connection.close();
		return id;
	}
	
	private int createDepartment(String departmentName, Connection connection) throws SQLException{
		PreparedStatement statement = connection.prepareStatement("INSERT INTO DEPARTMENT (NAME) VALUES(?)",Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, departmentName);
		return executeCreateStatment(connection, statement);
	}
	
	public int createSubDepartment(String subDepartmentName) throws SQLException{
		Connection connection = getConnection();
		int id = 0;
		try{
			id = createSubDepartment(subDepartmentName, connection);
		}
		catch(SQLException e){
			connection.close();
			throw new SQLException(e.getMessage());
		}
		connection.close();
		return id;
	}
	
	private int createSubDepartment(String subDepartmentName, Connection connection) throws SQLException{
		PreparedStatement statement = connection.prepareStatement("INSERT INTO SUB_DEPARTMENT (NAME) VALUES(?)",Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, subDepartmentName);
		return executeCreateStatment(connection, statement);
	}
	
	public void createBudgetYear(String budgetYear) throws SQLException{
		Connection connection = getConnection();
		try{
			createBudgetYear(budgetYear, connection);
		}
		catch(SQLException e){
			connection.close();
			throw new SQLException(e.getMessage());
		}
		connection.close();
	}
	
	private void createBudgetYear(String budgetYear, Connection connection) throws SQLException{
		PreparedStatement statement = connection.prepareStatement("MERGE INTO BUDGET_YEAR (B_YEAR) VALUES(?)");
		statement.setString(1, budgetYear);
		statement.execute();
	}
	
	private void saveMultColumnsInReviewTable(ReviewTable rTable, Connection connection) throws Exception {
		for(int i = 0; i<rTable.getMultColumns().size(); i++){
			for(Entry<Integer,MultColumn> e : rTable.getMultColumns().get(i).entrySet()){
				int id = 0;
				if(e.getValue().getId()==0){
					id = create(e.getValue());
					e.getValue().setId(id);
				}
				else{
					update(e.getValue());
				}
			}
		}
	}

	private void saveMultColumnsToReviewTable(ReviewTable rTable, Connection connection) {
		for(int i = 0; i<rTable.getMultColumns().size(); i++){
			for(Entry<Integer,MultColumn> e : rTable.getMultColumns().get(i).entrySet()){
				try{
					PreparedStatement statement = multColToTableStatement(connection,rTable,i,e.getKey());
					executeCreateStatment(connection, statement);
				}
				catch(Exception ex){
				}
			}
		}
	}

	private void saveMultColumnsToTrainingTable(TrainingTable tTable, Connection connection) {
		for(int i = 0; i<tTable.getMultColumns().size(); i++){
			for(Entry<Integer,MultColumn> e : tTable.getMultColumns().get(i).entrySet()){
				try{
					PreparedStatement statement = multColToTableStatement(connection,tTable,i,e.getKey());
					executeCreateStatment(connection, statement);
				}
				catch(Exception ex){
				}
			}
		}
	}

	private void saveMultColumnsInTrainingTable(TrainingTable tTable, Connection connection) throws Exception {
		for(int i = 0; i<tTable.getMultColumns().size(); i++){
			for(Entry<Integer,MultColumn> e : tTable.getMultColumns().get(i).entrySet()){
				int id = 0;
				if(e.getValue().getId()==0){
					id = create(e.getValue());
					e.getValue().setId(id);
				}
				else{
					update(e.getValue());
				}
			}
		}
	}
	
	private void saveFactorColumnsInTrainingTable(TrainingTable tTable, Connection connection) throws Exception {
		for(int i = 0; i<tTable.getFactorColumns().size(); i++){
			for(Entry<Integer,QuantityColumn> e : tTable.getFactorColumns().get(i).entrySet()){
				int id = 0;
				if(e.getValue().getId()==0){
					id = create(e.getValue());
					e.getValue().setId(id);
				}
				else{
					update(e.getValue());
				}
			}
		}
	}

	private void saveFactorColumnsToTrainingTable(TrainingTable tTable, Connection connection) {
		for(int i = 0; i<tTable.getFactorColumns().size(); i++){
			for(Entry<Integer,QuantityColumn> e : tTable.getFactorColumns().get(i).entrySet()){
				try{
					PreparedStatement statement = factorColToTrainingTableStatement(connection,tTable,i,e.getKey());
					executeCreateStatment(connection, statement);
				}
				catch(Exception ex){
				}
			}
		}	
	}
	
	private void saveFixedColumnsInTrainingTable(TrainingTable tTable, Connection connection) throws Exception {
		for(Entry<Integer,Column> e: tTable.getFixedColumns().entrySet()){
			int id = 0;
			if(e.getValue().getId()==0){
				id = create(e.getValue());
				e.getValue().setId(id);
			}
			else{
				update(e.getValue());
			}
		}
	}

	private void saveFixedColumnsToTrainingTable(TrainingTable tTable, Connection connection) {
		for(Entry<Integer,Column> e: tTable.getFixedColumns().entrySet()){
			try{
				PreparedStatement statement = fixedColToTrainingTableStatement(connection, tTable, e.getKey());
				executeCreateStatment(connection, statement);
			}
			catch(Exception ex){
			}
		}
	}

	
	private void saveTableColumns(Table table, Connection connection) throws Exception {
		for(Entry<Integer,Column> e: table.getColumns().entrySet()){
			int id = 0;
			if(e.getValue().getId()==0){
				id = create(e.getValue());
				e.getValue().setId(id);
			}
			else{
				update(e.getValue());
			}
		}
	}
	
	private void savePeriodsDuration(TrainingTable tTable, Connection connection) throws SQLException {
		for(int i=0;i<tTable.getNumberOfPeriods();i++){
			PreparedStatement statement = durationToPeriodStatement(connection, tTable, i);
			executeCreateStatment(connection, statement);
		}
	}

	private void saveColumnsToTable(Table table, Connection connection){
		for(Entry<Integer,Column> e: table.getColumns().entrySet()){
			try{
				PreparedStatement statement = colToTableSatement(connection, table, e.getKey());
				executeCreateStatment(connection, statement);
			}
			catch(Exception ex){
			}
		}
	}
	
	private void saveInnerColumnsToSumColumn(SummaryColumn sc, Connection connection) {
		for(int i=0;i<sc.getColumns().size();i++){						
			try{
				PreparedStatement statement = columnsToSumColumnStatement(connection, sc, i);
				executeCreateStatment(connection, statement);							
			}
			catch(Exception e){
			}
		}
	}
	
	private void saveInnerColumnsInSumColumn(SummaryColumn sc, Connection connection) throws Exception {
		for(int i=0;i<sc.getColumns().size();i++){
			int id = 0;
			if(sc.getColumns().get(i).getId()==0){
				id = create(sc.getColumns().get(i));
				sc.getColumns().get(i).setId(id);
			}
			else{
				update(sc.getColumns().get(i));
			}
		}
	}
	
	private void saveInnerAssumptionsToCalcColumn(CalculatedColumn cc, Connection connection) {
		for(int i=0;i<cc.getAssumptions().size();i++){
			try{
				PreparedStatement statement = assumptionsToCalcColumnStatement(connection, cc, i);
				executeCreateStatment(connection, statement);							
			}
			catch(Exception e){
			}
		}
	}
	
	private void saveInnerAssumptionsInCalcColumn(CalculatedColumn cc, Connection connection) throws Exception {
		for(int i=0;i<cc.getAssumptions().size();i++){
			int id = 0;
			if(cc.getAssumptions().get(i).getId()==0){
				id = create(cc.getAssumptions().get(i));
				cc.getAssumptions().get(i).setId(id);
			}
			else{
				update(cc.getAssumptions().get(i));
			}
		}
	}
	
	private void saveInnerColumnsToMultColumn(MultColumn mc, Connection connection) {
		for(int i=0;i<mc.getColumns().size();i++){
			try{
				PreparedStatement statement = columnsToColumnStatement(connection, mc, i);
				executeCreateStatment(connection, statement);							
			}
			catch(Exception e){
			}
		}
	}
	
	private void saveInnerColumnsInMultColumn(MultColumn mc, Connection connection) throws Exception {
		for(int i=0;i<mc.getColumns().size();i++){
			int id = 0;
			if(mc.getColumns().get(i).getId()==0){
				id = create(mc.getColumns().get(i));
				mc.getColumns().get(i).setId(id);
			}
			else{
				update(mc.getColumns().get(i));
			}
		}
	}
	
	private void saveInnerAssumptionInColumn(MultColumn mc, Connection connection) throws Exception {
		if(mc.getAssumption() != null){
			int id = 0;
			if(mc.getAssumption().getId()==0){
				id = create(mc.getAssumption());
				mc.getAssumption().setId(id);
			}
			else{
				update(mc.getAssumption());
			}
		}
	}
	
	private void saveInnerAssumptionToAssumption(CalculatedAssumption ca, Connection connection) throws Exception{
		for(int j=0;j<ca.getAssumptions().size();j++){
			try{							
				PreparedStatement statement = assumptionsToAssumptionStatement(connection, ca, j);
				executeCreateStatment(connection, statement);
			}
			catch(Exception e){
			}
		}
	}
	
	private void saveInnerAssumptions(CalculatedAssumption ca, Connection connection) throws Exception{
		for(int j=0;j<ca.getAssumptions().size();j++){
			int id = 0;
			if(ca.getAssumptions().get(j).getId()==0){
				id = create(ca.getAssumptions().get(j));
				ca.getAssumptions().get(j).setId(id);
			}
			else{
				update(ca.getAssumptions().get(j), connection);
			}
		}
	}
	
	private void saveItemListeners(Item item, Connection connection) throws Exception{
		int id = -1;
		for(int i = 0;i<item.getListeners().size();i++){
			if(((Item)item.getListeners().get(i)).getId()==0){
				id = create(item.getListeners().get(i));
				((Item)item.getListeners().get(i)).setId(id);
				
				//save listeners to item
				try{
					PreparedStatement statement = listenersToItemStatement(connection, item, i);
					executeCreateStatment(connection, statement);
				}
				catch(Exception e){
				}
			}
			else{
				update((Item)item.getListeners().get(i));
			}
		}
	}
	
	private void saveSpecialOperationAssumption(CalculatedAssumption ca, Connection connection) throws Exception{
		int id = 0;
		if(ca.getSpecialOperationAssumption()!=null){
			//save special operation assumption
			if(ca.getSpecialOperationAssumption().getId()==0){
				id = create(ca.getSpecialOperationAssumption());
				ca.getSpecialOperationAssumption().setId(id);
			}
			else{
				update(ca.getSpecialOperationAssumption());
			}
		}
	}
	
	private PreparedStatement reviewTableWithTrainingStatement(Connection connection, ReviewTable rTable) throws Exception{
		String s = "INSERT INTO REVIEW_TABLE (TABLE_ID,TRAINING_TABLE_ID ) VALUES(?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, rTable.getId());
		ps.setInt(2, rTable.getTrainingTable().getId());
		return ps;
	}
	
	private PreparedStatement reviewTableWithoutTrainingStatement(Connection connection, ReviewTable rTable) throws Exception{
		String s = "INSERT INTO REVIEW_TABLE (TABLE_ID) VALUES(?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, rTable.getId());
		return ps;
	}
	
	private PreparedStatement multColToTableStatement(Connection connection, Table table, int periodIndex,
			Integer key) throws SQLException {
		String s = "INSERT INTO MULT_COLUMNS_TO_TABLE (TABLE_ID, COLUMN_ID, COL_ID_IN_TABLE, PERIOD) VALUES(?,?,?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, table.getId());
		if(table instanceof TrainingTable)
			ps.setInt(2, ((TrainingTable)table).getMultColumns().get(periodIndex).get(key).getId());
		else if(table instanceof ReviewTable)
			ps.setInt(2, ((ReviewTable)table).getMultColumns().get(periodIndex).get(key).getId());
		ps.setInt(3, key);
		ps.setInt(4, periodIndex);
		return ps;
	}
	
	private PreparedStatement factorColToTrainingTableStatement(Connection connection,TrainingTable tTable,int periodIndex, int key) throws SQLException{
		String s = "INSERT INTO FACTOR_COLUMNS_TO_TABLE (TABLE_ID, COLUMN_ID, COL_ID_IN_TABLE, PERIOD) VALUES(?,?,?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, tTable.getId());
		ps.setInt(2, tTable.getFactorColumns().get(periodIndex).get(key).getId());
		ps.setInt(3, key);
		ps.setInt(4, periodIndex);
		return ps;
	}
	
	private PreparedStatement fixedColToTrainingTableStatement(Connection connection, TrainingTable tTable,
			Integer key) throws Exception {
		String s = "INSERT INTO FIXED_COLUMNS_TO_TABLE (TABLE_ID ,COLUMN_ID ,COL_ID_IN_TABLE) VALUES(?,?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, tTable.getId());
		ps.setInt(2, tTable.getFixedColumns().get(key).getId());
		ps.setInt(3, key);
		return ps;
	}
	
	private PreparedStatement durationToPeriodStatement(Connection connection, TrainingTable tTable, int periodIndex) throws SQLException{
		String s = "INSERT INTO DURATION_TO_PERIOD (TABLE_ID , PERIOD_NUM , PERIOD_DURATION ) VALUES(?,?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, tTable.getId());
		ps.setInt(2,periodIndex+1);
		ps.setInt(3, tTable.getPeriodsDuration()[periodIndex]);
		return ps;
	}
	
	private PreparedStatement trainingTableStatement(Connection connection, TrainingTable tTable) throws Exception{
		String s = "INSERT INTO TRAINING_TABLE (TABLE_ID , NUM_OF_PERIODS ) VALUES(?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, tTable.getId());
		ps.setInt(2, tTable.getNumberOfPeriods());
		return ps;
	}
	
	private PreparedStatement colToTableSatement (Connection connection, Table table, int colId) throws Exception{
		String s = "INSERT INTO COLUMNS_TO_TABLE (TABLE_ID ,COLUMN_ID ,COLID_IN_TABLE) VALUES(?,?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, table.getId());
		ps.setInt(2,table.getColumns().get(colId).getId());
		ps.setInt(3, colId);
		
		return ps;
	}
	
	private PreparedStatement tableStatement (Connection connection, Table table) throws SQLException{
		String s = "INSERT INTO REGULAR_TABLE (CLASSIFICATION_ID ,B_YEAR ,SUM_COLUMN_ID ,TABLE_TYPE, COL_INDEX) VALUES (?,?,?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, getClassificationID(connection, table.getClassificaion()));
		ps.setString(2, table.getBudgetYear());
		ps.setInt(3, table.getSumColumn().getId());
		ps.setInt(4, tableType.get(table.getClass()));
		ps.setInt(5, table.getColIdInTable());
		return ps;
	}
	
	private PreparedStatement columnsToSumColumnStatement(Connection connection, SummaryColumn sc, int colIndex) throws SQLException {
		String s = "INSERT INTO COLUMNS_TO_SUM_COLUMN (SUM_COLUMN_ID ,INNER_COLUMN_ID) VALUES(?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, sc.getId());
		ps.setInt(2, sc.getColumns().get(colIndex).getId());
		return ps;
	}
	
	private PreparedStatement summaryColumnStatement(Connection connection, SummaryColumn sc) throws SQLException {
		String s = "INSERT INTO SUM_COLUMN (COLUMN_ID) VALUES (?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, sc.getId());
		return ps;
	}
	
	private PreparedStatement assumptionsToCalcColumnStatement(Connection connection, CalculatedColumn cc, int assummptionIndex) throws SQLException{
		String s = "INSERT INTO ASSUMPTIONS_TO_CALC_COLUMN (COLUMN_ID ,ASSUMPTION_ID) VALUES(?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, cc.getId());
		ps.setInt(2,cc.getAssumptions().get(assummptionIndex).getId());
		return ps;
	}
	
	private PreparedStatement calculatedColumnStatement(Connection connection, CalculatedColumn cc) throws SQLException{
		String s = "INSERT INTO CALCULATED_COLUMN (ID) VALUES(?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, cc.getId());
		return ps;
	}
	
	private PreparedStatement columnsToColumnStatement(Connection connection, MultColumn mc, int columnIndex) throws SQLException {
		String s = "INSERT INTO COLUMNS_TO_MULT_COLUMN (MULT_COLUMN_ID ,INNER_COLUMN_ID) VALUES(?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, mc.getId());
		ps.setInt(2,mc.getColumns().get(columnIndex).getId());
		
		return ps;
	}
	
	private PreparedStatement assumptionsToAssumptionStatement(Connection connection, CalculatedAssumption ca, int assumptiomIndex) throws SQLException {
		String s = "INSERT INTO ASSUMPTIONS_TO_ASSUMPTION (CALCULATED_ASSUMPTION_ID ,ASSUMPTION_ID ) VALUES(?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, ca.getId());
		ps.setInt(2, ca.getAssumptions().get(assumptiomIndex).getId());
		return ps;
	}
	
	private PreparedStatement listenersToItemStatement(Connection connection, Item item, int listenerIndex) throws Exception{
		String s = "INSERT INTO LISTENERS (ITEM_ID ,LISTENER_ID) VALUES(?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, item.getId());
		ps.setInt(2, ((Item)item.getListeners().get(listenerIndex)).getId());
		return ps;
	}
	
	private PreparedStatement itemValuesStatement(Connection connection, Item item, int valueIndex) throws SQLException{
		String s = "INSERT INTO ITEM_VALUES (ITEM_ID ,POS_INDEX ,VALUE ) VALUES(?,?,?)";
		PreparedStatement ps = connection.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, item.getId());
		ps.setInt(2, valueIndex);
		ps.setDouble(3, item.getValue(valueIndex));
		return ps;
	}
	
	private PreparedStatement quantityColumnStatement(Connection connection, QuantityColumn qc) throws SQLException{
		String statement = "INSERT INTO QUANTITY_COLUMN (ID) VALUES(?)";
		PreparedStatement ps = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, qc.getId());
		return ps;
	}
	
	private PreparedStatement columnStatement(Connection connection, Column column) throws SQLException {
		String statement = "INSERT INTO \"Column\" (ID ,COLUMN_TYPE ,IS_VISIBLE) VALUES(?,?,?)";
		PreparedStatement ps = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, column.getId());
		ps.setInt(2, column.getColumnType().ordinal());
		ps.setBoolean(3, column.isVisible());
		return ps;
	}
	
	private PreparedStatement multColumnStatement(Connection connection, MultColumn mc) throws SQLException{
		String statement = "INSERT INTO MULT_COLUMN (ID ,ASSUMPTION_ID) VALUES(?,?)";
		PreparedStatement ps = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, mc.getId());
		if(mc.getAssumption()!=null)
			ps.setInt(2, mc.getAssumption().getId());
		else
			ps.setNull(2, java.sql.Types.INTEGER);
		return ps;
	}

	
	private PreparedStatement calculatedAssumptionStatement(Connection connection, CalculatedAssumption ca) throws Exception{
		String statement = "INSERT INTO CALCULATED_ASSUMPTION (ASSUMPTION_ID ,\"Action\" ,SPECIAL_OP";
		if(ca.getSpecialOperationAssumption()==null){
			statement+= ",SPECIAL_ASSUMPTION_ID) values(?,?,?,?)";
		}
		else{
			statement+= ") values(?,?,?)";
		}
		PreparedStatement ps = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, ca.getId());
		ps.setInt(2, ca.getAction().ordinal());
		ps.setInt(3, ca.getSpecialOperationAction().ordinal());
		if(ca.getSpecialOperationAssumption()==null)
			ps.setInt(4, ca.getSpecialOperationAssumption().getId());
		return ps;
	}
	
	private PreparedStatement itemStatement (Connection connection, Item item) throws SQLException{
		String statement = "INSERT INTO ITEM (Title, Class_ID, B_Year, Item_Type) VALUES(?,?,?,?)";
		PreparedStatement ps = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
		
		ps.setString(1, item.getTitle());
		ps.setInt(2, getClassificationID(connection, item.getClassification()));
		ps.setString(3, item.getBudgetYear());
		ps.setInt(4,itemType.get(item.getClass()));
		
		return ps;
	}
		
	private PreparedStatement assumptionStatement (Connection connection, Assumption assumption)throws SQLException{
		String statement = "INSERT INTO ASSUMPTION (ID,ASSUMPTION_TYPE) Values (?,?)";
		PreparedStatement ps = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, assumption.getId());
		ps.setInt(2, assumption.getType().getType().ordinal());
		return ps;
	}
	
	private PreparedStatement atomAssumptionStatement (Connection connection, AtomAssumption assumption)throws SQLException{
		String statement = "INSERT INTO ATOM_ASSUMPTION (ID,IS_PERIODICAL) Values (?,?)";
		PreparedStatement ps = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, assumption.getId());
		ps.setBoolean(2, assumption.isPerdiocal());
		return ps;
	}
	
	private int getClassificationID(Connection connection, Classification classification) throws SQLException{
		int id = 0;
		String query = "SELECT * FROM CLASSIFICATION "
				+ "INNER JOIN DEPARTMENT ON CLASSIFICATION.DEP_ID = DEPARTMENT.DEP_ID "
				+ "INNER JOIN SUB_DEPARTMENT ON  CLASSIFICATION.SUB_DEP_ID  = SUB_DEPARTMENT.SUB_DEP_ID "
				+ "WHERE DEPARTMENT .NAME  = ? AND SUB_DEPARTMENT.NAME  = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, classification.getDepartment());
		statement.setString(2, classification.getSubDepartment());
		ResultSet rs =  statement.executeQuery();
		if(rs.next()){
			id = rs.getInt(1);
		}
		else{
			id = createClassification(connection, classification);
		}
		return id;
	}

	private int createClassification(Connection connection, Classification classification) throws SQLException{
		
		String statement = "INSERT INTO CLASSIFICATION (DEP_ID ,SUB_DEP_ID ) Values (?,?)";
		PreparedStatement ps = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, getAndCreateDepartmentID(connection, classification.getDepartment()));
		ps.setInt(2, getAndCreateSubDepartmentID(connection,classification.getSubDepartment()));
		return executeCreateStatment(connection, ps);
	}
	
	private int getAndCreateDepartmentID(Connection connection, String department) throws SQLException{
		int id = getDepartmentID(department, connection);
		if(id==0){
			id = createDepartment(department, connection);
		}
		return id;
	}
	
	private int getAndCreateSubDepartmentID(Connection connection, String SubDepartment) throws SQLException{
		int id = getSubDepartmentID(SubDepartment, connection);
		if(id==0){
			id = createSubDepartment(SubDepartment, connection);
		}
		return id;
	}
	
	public ArrayList<String> getAllBudgetYears() throws SQLException{
		ArrayList<String> budgetYears = new ArrayList<>();
		Connection connection = getConnection();
		try{
			String query = "SELECT B_YEAR FROM BUDGET_YEAR ";
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet rs =  statement.executeQuery();
			while(rs.next()){
				budgetYears.add(rs.getString(1));
			}
		}
		catch(SQLException e){
			connection.close();
			throw e;
		}
		connection.close();
		return budgetYears;
	}
	
	public ArrayList<String> getAllDepartments() throws SQLException{
		ArrayList<String> departments = new ArrayList<>();
		Connection connection = getConnection();
		try{
			String query = "SELECT NAME FROM DEPARTMENT ";
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet rs =  statement.executeQuery();
			while(rs.next()){
				departments.add(rs.getString(1));
			}
		}
		catch(SQLException e){
			connection.close();
			throw e;
		}
		connection.close();
		return departments;
	}
	
	public ArrayList<String> getAllSubDepartments() throws SQLException{
		ArrayList<String> subDepartments = new ArrayList<>();
		Connection connection = getConnection();
		try{
			String query = "SELECT NAME FROM SUB_DEPARTMENT ";
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet rs =  statement.executeQuery();
			while(rs.next()){
				subDepartments.add(rs.getString(1));
			}
		}
		
		catch(SQLException e){
			connection.close();
			throw e;
		}
		connection.close();
		return subDepartments;
	}
}