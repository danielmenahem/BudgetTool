package ui;

import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import bl.Assumption;
import bl.AssumptionType.Type;
import bl.CalculatedAssumption;
import bl.CalculatedAssumption.Action;
import bl.CalculatedAssumption.SpecialOperation;
import interfaces.AssumptionsManagerIF;
import javafx.scene.control.ListView;
import javafx.scene.control.ComboBox;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.SelectionMode;
import ui.supports.StylePatterns;
import javafx.scene.shape.Polygon;

public class BuildComplexAssumption extends Stage{
	
	private static final String [] MOUNTHS = {"Jul", "Aug","Sep", "Oct","Nov","Dec","Jan","Feb","Mar","Apr","May","Jun"};
	private static final String REMOVE_STATE = "Remove";
	private static final String ADD_STATE = "Add";
	private static final int NUMBER_OF_MONTHS = 12;
	
	private boolean isPlanning;
	private AssumptionsManagerIF manager;
	private CalculatedAssumption assumption;
	
	private BorderPane paneMain = new BorderPane();
	private VBox paneButtom = new VBox();
	private VBox paneSpecial = new VBox();
	private VBox paneButtons = new VBox();
	private HBox paneSpecialData = new HBox();
	private HBox paneSpecialOperation = new HBox();
	private HBox paneSpacialAssumption = new HBox();
	private HBox paneCenter = new HBox();
	private GridPane paneValues = new GridPane();
	private StackPane paneButtonsContainer = new StackPane();
	private StackPane paneValuesHeader = new StackPane();
	private StackPane paneGridValuesContainer = new StackPane();
	private Label lblAssumptionDetails;
	private ListView<Assumption> lvInnerAssumption;
	private ListView<Assumption> lvAssumptionsToAdd;
	private Button btnAddToAssumption = new Button();
	private Button btnRemoveFromAssumption = new Button();
	private Button btnRemoveOrAddSpecialOperation = new Button(REMOVE_STATE);
	private ComboBox<Assumption> cmbSpecialOperationAssumption = new ComboBox<>();
	private ComboBox<CalculatedAssumption.SpecialOperation> cmbSpecialOperation = new ComboBox<>();
	private Label lblButtomHeader = new Label("Assumption Values");
	private Label lblSpecialOperation = new Label("Select Operation");
	private Label lblSpecialAssumption = new Label("Select Assumption");
	private Label lblSpecialHeader = new Label("Special Operation");
	private Label lblSpecialMsg = new Label();
	private Label [] lblMonths = new Label[NUMBER_OF_MONTHS];
	private Label [] lblValues = new Label[NUMBER_OF_MONTHS];
	private ObservableList<Assumption> innerAssumptions;
	private ObservableList<Assumption> assumptionsToAdd;
	private ObservableList<Assumption> specialAssumptionPool;
	private ObservableList<CalculatedAssumption.SpecialOperation> specialOperation;
	
	public BuildComplexAssumption(AssumptionsManagerIF manager, Assumption assumption, Boolean isPlanning) {
		super();
		this.manager = manager;
		this.assumption = (CalculatedAssumption)assumption;
		this.isPlanning = isPlanning;
		setObsLists();
		buildGUI();
		setButtonsAction();
		setSpecialOperationCurrentState();
		this.setAlwaysOnTop(true);

	}

	private void setSpecialOperationCurrentState() {
		if(assumption.getSpecialOperationAction()!=SpecialOperation.none){
			cmbSpecialOperation.getSelectionModel().select(assumption.getSpecialOperationAction());
			cmbSpecialOperationAssumption.getSelectionModel().select(assumption.getSpecialOperationAssumption());
			prepareRemoveState();
		}
		else{
			prepareAddState();
		}
	}

