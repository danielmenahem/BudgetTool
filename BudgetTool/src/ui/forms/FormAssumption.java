package ui.forms;


import java.util.ArrayList;
import java.util.Comparator;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.*;

import bl.Assumption;
import bl.AssumptionType;
import bl.AtomAssumption;
import bl.AssumptionType.Type;
import interfaces.AssumptionsMangerIF;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ScrollPane;
import javafx.util.Callback;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.beans.property.*;
import javafx.util.converter.DefaultStringConverter;
import ui.interfaces.FormListener;
import ui.supports.DoubleEditingCell;
import ui.supports.FormEvent;



public class FormAssumption extends Form implements FormListener<Assumption>{
	private static final int VALUE_COLUMN_WIDTH  = 50;
	private static final int STRING_COLUMN_WIDTH  = 120;
	
	private AssumptionsMangerIF manager;
	private VBox paneMain = new VBox();
	private HBox paneFilters = new HBox(); 
	private GridPane paneNew = new GridPane();
	private TableView <Assumption> table;
	private TableColumn <Assumption, Integer> colID = new TableColumn<>("ID");
	private TableColumn <Assumption, String> colType = new TableColumn<>("Type");
	private TableColumn <Assumption, String> colDepartment = new TableColumn<>("Departments");
	private TableColumn <Assumption, String> colSubDepartment = new TableColumn<>("Sub Department");
	private TableColumn <Assumption, String> colTitle = new TableColumn<>("Title");
	private TableColumn <Assumption, Double> colJul = new TableColumn<>("Jul");
	private TableColumn <Assumption, Double> colAug = new TableColumn<>("Aug");
	private TableColumn <Assumption, Double> colSep = new TableColumn<>("Sep");
	private TableColumn <Assumption, Double> colOct = new TableColumn<>("Oct");
	private TableColumn <Assumption, Double> colNov = new TableColumn<>("Nov");
	private TableColumn <Assumption, Double> colDec = new TableColumn<>("Dec");
	private TableColumn <Assumption, Double> colJan = new TableColumn<>("Jan");
	private TableColumn <Assumption, Double> colFeb = new TableColumn<>("Feb");
	private TableColumn <Assumption, Double> colMar = new TableColumn<>("Mar");
	private TableColumn <Assumption, Double> colApr = new TableColumn<>("Apr");
	private TableColumn <Assumption, Double> colMay = new TableColumn<>("May");
	private TableColumn <Assumption, Double> colJun = new TableColumn<>("Jun");
	private TableColumn <Assumption, String> colDataType = new TableColumn<>("Data Type");
	private ObservableList<Assumption> assumptions;
	private ObservableList<String> types;
	private ObservableList<String> deps;
	private ObservableList<String> subDeps;
	

	public FormAssumption(AssumptionsMangerIF manager, boolean isPlanning){
		
		super(isPlanning);
		this.manager = manager;
		if(isPlanning)
			this.setStyle("-fx-background-color: yellow;");
		else
			this.setStyle("-fx-background-color: blue;");
		setObsLists();
		table = new TableView<>(assumptions);
		setColumnsCellsValueFactory();
		setColumnsCellsFactory();
		setColumnsActions();

		table.getColumns().addAll(colID, colType,colDepartment, colSubDepartment, colTitle, colJul, colAug, colSep, 
				colOct, colNov, colDec, colJan, colFeb, colMar, colApr, colMay, colJun, colDataType);
		table.setEditable(true);
		setColsSizesAndAlignment();
		paneMain.getChildren().addAll(paneFilters, new ScrollPane(table), paneNew);
		this.getChildren().add(paneMain);
	}
	
