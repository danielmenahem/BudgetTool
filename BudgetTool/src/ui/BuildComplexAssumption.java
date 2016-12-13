package ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import bl.Assumption;
import bl.AssumptionType.Type;
import bl.CalculatedAssumption;
import bl.CalculatedAssumption.Action;
import interfaces.AssumptionsManagerIF;
import javafx.scene.control.ListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ui.supports.StylePatterns;
import javafx.scene.shape.Polygon;

public class BuildComplexAssumption extends Stage{
	
	private static final String [] MOUNTHS = {"Jul", "Aug","Sep", "Oct","Nov","Dec","Jan","Feb","Mar","Apr","May","Jun"};
	private static final int NUMBER_OF_MONTHS = 12;
	
	private AssumptionsManagerIF manager;
	private CalculatedAssumption assumption;
	private boolean isPlanning;
	private VBox paneButtom = new VBox();
	private GridPane paneValues = new GridPane();
	private BorderPane paneMain = new BorderPane();
	private StackPane paneCenter = new StackPane();
	private StackPane paneValuesHeader = new StackPane();
	private StackPane paneGridValuesContainer = new StackPane();
	private VBox paneButtons = new VBox();
	private Label lblAssumptionDetails;
	private ListView<Assumption> lvInnerAssumption;
	private ListView<Assumption> lvAssumptionsToAdd;
	private Button btnAddToAssumption = new Button();
	private Button btnRemoveFromAssumption = new Button();
	private Label lblButtomHeader = new Label("Assumption Values");
	private Label [] lblMonths = new Label[NUMBER_OF_MONTHS];
	private Label [] lblValues = new Label[NUMBER_OF_MONTHS];
	private ObservableList<Assumption> innerAssumptions;
	private ObservableList<Assumption> assumptionsToAdd;
	
	public BuildComplexAssumption(AssumptionsManagerIF manager, Assumption assumption, Boolean isPlanning) {
		super();
		this.manager = manager;
		this.assumption = (CalculatedAssumption)assumption;
		this.isPlanning = isPlanning;
		setObsLists();
		buildGUI();
		setButtonsAction();
		this.setAlwaysOnTop(true);

	}

	private void setButtonsAction() {
		btnAddToAssumption.setOnAction(e ->  addInnerAssumptions());
		btnRemoveFromAssumption.setOnAction(e -> removeInnerAssumptions());
	}
	
	private void removeInnerAssumptions(){
		for(Assumption a : lvInnerAssumption.getSelectionModel().getSelectedItems()){
			try {
				this.assumption.removeAssumption(a);
				this.innerAssumptions.remove(a);
				this.assumptionsToAdd.add(a);
			} catch (Exception e1) {
			}
		}
		updateAssumption();
		updateValues();
	}
	
	private void addInnerAssumptions(){
		for(Assumption a : lvAssumptionsToAdd.getSelectionModel().getSelectedItems()){
			try {
				this.assumption.addAssumption(a);
				this.innerAssumptions.add(a);
				this.assumptionsToAdd.remove(a);
			} catch (Exception e1) {
			}
		}
		updateAssumption();
		updateValues();
	}
	
	private void updateAssumption(){
		if(this.isPlanning)
			manager.updateAssumptionInPlanning(assumption);
		else
			manager.updateAssumptionInActual(assumption);
	}

	private void setObsLists() {
		this.innerAssumptions = FXCollections.observableArrayList(assumption.getAssumptions());
		this.assumptionsToAdd = getAssumptionsToAdd();
	}

	private void buildGUI() {
		Scene scene = new Scene(paneMain);
		//scene.getStylesheets().add("/ui/supports/GridStyle.css");
		lblAssumptionDetails = new Label(this.assumption.toString() + " Action: " +assumption.getAction());
		lvInnerAssumption = new ListView<>(this.innerAssumptions);
		lvAssumptionsToAdd = new ListView<>(this.assumptionsToAdd);
		paneButtons.getChildren().addAll(btnAddToAssumption, btnRemoveFromAssumption);
		paneCenter.getChildren().add(paneButtons);
		paneButtons.setMaxHeight(50);
		paneButtons.setPadding(new Insets(5));
		paneButtons.setSpacing(10);
		lblButtomHeader.setStyle(StylePatterns.HEADER_CSS);
		paneValuesHeader.getChildren().add(lblButtomHeader);
		paneGridValuesContainer.getChildren().add(paneValues);
		StackPane.setAlignment(paneButtons, Pos.CENTER);
		StackPane.setAlignment(lblButtomHeader, Pos.CENTER);
		StackPane.setAlignment(paneValues, Pos.CENTER);
		paneMain.setTop(lblAssumptionDetails);
		paneMain.setLeft(lvInnerAssumption);
		paneMain.setRight(lvAssumptionsToAdd);
		paneMain.setCenter(paneCenter);
		paneMain.setBottom(paneButtom);
		buildValuesPane();
		paneValues.setHgap(12);
		paneValues.setVgap(4);
		//paneValues.getStyleClass().add("value-grid");
		paneButtom.getChildren().addAll(paneValuesHeader,paneGridValuesContainer);
		paneValues.setAlignment(Pos.CENTER);
		paneMain.setPadding(new Insets(10,10,10,10));
		Polygon leftArrow = new Polygon();
		leftArrow.getPoints().addAll(new Double[]{
				   30.0, 30.0,
				   30.0, 0.0,
				   00.0, 15.0
		});
		Polygon rightArrow = new Polygon();
		rightArrow.getPoints().addAll(new Double[]{
				   0.0, 30.0,
				   0.0, 0.0,
				   30.0, 15.0
		});
		btnAddToAssumption.setShape(leftArrow);
		btnRemoveFromAssumption.setShape(rightArrow);
		this.setScene(scene);
	}
	
	private void buildValuesPane() {
		for(int i = 0; i<NUMBER_OF_MONTHS; i++){
			lblMonths[i] = new Label(MOUNTHS[i]);
			lblValues[i] = new Label(new DecimalFormat("##.##").format(assumption.getValue(i+1)));
			lblMonths[i].setStyle(StylePatterns.LABEL_CSS);
			lblValues[i].setStyle(StylePatterns.LABEL_CSS);
			paneValues.add(lblMonths[i], i, 0);
			paneValues.add(lblValues[i], i, 1);
		}
	}
	
	private void updateValues(){
		Platform.runLater(() -> {
			for(int i = 0; i<NUMBER_OF_MONTHS; i++){
				lblValues[i].setText(new DecimalFormat("##.##").format(assumption.getValue(i+1)));
			}
		});
	}

	private ObservableList<Assumption> getAssumptionsToAdd(){
		ArrayList<Assumption> toAdd = new ArrayList<>();
		HashMap<Integer, Assumption> assumptionPool;
		if(isPlanning)
			assumptionPool = this.manager.getPlanningAssumptions();
		else
			assumptionPool = this.manager.getActualAssumptions();
		
		for(int id: assumptionPool.keySet()){
			Assumption assumptionToCheck = assumptionPool.get(id);
			
			if(assumptionToCheck.getClassification().equals(assumption.getClassification()) && 
					assumptionToCheck.getId() != assumption.getId()){
				if(assumption.getAction()==Action.mult || assumptionToCheck.getType().getType() != Type.Percentage){					
					boolean exist = false;
					for(Assumption a : assumption.getAssumptions()){
						if(assumptionToCheck.getId()==a.getId()){
							exist = true;
							break;
						}
					}
					if(!exist)
						toAdd.add(assumptionToCheck);
				}
			}
		}
		return FXCollections.observableArrayList(toAdd);
	}
	
}
