package ui.forms;


import java.util.ArrayList;
import java.util.List;

import com.panemu.tiwulfx.common.TableCriteria;
import com.panemu.tiwulfx.common.TableData;
import com.panemu.tiwulfx.control.sidemenu.SideMenu;
import com.panemu.tiwulfx.table.NumberColumn;
import com.panemu.tiwulfx.table.TableControl;
import com.panemu.tiwulfx.table.TableController;
import com.panemu.tiwulfx.table.TextColumn;
import com.panemu.tiwulfx.table.BaseColumn;

import bl.Assumption;
import bl.AtomAssumption;
import interfaces.AssumptionsMangerIF;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.beans.*;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.beans.property.*;
import com.panemu.tiwulfx.table.CheckBoxColumn;
import com.panemu.tiwulfx.table.ComboBoxColumn;

public class FormAssumption extends Form{
	
	private AssumptionsMangerIF manager;
	private TableControl<bl.Assumption> table = new TableControl<>(bl.Assumption.class);
	//private TableView <Assumption> table = new TableView <>();

	


	public FormAssumption(AssumptionsMangerIF manager, boolean isPlanning){
		
		super(isPlanning);
		this.manager = manager;
		if(isPlanning)
			this.setStyle("-fx-background-color: yellow;");
		else
			this.setStyle("-fx-background-color: blue;");
		
/*		TableColumn <Assumption, Integer> col1 = new TableColumn<>("ID");
		TableColumn <Assumption, String> col2 = new TableColumn<>("Title");
		TableColumn <Assumption, Double> col3 = new TableColumn<>("Jul");
		TableColumn <Assumption, Double> col4 = new TableColumn<>("Aug");
		TableColumn <Assumption, Double> col5 = new TableColumn<>("Sep");
		TableColumn <Assumption, Double> col6 = new TableColumn<>("Oct");
		TableColumn <Assumption, Double> col7 = new TableColumn<>("Nov");
		TableColumn <Assumption, Double> col8 = new TableColumn<>("Dec");
		TableColumn <Assumption, Double> col9 = new TableColumn<>("Jan");
		TableColumn <Assumption, Double> col10 = new TableColumn<>("Feb");
		TableColumn <Assumption, Double> col11 = new TableColumn<>("Mar");
		TableColumn <Assumption, Double> col12 = new TableColumn<>("Apr");
		TableColumn <Assumption, Double> col13 = new TableColumn<>("May");
		TableColumn <Assumption, Double> col14 = new TableColumn<>("Jun");

		col1.setCellValueFactory(new PropertyValueFactory<Assumption, Integer>("id"));
		col2.setCellValueFactory(new PropertyValueFactory<Assumption, String>("title"));
		//col3.setCellValueFactory(new PropertyValueFactory<Assumption, Integer>("values"));
		col3.setCellValueFactory(new MonthValues(1));
		col4.setCellValueFactory(new MonthValues(2));
		col5.setCellValueFactory(new MonthValues(3));
		col6.setCellValueFactory(new MonthValues(4));
		col7.setCellValueFactory(new MonthValues(5));
		col8.setCellValueFactory(new MonthValues(6));
		col9.setCellValueFactory(new MonthValues(7));
		col10.setCellValueFactory(new MonthValues(8));
		col11.setCellValueFactory(new MonthValues(9));
		col12.setCellValueFactory(new MonthValues(10));
		col13.setCellValueFactory(new MonthValues(11));
		col14.setCellValueFactory(new MonthValues(12));

		

		table.getColumns().addAll(col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12, col13, col14);
		table.getItems().addAll(getData());
		this.getChildren().add(table);*/
		
		
		table.setAgileEditing(true);
		NumberColumn<bl.Assumption,Integer> col1 = new NumberColumn<>("id", Integer.class,50);
		col1.setText("ID");
		col1.setEditable(false);
		TextColumn<bl.Assumption> col2 = new TextColumn<>("title",150);
		col2.setText("Title");
		col2.setAlignment(Pos.CENTER);
		NumberColumn<bl.Assumption,Double> col3 = new NumberColumn<>("Jul", Double.class,50);
		col3.setCellValueFactory(new MonthValues(1));
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
/*		CheckBoxColumn <Assumption> isClaculated = new CheckBoxColumn<>("Calculated");
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
		});*/
		ComboBoxColumn<Assumption, String> type = new ComboBoxColumn<>("Assumption Type");
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
		
		table.getColumns().addAll(col1, type, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12, col13, col14);
		table.reloadFirstPage();
		table.setConfigurationID("FrmTstTextColumn");
		this.getChildren().add(table);
	}
	
	private void updateAssumption(Assumption a) {
		if(isPlanning())
			manager.updateAssumptionInPlanning(a);
		else
			manager.updateAssumptionInActual(a);	
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
	
	class AssupmtionTableControler extends TableController<bl.Assumption>{
		
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
					updateAssumption(a);
				}
			}
			return getData();
		}
		
	}
	
}
