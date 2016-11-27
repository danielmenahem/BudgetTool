package ui;

import javafx.application.Application;

import java.util.HashMap;

import controlers.BudgetManager;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import ui.forms.Form;
import ui.forms.FormAssumption;
import ui.forms.FormProperties;

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
	private Button btnRemove = new Button("Remove Current Form");
	
	private HashMap <Form, MenuItem> openPanes = new HashMap<>();
	private HashMap <FormProperties, String> formNames = new HashMap<>();
	
	private Form frmCurrent = null;
	
	public MainContainer(BudgetManager manager, Application app) {
		super(paneMain, primaryScreenBounds.getWidth()/1.01,primaryScreenBounds.getHeight()/1.05);
		this.manager = manager;
		this.application = app;
		buildGUI();
		initFromsNames();
		setActions();
	}
	
	private void buildGUI(){
		menuPlanning.getItems().addAll(pAssumpation, pColumn, pTable);
		menuActual.getItems().addAll(aAssumpation, aColumn, aTable);
		menuOverview.getItems().add(showBudget);
		menuBar.getMenus().addAll(menuPlanning, menuActual, menuOverview, menuOpen);
		menuBar.setMinWidth(this.getWidth());
		paneMain.setTop(menuBar);
		paneMain.setBottom(btnRemove);
	}
	
	private void initFromsNames(){
		formNames.put(new FormProperties(FormAssumption.class, true),"Planning Assumptions");
		formNames.put(new FormProperties(FormAssumption.class, false),"Actual Assumptions");
	}
	
	private void setActions(){
		pAssumpation.setOnAction(e -> {
			setMenuAction(FormAssumption.class, true);
		});
		
		aAssumpation.setOnAction(e -> {
			setMenuAction(FormAssumption.class, false);
		});
		
		btnRemove.setOnAction(e -> {
			int index = menuOpen.getItems().indexOf(openPanes.get(frmCurrent));
			menuOpen.getItems().remove(openPanes.get(frmCurrent));
			openPanes.remove(frmCurrent);
			if(index > 0){
				menuOpen.getItems().get(index-1).getOnAction().handle(e);
			}
			else{				
				frmCurrent = null;
				paneMain.setCenter(null);
			}
		});
	}
	
	private void setMenuAction(Class <?> c, boolean isPlanning){
		Form frm  = getForm(c, isPlanning);
		if(frm == null){
			frm = createForm(c, isPlanning);
			MenuItem pa = new MenuItem(formNames.get(new FormProperties(c, isPlanning)));
			pa.setOnAction(e2 -> {
				Form fa  = getForm(c, isPlanning);
				paneMain.setCenter(fa);
				frmCurrent = fa;
			});
			openPanes.put(frm, pa);
			menuOpen.getItems().add(pa);	
		}
		paneMain.setCenter(frm);
		frmCurrent = frm;
	}
	
	private Form createForm(Class<?>c, boolean isPlanning){
		if (c == FormAssumption.class)
			return new FormAssumption(manager, isPlanning);
		return null;
	}
	
	private Form getForm(Class<?>c, boolean isPlanning){
		for(Form form : openPanes.keySet()){
			if(c.isInstance(form) && form.isPlanning() == isPlanning){
				return form;
			}
		}
		return null;
	}
}
