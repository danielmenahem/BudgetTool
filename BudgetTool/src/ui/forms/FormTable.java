package ui.forms;

import java.util.ArrayList;

import bl.Assumption;
import bl.ReviewTable;
import bl.Table;
import bl.TableComparator;
import bl.TrainingTable;
import interfaces.TablesManagerIF;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import ui.supports.StylePatterns;

public class FormTable extends Form {
	
	private TablesManagerIF manager;
	private TableView <Table> table;
	private TableColumn <Table, Integer> colID = new TableColumn<>("ID");
	private TableColumn <Table, String> colType = new TableColumn<>("Type");
	private TableColumn <Table, String> colDepartment = new TableColumn<>("Departments");
	private TableColumn <Table, String> colSubDepartment = new TableColumn<>("Sub Department");
	
	private Text lblNewTableHeader = new Text("Create New Table");
	private Label lblNewTableType = new Label("Type");
	private Label lblNewTableDepartment = new Label("Department");
	private Label lblNewTableSubDep = new Label("Sub Department");
	//private Label lblNewTrainingTable


	
	private ObservableList<Table> tables;
	private ObservableList<String> types;
	private ObservableList<String> deps;
	private ObservableList<String> subDeps;
	
    private Callback<TableColumn<Table,String>,TableCell<Table,String>> depsCallBack;
    private Callback<TableColumn<Table,String>,TableCell<Table,String>> subDepsCallBack;

	
	public FormTable(TablesManagerIF manager, boolean isPlanning, double width) {
		super(isPlanning, width);
		this.manager = manager;
		setObsLists();
		table = new TableView<>(tables);
		setColumnsCallBacks();
		setColumnsCellsValueFactory();
		setColumnsCellsFactory();
		setColumnsActions();
		setTableColumns();
		table.setEditable(true);
		setColsSizesAndAlignment();
		table.setMaxHeight(360);
		buildNewTableGUI();
		table.setMaxWidth(this.getFormWidth()/1.02);
		this.getChildren().add(table);
	}
	
	private void buildNewTableGUI() {
		
	}

	private void setColsSizesAndAlignment() {
		colID.setPrefWidth(45);
		colDepartment.setPrefWidth(STRING_COLUMN_WIDTH);
		colSubDepartment.setPrefWidth(STRING_COLUMN_WIDTH);
		colType.setPrefWidth(STRING_COLUMN_WIDTH);
		for(TableColumn<?,?> tc : table.getColumns()){
			tc.setStyle( StylePatterns.EDITABLE_TABLE_CELL_CSS);
		}
		colType.setStyle(StylePatterns.NOT_EDITABLE_TABLE_CELL_CSS);
		colID.setStyle(StylePatterns.NOT_EDITABLE_TABLE_CELL_CSS);
	}

	@SuppressWarnings("unchecked")
	private boolean setTableColumns() {
		return table.getColumns().addAll(colID,colType,colDepartment,colSubDepartment);
	}

	private void setColumnsActions() {
		colDepartment.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Table,String>>() {
			@Override
			public void handle(CellEditEvent<Table, String> event) {
				(event.getTableView().getItems().get(event.getTablePosition().getRow()))
				.getClassification().setDepartment(event.getNewValue());
				updateTable((event.getTableView().getItems().get(event.getTablePosition().getRow())));
			}

		});
		
		colSubDepartment.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Table,String>>() {
			@Override
			public void handle(CellEditEvent<Table, String> event) {
				(event.getTableView().getItems().get(event.getTablePosition().getRow()))
				.getClassification().setSubDepartment(event.getNewValue());
				updateTable((event.getTableView().getItems().get(event.getTablePosition().getRow())));
			}
		});
		
	}
	

	private void updateTable(Table table) {
		if(isPlanning())
			manager.updateTableInPlanning(table);
		else
			manager.updateTableInActual(table);
	}

	private void setColumnsCallBacks(){
		depsCallBack = ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), deps);
		subDepsCallBack = ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), subDeps);
	}
	
	private void setColumnsCellsFactory() {

        colSubDepartment.setCellFactory(col -> {
            TableCell<Table, String> cell = subDepsCallBack.call(col);
            addComboBoxCellListener(cell);
            return cell;
        });
		
		colDepartment.setCellFactory(col -> {
            TableCell<Table, String> cell = depsCallBack.call(col);
            addComboBoxCellListener(cell);
            return cell;
        });
	}

	private void addComboBoxCellListener(TableCell<Table, String> cell) {
       cell.itemProperty().addListener((obs, oldValue, newValue) -> {
            @SuppressWarnings("unchecked")
			TableRow<Table> row = cell.getTableRow();
            if (row == null) {
                cell.setEditable(false);
            }
            else {
                Table t = (Table) cell.getTableRow().getItem();
                if(t == null)
                    cell.setEditable(false);
                else if (t.getColumns().size() == 0 && !(t instanceof TrainingTable) && !(t instanceof ReviewTable))
                	cell.setEditable(true);
                else
                	cell.setEditable(false);
            }
            if(cell.isEditable()){
            	cell.setStyle(StylePatterns.EDITABLE_TABLE_CELL_CSS);
            }
            else{
            	cell.setStyle(StylePatterns.NOT_EDITABLE_TABLE_CELL_CSS);
            }
        });
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
