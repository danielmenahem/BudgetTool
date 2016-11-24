package ui;

import javafx.application.Application;

import java.util.ArrayList;
import java.util.HashMap;

import controlers.BudgetManager;
import interfaces.EnteranceManagerIF;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import ui.forms.FormAssumption;

public class MainContainer extends Scene{

	private BudgetManager manager;
	private Application application;
	
	private static BorderPane paneMain = new BorderPane();
	private static Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

	private MenuBar menuBar = new MenuBar();
	private Menu menuPlanning = new Menu("Planning");
	private Menu menuActual = new Menu("Actual");
	private Menu menuOverview = new Menu("Overview");
	private Menu menuOpen = new Menu("Open Forms");
	private MenuItem pAssumpation = new MenuItem("Assumptions");
	private MenuItem pColumn = new MenuItem("Columns");
	private MenuItem pTable = new MenuItem("Tables");
	private MenuItem aAssumpation = new MenuItem("Assumptions");
	private MenuItem aColumn = new MenuItem("Columns");
	private MenuItem aTable = new MenuItem("Tables");
	private MenuItem showBudget = new MenuItem("Show Budget");
	private HashMap <Pane, MenuItem> openPanes = new HashMap<>();
	
	private FormAssumption frmPlanningAssumption = null;
	private FormAssumption frmActualAssumption = null;
	
	public MainContainer(BudgetManager manager, Application app) {
		super(paneMain, primaryScreenBounds.getWidth()/1.01,primaryScreenBounds.getHeight()/1.05);
		this.manager = manager;
		this.application = app;
		menuPlanning.getItems().addAll(pAssumpation, pColumn, pTable);
		menuActual.getItems().addAll(aAssumpation, aColumn, aTable);
		menuOverview.getItems().add(showBudget);
		menuBar.getMenus().addAll(menuPlanning, menuActual, menuOverview, menuOpen);
		menuBar.setMinWidth(this.getWidth());
		setMenuItemsActions();
		paneMain.setTop(menuBar);
	}
	
	private void setMenuItemsActions(){
		pAssumpation.setOnAction(e -> {
			if(frmPlanningAssumption == null){
				frmPlanningAssumption = new FormAssumption(manager, true);
				MenuItem pa = new MenuItem("Planning Assumptions");
				pa.setOnAction(e2 -> {
					paneMain.setCenter(frmPlanningAssumption);
				});
				openPanes.put(frmPlanningAssumption, pa);
				menuOpen.getItems().add(pa);
				
			}
			paneMain.setCenter(frmPlanningAssumption);
		});
		
		aAssumpation.setOnAction(e -> {
			if(frmActualAssumption == null){
				frmActualAssumption = new FormAssumption(manager, false);
				MenuItem pa = new MenuItem("Actual Assumptions");
				pa.setOnAction(e2 -> {
					paneMain.setCenter(frmActualAssumption);
				});
				openPanes.put(frmActualAssumption, pa);
				menuOpen.getItems().add(pa);	
			}
			paneMain.setCenter(frmActualAssumption);
		});
	}
	
}




