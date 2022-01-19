package pkgMisc;

import java.util.Comparator;

import pkgSubjects.Person;

public class PersonHealthComparator implements Comparator<Person> {

	@Override
	public int compare(Person p1, Person p2) {
		int ret = 0;
		
		if (p1.getHealthStatus() == p2.getHealthStatus()) {
			ret = p1.getPersonName().compareTo(p2.getPersonName());
		} else {
			ret = p1.getHealthStatus().ordinal() - p2.getHealthStatus().ordinal();
		}
		
		return ret;
	}

}
