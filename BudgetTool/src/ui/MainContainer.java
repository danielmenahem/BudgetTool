package ui;

import javafx.application.Application;
import javafx.application.Platform;

import java.util.HashMap;

import controlers.BudgetManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import ui.forms.Form;
import ui.forms.FormAssumption;
import ui.forms.FormColumn;
import ui.forms.FormProperties;
import ui.forms.FormTable;
import ui.supports.StylePatterns;

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
	private StackPane paneRemove = new StackPane();
	private Button btnRemove = new Button("Remove Current Form");
	
	private HashMap <Form, MenuItem> openPanes = new HashMap<>();
	private HashMap <FormProperties, String> formNames = new HashMap<>();
	
	private Form frmCurrent = null;
	
	public MainContainer(BudgetManager manager, Application app) {
		super(paneMain, primaryScreenBounds.getWidth()/1.01,primaryScreenBounds.getHeight()/1.05);
		this.manager = manager;
		this.application = app;
		buildGUI();
		initFormsNames();
		setActions();
	}
	
	private void buildGUI(){
		this.getStylesheets().add("/ui/supports/MenuStyle.css");
		menuPlanning.getItems().addAll(pAssumpation, pColumn, pTable);
		menuPlanning.getStyleClass().add("menu");
		menuActual.getItems().addAll(aAssumpation, aColumn, aTable);
		menuOverview.getItems().add(showBudget);
		menuBar.getMenus().addAll(menuPlanning, menuActual, menuOverview, menuOpen);
		menuBar.setMinWidth(this.getWidth());
		menuBar.setStyle(StylePatterns.EDGE_PANE_CSS);
		btnRemove.setStyle(StylePatterns.BUTTON_CSS);
		paneRemove.getChildren().add(btnRemove);
		StackPane.setAlignment(btnRemove, Pos.CENTER_LEFT);
		paneRemove.setStyle(StylePatterns.EDGE_PANE_CSS);
		paneRemove.setPadding(new Insets(5,10,5,10));
		paneMain.setTop(menuBar);
		paneMain.setBottom(paneRemove);
	}
	
	private void initFormsNames(){
		formNames.put(new FormProperties(FormAssumption.class, true),"Planning Assumptions");
		formNames.put(new FormProperties(FormAssumption.class, false),"Actual Assumptions");
		formNames.put(new FormProperties(FormColumn.class, true), "Planning Columns");
		formNames.put(new FormProperties(FormColumn.class, false), "Actual Columns");
		formNames.put(new FormProperties(FormTable.class, true), "Planning Tables");
		formNames.put(new FormProperties(FormTable.class, false), "Actual Tables");
	}
	
	private void setActions(){
		pAssumpation.setOnAction(e -> {
			setMenuAction(FormAssumption.class, true);
		});
		
		aAssumpation.setOnAction(e -> {
			setMenuAction(FormAssumption.class, false);
		});
		
		pColumn.setOnAction(e -> {
			setMenuAction(FormColumn.class, true);
		});
		
		aColumn.setOnAction(e -> {
			setMenuAction(FormColumn.class, false);
		});
		
		pTable.setOnAction(e -> {
			setMenuAction(FormTable.class, true);
		});
		
		aTable.setOnAction(e -> {
			setMenuAction(FormTable.class, false);
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
		
		btnRemove.setOnMousePressed(e->{
			Platform.runLater(() -> {				
				btnRemove.setStyle(StylePatterns.BUTTON_HOVERD_CSS);
			});
		});
		btnRemove.setOnMouseReleased(e->{
			Platform.runLater(() -> {				
				btnRemove.setStyle(StylePatterns.BUTTON_CSS);
			});
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
			return new FormAssumption(this.manager, isPlanning, this.getWidth());
		else if(c == FormColumn.class)
			return new FormColumn(this.manager, isPlanning, this.getWidth());
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
