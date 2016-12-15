package ui.forms;


import java.util.ArrayList;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.scene.control.cell.*;

import bl.Assumption;
import bl.AssumptionComperator;
import bl.AssumptionType;
import bl.AtomAssumption;
import bl.CalculatedAssumption;
import bl.CalculatedAssumption.Action;
import bl.AssumptionType.Type;
import interfaces.AssumptionsManagerIF;
import ui.BuildComplexAssumption;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.property.*;
import javafx.util.converter.DefaultStringConverter;
import javafx.application.Platform;
import ui.interfaces.FormListener;
import ui.supports.DoubleEditingCell;
import ui.supports.FormEvent;
import ui.supports.StylePatterns;



public class FormAssumption extends Form implements FormListener<Assumption>{
	
	private static final int VALUE_COLUMN_WIDTH  = 50;
	private static final int STRING_COLUMN_WIDTH  = 100;
	private AssumptionsManagerIF manager;
	private VBox paneMain = new VBox();
	private HBox paneFilters = new HBox();
	private HBox paneFilterByType = new HBox();
	private HBox paneFilterByDepartment = new HBox();
	private HBox paneFilterBySubDepartment = new HBox();
	private HBox paneFilterByDataType = new HBox();
	private VBox paneNew = new VBox();
	private GridPane paneNewDefenitions = new GridPane();
	private HBox paneNewValues = new HBox();
	private GridPane paneNewValuesLeft = new GridPane();
	private GridPane paneNewValuesRight = new GridPane();
	private StackPane paneNewAssumptionHeader = new StackPane();
	private StackPane paneNewAssumptionAddButton = new StackPane();
	private StackPane paneNewAssumptionMessage = new StackPane();
	private Text lblNewAssumptionHeader = new Text("Create New Assumption");
	private Label lblNewAssumptionType = new Label("Type");
	private Label lblNewAssumptionTitle = new Label("Title");
	private Label lblNewAssumptionValue = new Label("Value");
	private Label lblNewAssumptionDepartment = new Label("Department");
	private Label lblNewAssumptionSubDep = new Label("Sub Department");
	private Label lblNewAssumptionDataType = new Label("Data Type");
	private Label lblNewAssumptionAction = new Label("Action");
	private Label lblMsg = new Label("");
	private Label lblFilterByType = new Label ("Type");
	private Label lblFilterByDepartment = new Label("Department");
	private Label lblFilterBySubDepartment = new Label("Sub Department");
	private Label lblFilterByDataType = new Label("Data Type");
	private ComboBox<String> cmbNewAssumptionType;
	private ComboBox<String> cmbNewAssumptionDepartment;
	private ComboBox<String> cmbNewAssumptionSubDepartment;
	private ComboBox<String> cmbNewAssumptionDataType;
	private ComboBox<String> cmbNewAssumptionAction;
	private ComboBox<String> cmbFilterByType;
	private ComboBox<String> cmbFilterByDepartment;
	private ComboBox<String> cmbFilterBySubDepartment;
	private ComboBox<String> cmbFilterByDataType;
	private TextField tfNewAssumptionTitle = new TextField();
	private TextField tfNewAssumptionValue = new TextField();
	private Button btnNewAssumptionAdd = new Button("Add Assumption");
	private Button btnSearch = new Button("Search");
	private Button btnClearFilters = new Button("Clear Filters");

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
    private TableColumn<Assumption, String> colAction = new TableColumn<>( "Complex Action" );
	private ObservableList<Assumption> assumptions;
	private ObservableList<String> types;
	private ObservableList<String> dataTypes;
	private ObservableList<String> deps;
	private ObservableList<String> subDeps;
	private ObservableList<String> actions;
	
	private boolean hasFilterChange = false;
	
	public FormAssumption(AssumptionsManagerIF manager, boolean isPlanning, double formWidth){
		super(isPlanning, formWidth);
		this.manager = manager;
		if(isPlanning)
			this.setStyle(StylePatterns.PLANNING_BACKGROUND_CSS);
		else
			this.setStyle(StylePatterns.ACTUAL_BACKGROUND_CSS);
		setObsLists();
		buildFiltersGUI();
		table = new TableView<>(assumptions);
		setColumnsCellsValueFactory();
		setColumnsCellsFactory();
		setColumnsActions();
		setTableColumns();
		table.setEditable(true);
		setColsSizesAndAlignment();
		buildNewAssumptionGUI();
		table.setMaxHeight(360);
		table.setMaxWidth(this.getFormWidth()/1.02);
		table.setMinWidth(this.getFormWidth()/1.02);
		paneMain.getChildren().addAll(paneFilters, table, paneNew);
		this.getChildren().add(paneMain);
	}

