package ui.forms;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.TextField;
import java.util.regex.Pattern;

import javafx.scene.control.cell.*;
import javafx.beans.property.SimpleIntegerProperty;

import bl.Assumption;
import bl.AssumptionType;
import bl.AtomAssumption;
import bl.AssumptionType.Type;
import interfaces.AssumptionsMangerIF;
import javafx.scene.control.TableCell;
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
import javafx.application.Platform;
import javafx.beans.*;
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
	
	private AssumptionsMangerIF manager;
	private VBox paneMain = new VBox();
	private HBox paneFilters = new HBox(); 
	private GridPane paneNew = new GridPane();
	//private TableControl<bl.Assumption> table = new TableControl<>(bl.Assumption.class);
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

	public FormAssumption(AssumptionsMangerIF manager, boolean isPlanning){
		
		super(isPlanning);
		this.manager = manager;
		if(isPlanning)
			this.setStyle("-fx-background-color: yellow;");
		else
			this.setStyle("-fx-background-color: blue;");
		assumptions = getObsevableData();
		table = new TableView<>(assumptions);
		
		ObservableList<String> types = FXCollections.observableArrayList(AssumptionType.Type.Costs.toString(),
				AssumptionType.Type.Percentage.toString(), AssumptionType.Type.Quantity.toString());
		ObservableList<String> deps = FXCollections.observableArrayList(manager.getDepartments());
		ObservableList<String> subDeps = FXCollections.observableArrayList(manager.getSubDepartments());
		//TableColumn<Assumption, String> col15 = new TableColumn<>("Type");
		colDataType.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), types));
		colDataType.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Assumption,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Assumption, String> param) {
				return new  SimpleStringProperty(param.getValue().getType().getType().toString());
			}
		});
		colDataType.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Assumption,String>>() {
			@Override
			public void handle(CellEditEvent<Assumption, String> event) {
				if(event.getNewValue() == AssumptionType.Type.Costs.toString())
					(event.getTableView().getItems().get(event.getTablePosition().getRow())).setType(new AssumptionType(Type.Costs));
				else if(event.getNewValue() == AssumptionType.Type.Percentage.toString())
					(event.getTableView().getItems().get(event.getTablePosition().getRow())).setType(new AssumptionType(Type.Percentage));
				else if(event.getNewValue() == AssumptionType.Type.Quantity.toString())
					(event.getTableView().getItems().get(event.getTablePosition().getRow())).setType(new AssumptionType(Type.Quantity));
				manager.updateAssumptionInPlanning((event.getTableView().getItems().get(event.getTablePosition().getRow())));
			}
		});
		
		colDepartment.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), deps));
		colDepartment.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Assumption,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Assumption, String> param) {
				return new  SimpleStringProperty(param.getValue().getClassification().getDepartment());
			}
		});
		
		colDepartment.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Assumption,String>>() {
			@Override
			public void handle(CellEditEvent<Assumption, String> event) {
				(event.getTableView().getItems().get(event.getTablePosition().getRow()))
				.getClassification().setDepartment(event.getNewValue());
				(event.getTableView().getItems().get(event.getTablePosition().getRow())).setUpdated(false);
				manager.updateAssumptionInPlanning((event.getTableView().getItems().get(event.getTablePosition().getRow())));
			}
		});
		
		colSubDepartment.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), subDeps));
		colSubDepartment.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Assumption,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Assumption, String> param) {
				return new  SimpleStringProperty(param.getValue().getClassification().getSubDepartment());
			}
		});
		
		colSubDepartment.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Assumption,String>>() {
			@Override
			public void handle(CellEditEvent<Assumption, String> event) {
				(event.getTableView().getItems().get(event.getTablePosition().getRow()))
				.getClassification().setSubDepartment(event.getNewValue());
				(event.getTableView().getItems().get(event.getTablePosition().getRow())).setUpdated(false);
				manager.updateAssumptionInPlanning((event.getTableView().getItems().get(event.getTablePosition().getRow())));
			}
		});
		
		colID.setCellValueFactory(new PropertyValueFactory<Assumption, Integer>("id"));
		colTitle.setCellValueFactory(new PropertyValueFactory<Assumption, String>("title"));
		colTitle.setCellFactory(TextFieldTableCell.<Assumption>forTableColumn());
		colTitle.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Assumption,String>>() {
			@Override
			public void handle(CellEditEvent<Assumption, String> event) {
				(event.getTableView().getItems().get(event.getTablePosition().getRow())).setTitle(event.getNewValue());
				manager.updateAssumptionInPlanning((event.getTableView().getItems().get(event.getTablePosition().getRow())));
			}
		});
		
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
		
		table.getColumns().addAll(colID, colType,colDepartment, colSubDepartment, colTitle, colJul, colAug, colSep, 
				colOct, colNov, colDec, colJan, colFeb, colMar, colApr, colMay, colJun, colDataType);
		table.setEditable(true);
		//table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		setColsSizes();
		paneMain.getChildren().addAll(paneFilters, new ScrollPane(table), paneNew);
		this.getChildren().add(paneMain);
		
	}
	
	private void setColsSizes(){
		colID.setPrefWidth(45);
		colDataType.setPrefWidth(120);
		colDepartment.setPrefWidth(120);
		colSubDepartment.setPrefWidth(120);
		colType.setPrefWidth(120);
		colTitle.setPrefWidth(200);
		colJul.setPrefWidth(50);
		colAug.setPrefWidth(50);
		colSep.setPrefWidth(50);
		colOct.setPrefWidth(50);
		colNov.setPrefWidth(50);
		colDec.setPrefWidth(50);
		colJan.setPrefWidth(50);
		colFeb.setPrefWidth(50);
		colMar.setPrefWidth(50);
		colApr.setPrefWidth(50);
		colMay.setPrefWidth(50);
		colJun.setPrefWidth(50);
		for(TableColumn tc : table.getColumns()){
			tc.setStyle( "-fx-alignment: CENTER;");
		}
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
	
	public ArrayList<Assumption> getData(){
		ArrayList<Assumption> lst = new ArrayList<>();
		if(isPlanning())
			for(int id : manager.getPlanningAssumptions().keySet()){
				lst.add(manager.getPlanningAssumptions().get(id));
			}
		else
			for(int id : manager.getActualAssumptions().keySet()){
				lst.add(manager.getActualAssumptions().get(id));
			}
		return lst;
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



@Override
public void actionOnEvent(FormEvent<Assumption> e) {
	updateAssumption(e.getItem());
}
   
	
/*		table.setAgileEditing(true);
	NumberColumn<bl.Assumption,Integer> col1 = new NumberColumn<>("id", Integer.class,50);
	col1.setText("ID");
	col1.setEditable(false);
	TextColumn<bl.Assumption> col2 = new TextColumn<>("title",150);
	col2.setText("Title");
	col2.setAlignment(Pos.CENTER);
	NumberColumn<bl.Assumption,Double> col3 = new NumberColumn<>("Jul", Double.class,50);
	col3.setCellValueFactory(new MonthValues(1));
	col3.setRequired(false);
	col3.setPropertyName(null);
	col3.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Assumption,Double>>() {
		@Override
		public void handle(CellEditEvent<Assumption, Double> event) {
			(event.getTableView().getItems().get(event.getTablePosition().getRow())).setValue(event.getNewValue(), 1);
		}
	});
	NumberColumn<bl.Assumption,Double> col4 = new NumberColumn<>("Aug", Double.class,50);
	col4.setCellValueFactory(new MonthValues(2));
	NumberColumn<bl.Assumption,Double> col5 = new NumberColumn<>("Sep", Double.class,50);
	col5.setCellValueFactory(new MonthValues(3));
	NumberColumn<bl.Assumption,Double> col6 = new NumberColumn<>("Oct", Double.class,50);
	col6.setCellValueFactory(new MonthValues(4));
	NumberColumn<bl.Assumption,Double> col7 = new NumberColumn<>("Nov", Double.class,50);
	col7.setCellValueFactory(new MonthValues(5));
	NumberColumn<bl.Assumption,Double> col8 = new NumberColumn<>("Dec", Double.class,50);
	col8.setCellValueFactory(new MonthValues(6));
	NumberColumn<bl.Assumption,Double> col9 = new NumberColumn<>("Jan", Double.class,50);
	col9.setCellValueFactory(new MonthValues(7));
	NumberColumn<bl.Assumption,Double> col10 = new NumberColumn<>("Feb", Double.class,50);
	col10.setCellValueFactory(new MonthValues(8));
	NumberColumn<bl.Assumption,Double> col11 = new NumberColumn<>("Mar", Double.class,50);
	col11.setCellValueFactory(new MonthValues(9));
	NumberColumn<bl.Assumption,Double> col12 = new NumberColumn<>("Apr", Double.class,50);
	col12.setCellValueFactory(new MonthValues(10));
	NumberColumn<bl.Assumption,Double> col13 = new NumberColumn<>("May", Double.class,50);
	col13.setCellValueFactory(new MonthValues(11));
	NumberColumn<bl.Assumption,Double> col14 = new NumberColumn<>("Jun", Double.class,50);
	col14.setCellValueFactory(new MonthValues(12));
	CheckBoxColumn <Assumption> isClaculated = new CheckBoxColumn<>("Calculated");
	isClaculated.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Assumption,Boolean>, ObservableValue<Boolean>>() {
		
		@Override
		public ObservableValue<Boolean> call(CellDataFeatures<Assumption, Boolean> param) {
			boolean isCalc;
			if(param.getValue() instanceof AtomAssumption)
				isCalc = false;
			else
				isCalc = true;
			return new ReadOnlyObjectWrapper<>(isCalc);
		}
	});
	ComboBoxColumn<Assumption, String> type = new ComboBoxColumn<>("assumptionType");
	type.addItem("Atom", "Atom");
	type.addItem("Complex", "Complex");
	type.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Assumption,String>, ObservableValue<String>>() {
		@Override
		public ObservableValue<String> call(CellDataFeatures<Assumption, String> param) {
			if(param.getValue() instanceof AtomAssumption)
				return new ReadOnlyObjectWrapper<>("Atom");
			return new ReadOnlyObjectWrapper<>("Complex");		
		}
	});

	table.setController(new AssupmtionTableControler());
	
	table.getColumns().addAll(col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12, col13, col14);
	table.reloadFirstPage();
	table.setConfigurationID("FrmTstTextColumn");
	this.getChildren().add(table);*/
	
/*	class AssupmtionTableControler extends TableController<bl.Assumption>{
		
		@Override
		public TableData<bl.Assumption> loadData(int arg0, List<TableCriteria> arg1, List<String> arg2,
				List<SortType> arg3, int arg4) {
			ArrayList <bl.Assumption> lst = getData();
			return new TableData<>(lst, false, lst.size());
		}
		
		@Override
		public List<Assumption> update(List<Assumption> records){
			for(Assumption a : records){
				if(!a.isUpdated()){
					try{						
						updateAssumption(a);
					}
					catch(Exception e){}
				}
			}
			return getData();
		}
		
		@Override
		public List<Assumption> insert(List<Assumption> newRecords){
			for(Assumption a : newRecords){
				
			}
			return null;
		}
		
	}*/
	
}