	private void setColumnsCellsFactory() {
		colDataType.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), types));
		colJul.setCellFactory(col -> new DoubleEditingCell<Assumption>(1,this));
		colAug.setCellFactory(col -> new DoubleEditingCell<Assumption>(2,this));
		colSep.setCellFactory(col -> new DoubleEditingCell<Assumption>(3,this));
		colOct.setCellFactory(col -> new DoubleEditingCell<Assumption>(4,this));
		colNov.setCellFactory(col -> new DoubleEditingCell<Assumption>(5,this));
		colDec.setCellFactory(col -> new DoubleEditingCell<Assumption>(6,this));
		colJan.setCellFactory(col -> new DoubleEditingCell<Assumption>(7,this));
		colFeb.setCellFactory(col -> new DoubleEditingCell<Assumption>(8,this));
		colMar.setCellFactory(col -> new DoubleEditingCell<Assumption>(9,this));
		colApr.setCellFactory(col -> new DoubleEditingCell<Assumption>(10,this));
		colMay.setCellFactory(col -> new DoubleEditingCell<Assumption>(11,this));
		colJun.setCellFactory(col -> new DoubleEditingCell<Assumption>(12,this));
		colTitle.setCellFactory(TextFieldTableCell.<Assumption>forTableColumn());
		colSubDepartment.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), subDeps));
		colDepartment.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), deps));
	}

	private void setColsSizesAndAlignment(){
		colID.setPrefWidth(45);
		colDataType.setPrefWidth(STRING_COLUMN_WIDTH);
		colDepartment.setPrefWidth(STRING_COLUMN_WIDTH);
		colSubDepartment.setPrefWidth(STRING_COLUMN_WIDTH);
		colType.setPrefWidth(STRING_COLUMN_WIDTH);
		colTitle.setPrefWidth(200);
		colJul.setPrefWidth(VALUE_COLUMN_WIDTH);
		colAug.setPrefWidth(VALUE_COLUMN_WIDTH);
		colSep.setPrefWidth(VALUE_COLUMN_WIDTH);
		colOct.setPrefWidth(VALUE_COLUMN_WIDTH);
		colNov.setPrefWidth(VALUE_COLUMN_WIDTH);
		colDec.setPrefWidth(VALUE_COLUMN_WIDTH);
		colJan.setPrefWidth(VALUE_COLUMN_WIDTH);
		colFeb.setPrefWidth(VALUE_COLUMN_WIDTH);
		colMar.setPrefWidth(VALUE_COLUMN_WIDTH);
		colApr.setPrefWidth(VALUE_COLUMN_WIDTH);
		colMay.setPrefWidth(VALUE_COLUMN_WIDTH);
		colJun.setPrefWidth(VALUE_COLUMN_WIDTH);
		for(TableColumn<?,?> tc : table.getColumns()){
			tc.setStyle( "-fx-alignment: CENTER;");
		}
	}
	
	private void setColumnsActions(){
		colDataType.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Assumption,String>>() {
			@Override
			public void handle(CellEditEvent<Assumption, String> event) {
				if(event.getNewValue() == AssumptionType.Type.Costs.toString())
					(event.getTableView().getItems().get(event.getTablePosition().getRow())).setType(new AssumptionType(Type.Costs));
				else if(event.getNewValue() == AssumptionType.Type.Percentage.toString())
					(event.getTableView().getItems().get(event.getTablePosition().getRow())).setType(new AssumptionType(Type.Percentage));
				else if(event.getNewValue() == AssumptionType.Type.Quantity.toString())
					(event.getTableView().getItems().get(event.getTablePosition().getRow())).setType(new AssumptionType(Type.Quantity));
				updateAssumption((event.getTableView().getItems().get(event.getTablePosition().getRow())));
			}
		});
		
		colDepartment.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Assumption,String>>() {
			@Override
			public void handle(CellEditEvent<Assumption, String> event) {
				(event.getTableView().getItems().get(event.getTablePosition().getRow()))
				.getClassification().setDepartment(event.getNewValue());
				(event.getTableView().getItems().get(event.getTablePosition().getRow())).setUpdated(false);
				updateAssumption((event.getTableView().getItems().get(event.getTablePosition().getRow())));
			}
		});
		
		colSubDepartment.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Assumption,String>>() {
			@Override
			public void handle(CellEditEvent<Assumption, String> event) {
				(event.getTableView().getItems().get(event.getTablePosition().getRow()))
				.getClassification().setSubDepartment(event.getNewValue());
				(event.getTableView().getItems().get(event.getTablePosition().getRow())).setUpdated(false);
				updateAssumption((event.getTableView().getItems().get(event.getTablePosition().getRow())));
			}
		});
		
		colTitle.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Assumption,String>>() {
			@Override
			public void handle(CellEditEvent<Assumption, String> event) {
				(event.getTableView().getItems().get(event.getTablePosition().getRow())).setTitle(event.getNewValue());
				updateAssumption((event.getTableView().getItems().get(event.getTablePosition().getRow())));
			}
		});
	}
	
	private void setColumnsCellsValueFactory(){
		colID.setCellValueFactory(new PropertyValueFactory<Assumption, Integer>("id"));
		colTitle.setCellValueFactory(new PropertyValueFactory<Assumption, String>("title"));
		colJul.setCellValueFactory(new MonthValues(1));
		colAug.setCellValueFactory(new MonthValues(2));
		colSep.setCellValueFactory(new MonthValues(3));
		colOct.setCellValueFactory(new MonthValues(4));
		colNov.setCellValueFactory(new MonthValues(5));
		colDec.setCellValueFactory(new MonthValues(6));
		colJan.setCellValueFactory(new MonthValues(7));
		colFeb.setCellValueFactory(new MonthValues(8));
		colMar.setCellValueFactory(new MonthValues(9));
		colApr.setCellValueFactory(new MonthValues(10));
		colMay.setCellValueFactory(new MonthValues(11));
		colJun.setCellValueFactory(new MonthValues(12));
		colType.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Assumption,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Assumption, String> param) {
				return param.getValue() instanceof AtomAssumption?
						new SimpleStringProperty("Atom"):new SimpleStringProperty("Complex");
			}
		});
		colDataType.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Assumption,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Assumption, String> param) {
				return new  SimpleStringProperty(param.getValue().getType().getType().toString());
			}
		});
		colDepartment.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Assumption,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Assumption, String> param) {
				return new  SimpleStringProperty(param.getValue().getClassification().getDepartment());
			}
		});
		colSubDepartment.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Assumption,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Assumption, String> param) {
				return new  SimpleStringProperty(param.getValue().getClassification().getSubDepartment());
			}
		});
	}
	
	private void setObsLists(){
		assumptions = getObsevableData();
		types = FXCollections.observableArrayList(AssumptionType.Type.Costs.toString(),
				AssumptionType.Type.Percentage.toString(), AssumptionType.Type.Quantity.toString());
		deps = FXCollections.observableArrayList(manager.getDepartments());
		subDeps = FXCollections.observableArrayList(manager.getSubDepartments());
	}
	
	private void updateAssumption(Assumption a) {
		if(isPlanning())
			manager.updateAssumptionInPlanning(a);
		else
			manager.updateAssumptionInActual(a);	
	}
	
	public ObservableList<Assumption> getObsevableData(){
		ArrayList<Assumption> lst = new ArrayList<>();
		if(isPlanning())
			for(int id : manager.getPlanningAssumptions().keySet()){
				lst.add(manager.getPlanningAssumptions().get(id));
			}
		else
			for(int id : manager.getActualAssumptions().keySet()){
				lst.add(manager.getActualAssumptions().get(id));
			}
		
		lst.sort(new Comparator<Assumption>() {
			@Override
			public int compare(Assumption a1, Assumption a2) {
				return a1.compareTo(a2);
			}
		});
		return FXCollections.observableArrayList(lst);
	}

	@Override
	public void actionOnEvent(FormEvent<Assumption> e) {
		updateAssumption(e.getItem());
	}
   	
	class MonthValues implements Callback<TableColumn.CellDataFeatures<Assumption,Double>, ObservableValue<Double>>{
		
		private int index;
		public MonthValues(int index) {
			this.index = index;
		}
		
		@Override
		public ObservableValue<Double> call(CellDataFeatures<Assumption, Double> param) {
			return new ReadOnlyObjectWrapper<>(param.getValue().getValue(index));
		}
	}
}
