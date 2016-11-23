package BL;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public abstract class Item implements ActionListener{
	
	public final int NUMBER_OF_MONTHS = 12;
	public final String MONTH_NOT_VALID = "Month number is not valid";
	
	private int id;
	
	private boolean isUpdated = false;
	
	private String title;
	private double [] values;
	private Classification classification;
	private String budgetYear;
	
	private ArrayList<ActionListener> listeners;
	
	public Item(int id){
		setId(id);
		listeners = new ArrayList<>();
		this.values = new double [NUMBER_OF_MONTHS];
	}

	public Item(String title, Classification classification, String budgetYear) {
		this.title = title;
		this.values = new double [NUMBER_OF_MONTHS];
		this.classification = classification;
		this.setBudgetYear(budgetYear);
		listeners = new ArrayList<>();
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
		this.isUpdated = false;
	}
	
	public double [] getValues() {
		return values;
	}
	
	public double getValue (int month){
		return values[month - 1];
	}
	
	public void setValues(double value) {
		for(int i=0;i<NUMBER_OF_MONTHS;i++)
			this.values[i] = value;
		
		this.isUpdated = false;
		processEvent();
	}
	
	public Classification getClassification() {
		return classification;
	}
	
	public void setClassification(Classification classification) {
		this.classification = classification;
		this.isUpdated = false;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setValue(double value, int month){
		this.values[month-1] = value;
		this.isUpdated = false;
	}
	
	public void addListener(ActionListener listener) throws Exception{
		if(listener==null)
			throw new Exception("listener can't be null");
		if(!listeners.contains(listener)){
			listeners.add(listener);
			this.isUpdated = false;
		}
	}
	
	public void removeListeners(ActionListener listener) throws Exception{
		boolean isExist  = listeners.remove(listener);
		if(!isExist)
			throw new Exception("listener does not exist");
		this.isUpdated = false;
	}
	
	public void processEvent(){
		for(int i=0;i<listeners.size();i++){
			listeners.get(i).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		}
	}
	
	public void actionPerformed(ActionEvent e){
		
	}

	public String getBudgetYear() {
		return budgetYear;
	}

	public void setBudgetYear(String budjetYear) {
		this.budgetYear = budjetYear;
		this.isUpdated = false;
	}

	public ArrayList<ActionListener> getListeners() {
		return listeners;
	}

	public boolean isUpdated() {
		return isUpdated;
	}

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}
	
	
}
