package bl;

import java.util.Comparator;

public class TableComparator implements Comparator<Table> {
	@Override
	public int compare(Table t1, Table t2) {
		return t1.compareTo(t2);
	}
}
