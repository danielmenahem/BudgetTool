package UI;

import java.sql.SQLException;

import Controlers.BudgetManager;
import IF.EnteranceManagerIF;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
	
	public Enterance(EnteranceManagerIF manager) throws SQLException{
		super(paneMain, 305, 200);
		this.manager = manager;
		budgetYears = FXCollections.observableArrayList(manager.getBudgetYears());
		buildGUI();
		setActions();
	}
	
	private void buildGUI() {
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
		paneMain.setAlignment(Pos.CENTER);
		paneCombo.setAlignment(Pos.CENTER);
		paneText.setAlignment(Pos.CENTER);
		paneCreate.setAlignment(Pos.CENTER);
		paneSelect.setAlignment(Pos.CENTER);
		paneCreate.setStyle("-fx-background-color: #d5d6e3;");
		paneSelect.setStyle("-fx-background-color: #d5d6e3;");
		paneCombo.setPadding(new Insets(5,5,10,5));
		paneText.setPadding(new Insets(5,5,10,5));
		paneSelect.setPadding(new Insets(5,5,10,5));
		paneSwitch.setPadding(new Insets(5,5,5,5));
		paneSwitch.setTranslateX(5);
	}
	
	private void setActions(){
		btnSwitch.setOnAction(e -> {
			paneSelect.setVisible(!paneSelect.isVisible());
			paneCreate.setVisible(!paneCreate.isVisible());
			if(paneCreate.isVisible())
				btnSwitch.setText(CHANGE_TO_SELECT_MODE);
			else
				btnSelect.setText(CHANGE_TO_CREATE_MODE);
		});
	}
}