	private void setButtonsAction() {
		btnAddToAssumption.setOnAction(e ->  addInnerAssumptions());
		btnAddToAssumption.setOnMousePressed(e -> {
			btnAddToAssumption.setStyle(StylePatterns.LEFT_ARROW_BUTTON_PRESSED_CSS);
		});
		btnAddToAssumption.setOnMouseReleased(e -> {
			btnAddToAssumption.setStyle(StylePatterns.LEFT_ARROW_BUTTON_CSS);
		});
		btnRemoveFromAssumption.setOnAction(e -> removeInnerAssumptions());
		
		btnRemoveFromAssumption.setOnMousePressed(e -> {
			btnRemoveFromAssumption.setStyle(StylePatterns.RIGHT_ARROW_BUTTON_PRESSED_CSS);
		});
		btnRemoveFromAssumption.setOnMouseReleased(e -> {
			btnRemoveFromAssumption.setStyle(StylePatterns.RIGHT_ARROW_BUTTON_CSS);
		});
		
		btnRemoveOrAddSpecialOperation.setOnAction(e -> specialAssumptionAction());
		
		btnRemoveOrAddSpecialOperation.setOnMousePressed(e -> {
			btnRemoveOrAddSpecialOperation.setStyle(StylePatterns.BUTTON_HOVERD_CSS);
		});
		btnRemoveOrAddSpecialOperation.setOnMouseReleased(e -> {
			btnRemoveOrAddSpecialOperation.setStyle(StylePatterns.BUTTON_CSS);
		});
	}
	
	private void specialAssumptionAction() {
		if(btnRemoveOrAddSpecialOperation.getText()==ADD_STATE)
			addSpecialAssumption();
		else
			removeSpecialAssumption();
	}
	
	private void removeSpecialAssumption() {
		try {
			Assumption a = assumption.removeSpecialOperation();
			updateAssumption();
			updateValues();
			assumptionsToAdd.add(a);
			prepareAddState();
			showMsg("");
		} catch (Exception e) {
			showMsg(e.getMessage());
		}
	}

	private void addSpecialAssumption(){
		if(cmbSpecialOperation.getSelectionModel().getSelectedIndex()!=-1 && 
				cmbSpecialOperationAssumption.getSelectionModel().getSelectedIndex()!=-1){
			try {
				Assumption a = cmbSpecialOperationAssumption.getSelectionModel().getSelectedItem();
				assumption.setSpecialOperationAssumption(a,
						cmbSpecialOperation.getSelectionModel().getSelectedItem());
				updateAssumption();
				updateValues();
				prepareRemoveState();
				showMsg("");
				assumptionsToAdd.remove(a);
			} catch (Exception e) {
				showMsg(e.getMessage());
			}
		}
		else{
			showMsg("You must select operation and assumption to add special assumption");
		}
	}
	
	private void prepareRemoveState(){
		cmbSpecialOperation.setDisable(true);
		cmbSpecialOperationAssumption.setDisable(true);
		btnRemoveOrAddSpecialOperation.setText(REMOVE_STATE);
	}
	
	private void prepareAddState(){
		cmbSpecialOperation.setDisable(false);
		cmbSpecialOperationAssumption.setDisable(false);
		cmbSpecialOperation.getSelectionModel().select(-1);
		cmbSpecialOperationAssumption.getSelectionModel().select(-1);
		btnRemoveOrAddSpecialOperation.setText(ADD_STATE);
	}
	
	private void showMsg(String msg){
		Platform.runLater(() -> {
			lblSpecialMsg.setText(msg);
		});
	}

	private void removeInnerAssumptions(){
		showMsg("");
		ObservableList<Assumption> selectedAssumption = lvInnerAssumption.getSelectionModel().getSelectedItems();
		ArrayList<Assumption> listAssumptions = new ArrayList<>();
		for(Assumption a : selectedAssumption){
			listAssumptions.add(a);
		}
		
		for(Assumption a : listAssumptions){
			try {
				this.assumption.removeAssumption(a);
				this.innerAssumptions.remove(a);
				this.assumptionsToAdd.add(a);
				if(a.getType().getType() != Type.Percentage)
					this.specialAssumptionPool.add(a);
			} catch (Exception e1) {
			}
		}
		updateAssumption();
		updateValues();
	}
	
