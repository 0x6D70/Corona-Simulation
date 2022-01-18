package pkgMisc;

import java.util.Comparator;

import pkgSubjects.Person;

public class PersonComparator implements Comparator<Person> {

	@Override
	public int compare(Person p1, Person p2) {
		int ret = 0;
		
		if (p1.getContacts() == p2.getContacts()) {
			ret = p1.getPersonName().compareTo(p2.getPersonName());
		} else {
			ret = p2.getContacts() - p1.getContacts();
		}
		
		return ret;
	}

}
