package bl;

public class Classification implements Comparable<Classification> {
	
	private String department;
	private String subDepartment;
	
	
	public Classification(String department, String subDepartment) {
		this.department = department;
		this.subDepartment = subDepartment;
	}
	
	public String getDepartment() {
		return department;
	}
	
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public String getSubDepartment() {
		return subDepartment;
	}
	public void setSubDepartment(String subDepartment) {
		this.subDepartment = subDepartment;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Classification){
			Classification c = (Classification)o;
			return this.department.equals(c.getDepartment()) && this.subDepartment.equals(c.getSubDepartment());
		}
		return false;
	}
	
	

	@Override
	public String toString() {
		return "Department: " + this.department + " Sub Department: "+ this.subDepartment;
	}

	@Override
	public int compareTo(Classification c) {
		if(this.equals(c))
			return 0;
		else if(this.getDepartment().equals(c.getDepartment()))
			return this.getSubDepartment().compareTo(c.getSubDepartment());
		return this.getDepartment().compareTo(c.getDepartment());
	}	
}
