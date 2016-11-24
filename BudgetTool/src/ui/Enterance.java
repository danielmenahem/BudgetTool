package ui;

import java.sql.SQLException;

import interfaces.EnteranceManagerIF;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Enterance extends Scene{
	
	private EnteranceManagerIF manager;
	private BudgetApplication application;
	
	private static final String CHANGE_TO_CREATE_MODE = "Create New Budget Year";
	private static final String CHANGE_TO_SELECT_MODE = "Select From List";
	
	private ObservableList <String> budgetYears;
	
	private Pane paneSwitch = new Pane();
	private static VBox paneMain = new VBox();
	private VBox paneSelect = new VBox();
	private VBox paneCreate = new VBox();
	private VBox paneGeneral = new VBox();
	private HBox paneCombo = new HBox();
	private HBox paneText = new HBox();
	
	private ComboBox<String> cmbYears;

	private Label lblSelect = new Label("Select Budget Year: "); 
	private Label lblCreate = new Label("Create New Budget Year: ");
	private Label lblInfo = new Label();
	
	private TextField tfCreate = new TextField();
	
	private Button btnSelect = new Button("Start");
	private Button btnCreate = new Button("Create");
	private Button btnSwitch = new Button(CHANGE_TO_CREATE_MODE);
	
	public Enterance(EnteranceManagerIF manager, BudgetApplication app) throws SQLException{
		super(paneMain, 325, 180);
		this.manager = manager;
		this.application = app;
		
		budgetYears = FXCollections.observableArrayList(manager.getBudgetYears());
		buildGUI();
		setActions();
	}
	
	private void buildGUI() {
		setControlsLayout();
		setControlsAlignment();
		setControlsStyle();
		setControlsPadding();
	}
	
	private void setControlsLayout(){
		cmbYears = new ComboBox<>(budgetYears);
		paneCombo.getChildren().addAll(lblSelect, cmbYears);
		paneSelect.getChildren().addAll(paneCombo, btnSelect);
		paneText.getChildren().addAll(lblCreate, tfCreate);
		paneCreate.getChildren().addAll(paneText, btnCreate);
		paneCreate.setVisible(false);
		paneSwitch.setMinWidth(300);
		paneCreate.setMinWidth(290);
		paneSelect.minWidthProperty().bind(paneCreate.widthProperty());
		paneGeneral.getChildren().addAll(lblInfo, btnSwitch);
		paneSwitch.getChildren().addAll(paneSelect, paneCreate);
		paneMain.getChildren().addAll(paneSwitch, paneGeneral);
		paneCreate.translateXProperty().bind(paneSelect.translateXProperty());
		paneCreate.translateYProperty().bind(paneSelect.translateYProperty());
	}
	
	private void setControlsStyle(){
		paneCreate.setStyle("-fx-background-color: #CAD1E6;-fx-border-color: #9CADE4;-fx-border-width: 2px;");
		paneSelect.setStyle("-fx-background-color: #CAD1E6;-fx-border-color: #9CADE4;-fx-border-width: 2px;");
		paneMain.setStyle("-fx-background-color: #8995BA");
		lblInfo.setStyle("-fx-font-size: 11;-fx-font-weight: bold; -fx-text-fill: red");
		lblSelect.setStyle("-fx-font-size: 11;-fx-font-weight: bold;");
		lblCreate.setStyle("-fx-font-size: 11;-fx-font-weight: bold;");
		btnCreate.setStyle("-fx-font-size: 11;-fx-font-weight: bold;");
		btnSelect.setStyle("-fx-font-size: 11;-fx-font-weight: bold;");
		btnSwitch.setStyle("-fx-font-size: 11;-fx-font-weight: bold;");
	}
	
	private void setControlsAlignment(){
		paneMain.setAlignment(Pos.CENTER);
		paneCombo.setAlignment(Pos.CENTER);
		paneText.setAlignment(Pos.CENTER);
		paneCreate.setAlignment(Pos.CENTER);
		paneSelect.setAlignment(Pos.CENTER);
		paneSwitch.setTranslateX(5);
	}
	
	private void setControlsPadding(){
		Insets padding = new Insets(5,5,10,5);
		paneCombo.setPadding(padding);
		paneText.setPadding(padding);
		paneSelect.setPadding(padding);
		paneCreate.setPadding(padding);
		paneSwitch.setPadding(new Insets(5,5,5,5));
		paneGeneral.setPadding(new Insets(10,5,10,5));
		lblInfo.setPadding(new Insets(0,0,5,10));
	}
	
	private void setActions(){
		btnSwitch.setOnAction(e -> {
			paneSelect.setVisible(!paneSelect.isVisible());
			paneCreate.setVisible(!paneCreate.isVisible());
			newInformation("");
			if(paneCreate.isVisible())
				Platform.runLater(() -> {
					btnSwitch.setText(CHANGE_TO_SELECT_MODE);
				});

			else
				Platform.runLater(() -> {
					btnSwitch.setText(CHANGE_TO_CREATE_MODE);
				});
		});
		
		btnCreate.setOnAction(e -> {
			if(tfCreate.getText().equals(""))
				newInformation("Budget year cannot be empty");
			else{
				try {
					manager.createNewBudgetYear(lblInfo.getText());
					manager.readAllData(lblInfo.getText());
					application.startMainContainerScene();
				} catch (Exception e1) {
					newInformation("The inserted budget year already exist");
				}
			}
		});
		
		btnSelect.setOnAction(e -> {
			if(cmbYears.getSelectionModel().getSelectedIndex() == -1){
				newInformation("You must select a budget year to continue");
			}
			else{
				try {
					manager.readAllData(cmbYears.getSelectionModel().getSelectedItem());
					application.startMainContainerScene();
				} catch (Exception e1) {
					newInformation("Problem wuth DB, please try again later");
				}
			}
		});
		

	}
	
	private void newInformation(String info){
		Platform.runLater(() ->{
			lblInfo.setText(info);
		});
	}
}