	@SuppressWarnings("unchecked")
	private boolean setTableColumns() {
		return table.getColumns().addAll(colID, colAction, colType,colDepartment, colSubDepartment, colTitle, colJul, colAug, colSep, 
				colOct, colNov, colDec, colJan, colFeb, colMar, colApr, colMay, colJun, colDataType);
	}
	
	private void buildFiltersGUI(){
		cmbFilterByType = new ComboBox<>(types);
		cmbFilterByDataType = new ComboBox<>(dataTypes);
		cmbFilterByDepartment = new ComboBox<>(deps);
		cmbFilterBySubDepartment = new ComboBox<>(subDeps);
		cmbFilterByType.setOnAction(e -> filterChange());
		cmbFilterByDataType.setOnAction(e -> filterChange());
		cmbFilterByDepartment.setOnAction(e -> filterChange());
		cmbFilterBySubDepartment.setOnAction(e -> filterChange());
		paneFilterByDepartment.getChildren().addAll(lblFilterByDepartment, cmbFilterByDepartment);
		paneFilterBySubDepartment.getChildren().addAll(lblFilterBySubDepartment, cmbFilterBySubDepartment);
		paneFilterByType.getChildren().addAll(lblFilterByType, cmbFilterByType);
		paneFilterByDataType.getChildren().addAll(lblFilterByDataType, cmbFilterByDataType);
		
		paneFilterByDepartment.setSpacing(5);
		paneFilterBySubDepartment.setSpacing(5);
		paneFilterByType.setSpacing(5);
		paneFilterByDataType.setSpacing(5);
		cmbFilterByType.setStyle(StylePatterns.COMBO_BOX_CSS);
		cmbFilterByDataType.setStyle(StylePatterns.COMBO_BOX_CSS);
		cmbFilterByDepartment.setStyle(StylePatterns.COMBO_BOX_CSS);
		cmbFilterBySubDepartment.setStyle(StylePatterns.COMBO_BOX_CSS);
		lblFilterByDataType.setStyle(StylePatterns.LABEL_CSS);
		lblFilterByDepartment.setStyle(StylePatterns.LABEL_CSS);
		lblFilterBySubDepartment.setStyle(StylePatterns.LABEL_CSS);
		lblFilterByType.setStyle(StylePatterns.LABEL_CSS);
		btnSearch.setStyle(StylePatterns.BUTTON_CSS);
		btnSearch.setOnAction(e -> search());
		btnClearFilters.setStyle(StylePatterns.BUTTON_CSS);
		btnClearFilters.setOnAction(e -> clearFilters());
		btnClearFilters.setOnMousePressed(e -> {
			btnClearFilters.setStyle(StylePatterns.BUTTON_HOVERD_CSS);
		});
		btnClearFilters.setOnMouseReleased(e -> {
			btnClearFilters.setStyle(StylePatterns.BUTTON_CSS);
		});
		btnSearch.setOnMousePressed(e -> {
			btnSearch.setStyle(StylePatterns.BUTTON_HOVERD_CSS);
		});
		btnSearch.setOnMouseReleased(e -> {
			btnSearch.setStyle(StylePatterns.BUTTON_CSS);
		});
		paneFilters.getChildren().addAll(paneFilterByDepartment, paneFilterBySubDepartment, paneFilterByType, paneFilterByDataType, btnSearch, btnClearFilters);
		paneFilters.setPadding(new Insets(5));
		paneFilters.setSpacing(25);
		paneFilters.setStyle("-fx-background-color: #EDF1FF; "
			+"-fx-border-width: 1;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-color:#aaaab2;");
	}
	
	private void filterChange(){
		this.hasFilterChange = true;
	}
	
	private void clearFilters(){
		cmbFilterByDataType.getSelectionModel().select(-1);
		cmbFilterByDepartment.getSelectionModel().select(-1);
		cmbFilterBySubDepartment.getSelectionModel().select(-1);
		cmbFilterByType.getSelectionModel().select(-1);
	}
	
