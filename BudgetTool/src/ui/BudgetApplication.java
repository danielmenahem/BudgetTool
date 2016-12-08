package ui;

import controlers.BudgetManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BudgetApplication extends Application {

	private BudgetManager manager;
	private Stage primaryStage;
	
		
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.manager = BudgetManager.createBudgetManager();
		this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setTitle("Enterance");
		primaryStage.setScene(new Enterance(manager, this));
		primaryStage.show();
		primaryStage.setAlwaysOnTop(true);
	}
	
	public void startMainContainerScene(){
		primaryStage.hide();
		primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.DECORATED);
		primaryStage.setScene(new MainContainer(manager, this));
		primaryStage.setTitle("Budget Tool");
		primaryStage.setAlwaysOnTop(false);
		primaryStage.setX(0);
		primaryStage.setY(0);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