	private void addInnerAssumptions(){
		showMsg("");
		ObservableList<Assumption> selectedAssumption = lvAssumptionsToAdd.getSelectionModel().getSelectedItems();
		ArrayList<Assumption> listAssumptions = new ArrayList<>();
		for(Assumption a : selectedAssumption){
			listAssumptions.add(a);
		}
		for(Assumption a : listAssumptions){
			try {
				this.assumption.addAssumption(a);
				this.innerAssumptions.add(a);
				this.assumptionsToAdd.remove(a);
				if(specialAssumptionPool.contains(a))
					this.specialAssumptionPool.remove(a);
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
		this.assumptionsToAdd = getAssumptionsToAdd(false);
		this.specialAssumptionPool = getAssumptionsToAdd(true);
		this.specialOperation =  FXCollections.observableArrayList(CalculatedAssumption.SpecialOperation.dev,
				CalculatedAssumption.SpecialOperation.sub);
	}

	private void buildGUI() {
		Scene scene = new Scene(paneMain);
		buildHeader();
		buildCenterPane();
		buildButtomPane();
		paneMain.setTop(lblAssumptionDetails);
		paneMain.setCenter(paneCenter);
		paneMain.setBottom(paneButtom);
		paneMain.setPadding(new Insets(10,10,10,10));
		setBackgroundColor();
		this.setScene(scene);
	}
	
	private void buildButtomPane() {
		buildSpecialPane();
		buildValuesPane();
		paneButtom.setSpacing(5);
		paneButtom.getChildren().addAll(paneSpecial, paneValuesHeader,paneGridValuesContainer);
		paneButtom.setPadding(new Insets(5));
	}

	private void buildSpecialPane() {
		cmbSpecialOperation = new ComboBox<>(specialOperation);
		cmbSpecialOperationAssumption = new ComboBox<>(specialAssumptionPool);
		paneSpecialOperation.getChildren().addAll(lblSpecialOperation, cmbSpecialOperation);
		paneSpacialAssumption.getChildren().addAll(lblSpecialAssumption, cmbSpecialOperationAssumption);
		paneSpecialData.getChildren().addAll(paneSpecialOperation, paneSpacialAssumption, btnRemoveOrAddSpecialOperation);
		paneSpecial.getChildren().addAll(lblSpecialHeader, paneSpecialData, lblSpecialMsg);
		paneSpacialAssumption.setSpacing(5);
		paneSpecialOperation.setSpacing(5);
		paneSpecialData.setSpacing(10);
		paneSpecial.setSpacing(5);
		paneSpecial.setAlignment(Pos.CENTER);
		lblSpecialHeader.setStyle(StylePatterns.SUB_TITLE_CSS);
		lblSpecialAssumption.setStyle(StylePatterns.LABEL_CSS);
		lblSpecialOperation.setStyle(StylePatterns.LABEL_CSS);
		lblSpecialMsg.setStyle(StylePatterns.LABEL_MESSAGE_CSS);
		cmbSpecialOperation.setStyle(StylePatterns.COMBO_BOX_CSS);
		cmbSpecialOperationAssumption.setStyle(StylePatterns.COMBO_BOX_CSS);
		btnRemoveOrAddSpecialOperation.setStyle(StylePatterns.BUTTON_CSS);
	}

	private void buildHeader(){
		lblAssumptionDetails = new Label(this.assumption.toString() + ", Action: " +assumption.getAction());
		lblAssumptionDetails.setStyle(StylePatterns.HEADER_CSS);
		lblAssumptionDetails.setTextAlignment(TextAlignment.CENTER);
		lblAssumptionDetails.setPadding(new Insets(0,0,5,0));
		BorderPane.setAlignment(lblAssumptionDetails, Pos.CENTER);
	}
	
	private void buildCenterPane(){
		lvInnerAssumption = new ListView<>(this.innerAssumptions);
		lvAssumptionsToAdd = new ListView<>(this.assumptionsToAdd);
		lvInnerAssumption.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		lvAssumptionsToAdd.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		paneButtons.getChildren().addAll(btnAddToAssumption, btnRemoveFromAssumption);
		paneButtonsContainer.getChildren().add(paneButtons);
		paneButtons.setMaxHeight(50);
		paneButtons.setPadding(new Insets(5));
		paneButtons.setSpacing(10);
		BorderPane.setAlignment(paneCenter, Pos.CENTER);
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
		btnAddToAssumption.setStyle(StylePatterns.LEFT_ARROW_BUTTON_CSS);
		btnRemoveFromAssumption.setShape(rightArrow);
		btnRemoveFromAssumption.setStyle(StylePatterns.RIGHT_ARROW_BUTTON_CSS);
		paneCenter.getChildren().addAll(lvInnerAssumption, paneButtonsContainer, lvAssumptionsToAdd);
	}
	
	private void setBackgroundColor(){
		if(isPlanning)
			paneMain.setStyle(StylePatterns.PLANNING_BACKGROUND_CSS);
		else
			paneMain.setStyle(StylePatterns.ACTUAL_BACKGROUND_CSS);
	}
	
	private void buildValuesPane() {
		for(int i = 0; i<NUMBER_OF_MONTHS; i++){
			lblMonths[i] = new Label(MOUNTHS[i]);
			lblValues[i] = new Label(new DecimalFormat("##.##").format(assumption.getValue(i+1)));
			if(i%2 == 0){				
				lblMonths[i].setStyle(StylePatterns.TABLE_EVEN_LABEL_CSS);
				lblValues[i].setStyle(StylePatterns.TABLE_EVEN_LABEL_CSS);
			}
			else{
				lblMonths[i].setStyle(StylePatterns.TABLE_ODD_LABEL_CSS);
				lblValues[i].setStyle(StylePatterns.TABLE_ODD_LABEL_CSS);
			}
			paneValues.add(lblMonths[i], i, 0);
			paneValues.add(lblValues[i], i, 1);
		}
		lblButtomHeader.setStyle(StylePatterns.SUB_TITLE_CSS);
		paneValuesHeader.getChildren().add(lblButtomHeader);
		paneGridValuesContainer.getChildren().add(paneValues);
		StackPane.setAlignment(paneButtons, Pos.CENTER);
		StackPane.setAlignment(lblButtomHeader, Pos.CENTER);
		StackPane.setAlignment(paneValues, Pos.CENTER);
		paneValues.setAlignment(Pos.CENTER);
		paneValues.setHgap(12);
		paneValues.setVgap(4);
	}
	
	private void updateValues(){
		Platform.runLater(() -> {
			for(int i = 0; i<NUMBER_OF_MONTHS; i++){
				lblValues[i].setText(new DecimalFormat("##.##").format(assumption.getValue(i+1)));
			}
		});
	}

	private ObservableList<Assumption> getAssumptionsToAdd(boolean isForSpecial){
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
				boolean skip = false;
				if(isForSpecial && assumptionToCheck.getType().getType() == Type.Percentage)
					skip = true;
				else if(assumption.getAction()!=Action.mult && assumptionToCheck.getType().getType() == Type.Percentage)
					skip = true;
				else{					
					for(Assumption a : assumption.getAssumptions()){
						if(assumptionToCheck.getId()==a.getId()){
							skip = true;
							break;
						}
					}
					if(!skip){						
						for(ActionListener al : assumption.getListeners()){
							if(al instanceof Assumption){
								Assumption a = (Assumption)al;
								if(a.getId() == assumptionToCheck.getId()){
									skip = true;
									break;
								}
							}
						}
					}
					if(!isForSpecial){						
						if(!skip)
							if(assumption.getSpecialOperationAssumption() != null)
								if(assumption.getSpecialOperationAssumption().getId() == assumptionToCheck.getId())
									skip = true;
					}
				}
				if(!skip)
					toAdd.add(assumptionToCheck);
				
			}
		}
		return FXCollections.observableArrayList(toAdd);
	}
	
}