	private void search(){
		if(this.hasFilterChange){
			if(cmbFilterByDataType.getSelectionModel().getSelectedIndex() == -1 && 
					cmbFilterByDepartment.getSelectionModel().getSelectedIndex() == -1 &&
					cmbFilterBySubDepartment.getSelectionModel().getSelectedIndex() == -1 &&
					cmbFilterByType.getSelectionModel().getSelectedIndex() == -1){
				assumptions = FXCollections.observableArrayList(getObsevableData());
			}
			else{
				assumptions = getFilterdData();
			}
			table.setItems(assumptions);
			this.hasFilterChange = false;
		}
	}
	
	private ObservableList<Assumption> getFilterdData(){
		ArrayList<Assumption> lst = new ArrayList<>();
		if(isPlanning())
			for(int id : manager.getPlanningAssumptions().keySet()){
				checkAndAddIfqualify(manager.getPlanningAssumptions().get(id), lst);
			}
		else
			for(int id : manager.getActualAssumptions().keySet()){
				checkAndAddIfqualify(manager.getActualAssumptions().get(id), lst);
			}
		
		lst.sort(new AssumptionComperator());
		return FXCollections.observableArrayList(lst);
	}
	
	private void checkAndAddIfqualify(Assumption assumption, ArrayList<Assumption> lst) {
		if((cmbFilterByType.getSelectionModel().getSelectedIndex() == -1) || 
				(cmbFilterByType.getSelectionModel().getSelectedIndex() == 0 && assumption instanceof AtomAssumption) || 
				(cmbFilterByType.getSelectionModel().getSelectedIndex() == 1 && assumption instanceof CalculatedAssumption))
			if((cmbFilterByDepartment.getSelectionModel().getSelectedIndex() == -1) ||
					(cmbFilterByDepartment.getSelectionModel().getSelectedItem().equals(assumption.getClassification().getDepartment())))
				if((cmbFilterBySubDepartment.getSelectionModel().getSelectedIndex() == -1) || 
						(cmbFilterBySubDepartment.getSelectionModel().getSelectedItem().equals(assumption.getClassification().getSubDepartment())))
					if(cmbFilterByDataType.getSelectionModel().getSelectedIndex() == -1 || 
					(cmbFilterByDataType.getSelectionModel().getSelectedIndex()==assumption.getType().getType().ordinal()))
						lst.add(assumption);
	}

