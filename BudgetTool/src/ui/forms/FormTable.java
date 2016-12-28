package ui.forms;

import java.util.ArrayList;

import bl.Assumption;
import bl.AtomAssumption;
import bl.ReviewTable;
import bl.Table;
import bl.TableComparator;
import bl.TrainingTable;
import interfaces.TablesManagerIF;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

public class FormTable extends Form {
	
	private TablesManagerIF manager;
	private TableView <Table> table;
	private TableColumn <Table, Integer> colID = new TableColumn<>("ID");
	private TableColumn <Table, String> colType = new TableColumn<>("Type");
	private TableColumn <Table, String> colDepartment = new TableColumn<>("Departments");
	private TableColumn <Table, String> colSubDepartment = new TableColumn<>("Sub Department");
	
	private ObservableList<Table> tables;
	private ObservableList<String> types;
	private ObservableList<String> deps;
	private ObservableList<String> subDeps;

	
	public FormTable(TablesManagerIF manager, boolean isPlanning, double width) {
		super(isPlanning, width);
		this.manager = manager;
		setObsLists();
		table = new TableView<>(tables);
		setColumnsCellsValueFactory();
		setColumnsCellsFactory();
		//setColumnsActions();
		//setTableColumns();
		table.setEditable(true);
		//setColsSizesAndAlignment();
		
	}
	
	private void setColumnsCellsFactory() {
		colSubDepartment.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), subDeps));
		colDepartment.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), deps));
	}

	private void setColumnsCellsValueFactory() {
		colID.setCellValueFactory(new PropertyValueFactory<Table, Integer>("id"));
		colType.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Table,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Table, String> param) {
				return param.getValue() instanceof TrainingTable?
						new SimpleStringProperty("Training"):(param.getValue() instanceof ReviewTable?
								new SimpleStringProperty("Review"):new SimpleStringProperty("Standard"));
			}
		});
		
		colDepartment.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Table,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Table, String> param) {
				return new  SimpleStringProperty(param.getValue().getClassification().getDepartment());
			}
		});
		
		colSubDepartment.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Table,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Table, String> param) {
				return new  SimpleStringProperty(param.getValue().getClassification().getSubDepartment());
			}
		});
	}

	private void setObsLists(){
		tables = getObservableData();
		types = FXCollections.observableArrayList("Standard","Training", "Review");
		deps = FXCollections.observableArrayList(manager.getDepartments());
		subDeps = FXCollections.observableArrayList(manager.getSubDepartments());
	}

	private ObservableList<Table> getObservableData() {
		ArrayList<Table> lst = new ArrayList<>();
		if(isPlanning()){
			for(int key : manager.getPlanningTables().keySet())
				lst.add(manager.getPlanningTables().get(key));
		}
		else{
			for(int key : manager.getActualTables().keySet())
				lst.add(manager.getActualTables().get(key));
		}
		lst.sort(new TableComparator());
		return FXCollections.observableArrayList(lst);
	}
}
