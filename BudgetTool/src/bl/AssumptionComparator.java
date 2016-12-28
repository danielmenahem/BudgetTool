package bl;

import java.util.Comparator;

public class AssumptionComparator implements Comparator<Assumption>{

	@Override
	public int compare(Assumption a1, Assumption a2) {
		return a1.compareTo(a2);
	}
}