	private void buildNewAssumptionGUI(){
		cmbNewAssumptionType = new ComboBox<>(types);
		cmbNewAssumptionType.setOnAction(e -> typeChangedAction());
		btnNewAssumptionAdd.setOnAction(e -> addAssumptionAction());
		tfNewAssumptionTitle.setMinWidth(385);

		tfNewAssumptionValue.setMaxWidth(VALUE_COLUMN_WIDTH);
		paneNewAssumptionHeader.getChildren().add(lblNewAssumptionHeader);
		
		paneNewDefenitions.add(lblNewAssumptionType, 0, 0);
		paneNewDefenitions.add(cmbNewAssumptionType, 1, 0);
		paneNewDefenitions.add(lblNewAssumptionTitle, 0, 1);
		paneNewDefenitions.add(tfNewAssumptionTitle, 1, 1);
		
		cmbNewAssumptionDataType = new ComboBox<>(dataTypes);
		cmbNewAssumptionDepartment = new ComboBox<>(deps);
		cmbNewAssumptionSubDepartment = new ComboBox<>(subDeps);
		cmbNewAssumptionAction = new ComboBox<>(actions);
		cmbNewAssumptionDepartment.setMinWidth(130);
		cmbNewAssumptionSubDepartment.setMinWidth(130);
		paneNewValuesLeft.add(lblNewAssumptionDataType, 0, 0);
		paneNewValuesLeft.add(cmbNewAssumptionDataType, 1, 0);
		
		paneNewValuesRight.add(lblNewAssumptionDepartment, 0, 0);
		paneNewValuesRight.add(cmbNewAssumptionDepartment, 1, 0);
		paneNewValuesRight.add(lblNewAssumptionSubDep, 0, 1);
		paneNewValuesRight.add(cmbNewAssumptionSubDepartment, 1, 1);
		paneNewValues.getChildren().addAll(paneNewValuesLeft,paneNewValuesRight);
		paneNewAssumptionAddButton.getChildren().add(btnNewAssumptionAdd);
		paneNewAssumptionMessage.getChildren().add(lblMsg);
		paneNewValues.setVisible(false);
		paneNewAssumptionAddButton.setVisible(false);
		paneNewDefenitions.setPadding(new Insets(10,10,10,10));
		paneNewDefenitions.setHgap(10);
		paneNewDefenitions.setVgap(5);
		paneNewValuesLeft.setPadding(new Insets(0,10,10,10));
		paneNewValuesLeft.setHgap(10);
		paneNewValuesLeft.setVgap(5);
		paneNewValuesRight.setPadding(new Insets(0,10,10,10));
		paneNewAssumptionMessage.setPadding(new Insets(0,10,0,10));

		paneNewValuesRight.setHgap(10);
		paneNewValuesRight.setVgap(5);
		lblNewAssumptionAction.setStyle(StylePatterns.LABEL_CSS);
		lblNewAssumptionDataType.setStyle(StylePatterns.LABEL_CSS);
		lblNewAssumptionDepartment.setStyle(StylePatterns.LABEL_CSS);
		lblNewAssumptionSubDep.setStyle(StylePatterns.LABEL_CSS);
		lblNewAssumptionTitle.setStyle(StylePatterns.LABEL_CSS);
		lblNewAssumptionType.setStyle(StylePatterns.LABEL_CSS);
		lblNewAssumptionValue.setStyle(StylePatterns.LABEL_CSS);
		lblNewAssumptionHeader.setStyle(StylePatterns.HEADER_CSS);
		btnNewAssumptionAdd.setStyle(StylePatterns.BUTTON_CSS);
		lblMsg.setStyle(StylePatterns.LABEL_MESSAGE_CSS);
		paneNew.setStyle("-fx-background-color: #FFFBE2;"
				+"-fx-border-width: 1;"
				+ "-fx-border-style: solid inside;"
				+ "-fx-border-color:#ADB4B6;");
		btnNewAssumptionAdd.setOnMousePressed(e -> {
			btnNewAssumptionAdd.setStyle(StylePatterns.BUTTON_HOVERD_CSS);
		});
		btnNewAssumptionAdd.setOnMouseReleased(e -> {
			btnNewAssumptionAdd.setStyle(StylePatterns.BUTTON_CSS);
		});
		paneNew.setMaxWidth(460);
		paneNewAssumptionHeader.setMaxWidth(430);
		paneNewAssumptionAddButton.setMaxWidth(430);
		StackPane.setAlignment(lblNewAssumptionHeader, Pos.CENTER);
		StackPane.setAlignment(btnNewAssumptionAdd, Pos.CENTER_RIGHT);
		StackPane.setAlignment(lblMsg, Pos.CENTER_LEFT);
		cmbNewAssumptionType.setStyle(StylePatterns.COMBO_BOX_CSS);
		cmbNewAssumptionAction.setStyle(StylePatterns.COMBO_BOX_CSS);
		cmbNewAssumptionDataType.setStyle(StylePatterns.COMBO_BOX_CSS);
		cmbNewAssumptionDepartment.setStyle(StylePatterns.COMBO_BOX_CSS);
		cmbNewAssumptionSubDepartment.setStyle(StylePatterns.COMBO_BOX_CSS);
		paneMain.setSpacing(5);
		paneMain.setPadding(new Insets(10,0,10,10));
		paneNew.getChildren().addAll(paneNewAssumptionHeader, paneNewDefenitions, paneNewValues, paneNewAssumptionAddButton, paneNewAssumptionMessage);
	}
	
	private void clearAfterTypeSwitch(){
		if(cmbNewAssumptionType.getSelectionModel().getSelectedIndex()==0){
			clearAfterSwitchToAtom();
		}
		else if(cmbNewAssumptionType.getSelectionModel().getSelectedIndex()==1) {
			clearAfterSwitchToComplex();
		}
	}
	
	private void clearAfterSwitchToComplex() {
		Platform.runLater(() -> {
			cmbNewAssumptionAction.getSelectionModel().select(-1);
		});
	}

