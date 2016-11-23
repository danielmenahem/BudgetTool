package BL;

import java.sql.SQLException;

import Controlers.BudgetManager;
import DAL.ActingBudgetDAL;
import DAL.BudgetDAL;

public class Test {

	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();
			BudgetManager bm = BudgetManager.createBudgetManager();
			//bm.loadInitialInformation();
			for(String s : bm.getDepartments())
				System.out.println(s);
			System.out.println("\n" + (System.currentTimeMillis()-start));
			bm.readAllData(bm.getBudgetYears().get(0));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		
/*		ActingBudgetDAL dal = new ActingBudgetDAL();
		dal.test();*/
		try {
			
			//Classification classification = dal.getClassification(BudgetDAL.getConnection(), 2);
			//System.out.println(classification.getDepartment()+ " "+ classification.getSubDepartment());
			//System.out.println(dal.readColumn(new MultColumn(12),BudgetDAL.getConnection()));
			//dal.updateSeason("18/19","17/18");
			//dal.createBudgetYear("17/18");
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

}

