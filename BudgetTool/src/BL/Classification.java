package BL;

public class Classification {
	
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
			return this.department.equals(c.department) && this.subDepartment.equals(c.subDepartment);
		}
		return false;
	}

	@Override
	public String toString() {
		return "Department: " + this.department + " Sub Department: "+ this.subDepartment;
	}	
}