	private void clearAfterSwitchToAtom() {
		Platform.runLater(() -> {
			tfNewAssumptionValue.setText("");
		});
	}

	private void addAssumptionAction() {
		if(cmbNewAssumptionType.getSelectionModel().getSelectedIndex()==-1)
			printMessage("Please insret assumption type");
		else{
			if(tfNewAssumptionTitle.getText().equals(""))
				printMessage("Please insert assumption title");
			else if(cmbNewAssumptionDepartment.getSelectionModel().getSelectedIndex()==-1)
				printMessage("Please select department");
			else if(cmbNewAssumptionSubDepartment.getSelectionModel().getSelectedIndex()==-1)
				printMessage("Please select sub department");
			else if(cmbNewAssumptionDataType.getSelectionModel().getSelectedIndex()==-1)
				printMessage("please select data type");
			else {				
				if(cmbNewAssumptionType.getSelectionModel().getSelectedIndex()==0){
					if(tfNewAssumptionValue.getText().equals(""))
						printMessage("Please insert assumption value");
					try{					
						Double.parseDouble(tfNewAssumptionValue.getText());
						assumptions.add(saveAtomAssumption());
						clearAfterCreation();
					}
					catch(Exception e){
						printMessage("Value must be a number");
					}
				}
				else{
					if(cmbNewAssumptionAction.getSelectionModel().getSelectedIndex()==-1)
						printMessage("Please choose assumption Action");
					else{
						assumptions.add(saveCalculatedAssumption());
						clearAfterCreation();
					}
				}
			}		
		} 
	}
	
	private void clearAfterCreation() {
		clearAfterSwitchToAtom();
		clearAfterSwitchToComplex();
		Platform.runLater(() -> {
			tfNewAssumptionTitle.setText("");
			cmbNewAssumptionDataType.getSelectionModel().select(-1);
		});
	}

	private Assumption saveCalculatedAssumption() {
		try{			
			if(isPlanning())
				return manager.createClaculatedAssumptionInPlanning(tfNewAssumptionTitle.getText(),cmbNewAssumptionDepartment.getSelectionModel().getSelectedItem(),
						cmbNewAssumptionSubDepartment.getSelectionModel().getSelectedItem(), Action.values()[cmbNewAssumptionAction.getSelectionModel().getSelectedIndex()],
						AssumptionType.Type.values()[cmbNewAssumptionDataType.getSelectionModel().getSelectedIndex()]);
			
			return manager.createCalculatedAssumptionInActual(tfNewAssumptionTitle.getText(),cmbNewAssumptionDepartment.getSelectionModel().getSelectedItem(),
					cmbNewAssumptionSubDepartment.getSelectionModel().getSelectedItem(), Action.values()[cmbNewAssumptionAction.getSelectionModel().getSelectedIndex()],
					AssumptionType.Type.values()[cmbNewAssumptionDataType.getSelectionModel().getSelectedIndex()]);
		}
		catch (Exception e) {
			printMessage("Problem with saving to DB");
			return null;
		}	
	}
	

	private Assumption saveAtomAssumption(){
		try {
			if(isPlanning())
				return manager.createAtomAssumptionInPlanning(tfNewAssumptionTitle.getText(),cmbNewAssumptionDepartment.getSelectionModel().getSelectedItem(),
						cmbNewAssumptionSubDepartment.getSelectionModel().getSelectedItem(), Double.parseDouble(tfNewAssumptionValue.getText()),
						AssumptionType.Type.values()[cmbNewAssumptionDataType.getSelectionModel().getSelectedIndex()]);
			
			return manager.createAtomAssumptionInActual(tfNewAssumptionTitle.getText(),cmbNewAssumptionDepartment.getSelectionModel().getSelectedItem(),
					cmbNewAssumptionSubDepartment.getSelectionModel().getSelectedItem(), Double.parseDouble(tfNewAssumptionValue.getText()),
					AssumptionType.Type.values()[cmbNewAssumptionDataType.getSelectionModel().getSelectedIndex()]);
			} 
		catch (Exception e) {
				printMessage("Problem with saving to DB");
				return null;
		}
	}
	
	private void printMessage(String msg){
		Platform.runLater(() -> {
			lblMsg.setText(msg);
		});
	}

