package IF;

import java.sql.SQLException;
import java.util.ArrayList;

public interface EnteranceManagerIF {
	ArrayList<String> getBudgetYears() throws SQLException;
	void createNewBudgetYear(String bYear) throws SQLException;
	void readAllData(String budgetYear) throws Exception;
}
