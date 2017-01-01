package interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import bl.Table;

public interface TablesManagerIF {
	ArrayList<String> getDepartments();
	ArrayList<String> getSubDepartments();
	HashMap<Integer, Table> getActualTables();
	HashMap<Integer, Table> getPlanningTables();
	void updateTableInActual(Table table);
	void updateTableInPlanning(Table table);

}