	private void typeChangedAction() {
		clearAfterTypeSwitch();
		if(cmbNewAssumptionType.getSelectionModel().getSelectedIndex()==-1){
			paneNewValues.setVisible(false);
			paneNewAssumptionAddButton.setVisible(false);
		}
		else if(cmbNewAssumptionType.getSelectionModel().getSelectedIndex()==0){
			paneNewValuesLeft.getChildren().removeAll(lblNewAssumptionAction,cmbNewAssumptionAction);
			paneNewValuesLeft.add(lblNewAssumptionValue, 0, 1);
			paneNewValuesLeft.add(tfNewAssumptionValue, 1, 1);
			paneNewValues.setVisible(true);
			paneNewAssumptionAddButton.setVisible(true);
		}
		else if(cmbNewAssumptionType.getSelectionModel().getSelectedIndex()==1){
			paneNewValuesLeft.getChildren().removeAll(lblNewAssumptionValue,tfNewAssumptionValue);
			paneNewValuesLeft.add(lblNewAssumptionAction, 0, 1);
			paneNewValuesLeft.add(cmbNewAssumptionAction, 1, 1);
			paneNewValues.setVisible(true);
			paneNewAssumptionAddButton.setVisible(true);
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
	
	private void setColumnsCellsFactory() {
		colDataType.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), dataTypes));
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
		colAction.setCellFactory(new ActionCellFactory());
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
	
	private void setColumnsCellsValueFactory(){
		colID.setCellValueFactory(new PropertyValueFactory<Assumption, Integer>("id"));
		colTitle.setCellValueFactory(new PropertyValueFactory<Assumption, String>("title"));
	    colAction.setCellValueFactory( new PropertyValueFactory<>( "DUMMY" ) );
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
		types = FXCollections.observableArrayList("Atom","Complex");
		dataTypes = FXCollections.observableArrayList(AssumptionType.Type.Costs.toString()
				, AssumptionType.Type.Quantity.toString(), AssumptionType.Type.Percentage.toString());
		deps = FXCollections.observableArrayList(manager.getDepartments());
		subDeps = FXCollections.observableArrayList(manager.getSubDepartments());
		actions = FXCollections.observableArrayList(CalculatedAssumption.Action.add.toString()
				,CalculatedAssumption.Action.sub.toString(),CalculatedAssumption.Action.mult.toString());
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
	
	class ActionCellFactory implements Callback<TableColumn<Assumption, String>, TableCell<Assumption, String>>{

		@Override
		public TableCell<Assumption, String> call(TableColumn<Assumption, String> param) {
			 final TableCell<Assumption, String> cell = new TableCell<Assumption, String>(){

                 final Button btn = new Button("Edit Complex");

                 @Override
                 public void updateItem(String item, boolean empty){
                	 btn.setStyle(StylePatterns.TABLE_BUTTON);
                     super.updateItem(item, empty);
                     if(empty){
                         setGraphic( null );
                         setText(null);
                         
                     }
                     else{
                    	 if (getTableView().getItems().get(getIndex()) instanceof AtomAssumption){
                    		 btn.setDisable(true);
                    	 }
                    	 else{
                    		 btn.setDisable(false);
                    	 }                  	 
                		 btn.setOnAction( e -> {
                			 Assumption assumption = getTableView().getItems().get(getIndex());
                			 Platform.runLater(() -> {
                				 try {
                					 Stage aTa = new BuildComplexAssumption(manager, assumption, isPlanning());
                					 aTa.initModality(Modality.APPLICATION_MODAL);
                					 aTa.showAndWait();
                					 hasFilterChange = true;
                					 btnSearch.fire();
                					 table.getColumns().get(0).setVisible(false);
                					 table.getColumns().get(0).setVisible(true);
                				 } catch (Exception e1) {
                					 e1.printStackTrace();
                				 }
                			 });
                		 });
                		 setGraphic(btn);
                		 setText(null);
                		 
                		 btn.setOnMousePressed(e -> {
                			 btn.setStyle(StylePatterns.TABLE_BUTTON_PRESS);
                		 });
                		 btn.setOnMouseReleased(e -> {
                			 btn.setStyle(StylePatterns.TABLE_BUTTON);
                		 });
                	 }
                 }

             };
             return cell;
         }
	}
}
