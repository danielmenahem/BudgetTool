package UI;

import Controlers.BudgetManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class BudgetApplication extends Application {

	private BudgetManager manager;
	
		
	@Override
	public void start(Stage primaryStage) throws Exception {
		manager = BudgetManager.createBudgetManager();
		primaryStage.setTitle("Enterance");
		primaryStage.setScene(new Enterance(manager));
		primaryStage.show();
		primaryStage.setAlwaysOnTop(true);
	}



	
	public static void main(String[] args) {
		launch(args);
	}
}
