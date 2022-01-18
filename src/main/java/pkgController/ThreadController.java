package pkgController;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import pkgData.Coordinate;
import pkgMisc.EventThreadControllerListener;
import pkgMisc.EventThreadControllerObject;
import pkgMisc.EventThreadControllerObject.EVENTTYPE;
import pkgMisc.PersonComparator;
import pkgMisc.PersonHealthComparator;
import pkgMisc.SimulationConstants;
import pkgSubjects.Person;
import pkgSubjects.Person.HEALTHSTATUS;

public class ThreadController implements PropertyChangeListener {
	private ArrayList<Person> persons = new ArrayList<>();
	private ArrayList<EventThreadControllerListener> evtThreadListener = new ArrayList<>();
	private FileWriter writer = null;
		
	public ThreadController() throws IOException {
		File file = new File(SimulationConstants.logFile);
		file.delete();
		file.createNewFile();
	}
	
	public void generateThreads(int count) {
		persons.clear();
		Person.resetDataPool();
		
		for (int i = 0; i < count; i++) {
			Person p = new Person();
			p.addPropertyChangeListener(this);
			
			// set infective
			if ((int)((double)i / count * 100) < SimulationConstants.getPercentInfectedAtStart()) {
				p.setHealthStatus(Person.HEALTHSTATUS.INFECTIVE);
			}
			
			notifyEvtThreadListener(EVENTTYPE.CREATE_PERSON, p);
			
			persons.add(p);
		}
		
		notifyEvtThreadListener(EventThreadControllerObject.EVENTTYPE.ALL_GENERATED);
	}
	
	public void startThreads() throws IOException {
		writer = new FileWriter(SimulationConstants.logFile);
		for (Person p : persons) {
			p.start();			
		}
		
		notifyEvtThreadListener(EventThreadControllerObject.EVENTTYPE.ALL_STARTED);
	}
	
	public void stopThreads() throws IOException {
		for (Person p : persons) {
			p.interrupt();
		}
		
		notifyEvtThreadListener(EventThreadControllerObject.EVENTTYPE.ALL_STOPPED);
		
		writer.close();
	}

	@Override
	public synchronized void propertyChange(PropertyChangeEvent evt) {
		Person source = (Person)evt.getSource();
		
		Coordinate oldCord = (Coordinate)evt.getOldValue();
		Coordinate newCord = (Coordinate)evt.getNewValue();
		
		if (oldCord != null && newCord != null) {
			source.setCoordinate(newCord);
			notifyEvtThreadListener(EVENTTYPE.UPDATE_PERSON, source);
		} else {
			oldCord = source.getOldCord();
			newCord = source.getCord();
			
			String logMessage = source.getPersonName() + " " + oldCord.toStringShort() + " => " + newCord.toStringShort() + "   ";
			
			for (Person p : persons) {
				if (p == source || p.getMoving() || source.getMoving())
					continue;
			
				
				// check if p is within the radius
				double xDiff = newCord.getX() - p.getCord().getX();
				double yDiff = newCord.getY() - p.getCord().getY();
				
				double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
				
				if (distance < SimulationConstants.getCurrentDangerousDistance()) {
					logMessage += " " + p.getPersonName() + ",";
					
					if (source.getHealthStatus() == HEALTHSTATUS.INFECTIVE) {
						p.addInfectiveContacts();
						notifyEvtThreadListener(EVENTTYPE.UPDATE_HEALTH, p);
					}				
					
					if (p.getHealthStatus() == HEALTHSTATUS.INFECTIVE) {
						source.addInfectiveContacts();
						notifyEvtThreadListener(EVENTTYPE.UPDATE_HEALTH, source);
					}
					
					p.addContact();
					source.addContact();				
				}
			}
			
			notifyEvtThreadListener(EVENTTYPE.ADD_LOG, logMessage);
		}
	}
	
	public void addEventThreadControllerListener(EventThreadControllerListener listener) {
		this.evtThreadListener.add(listener);
	}
	
	private void notifyEvtThreadListener(EventThreadControllerObject.EVENTTYPE type) {
		for (EventThreadControllerListener listener : this.evtThreadListener) {
			listener.onEventThreadControllerChanged(new EventThreadControllerObject(this, type));
		}
	}
	
	private void notifyEvtThreadListener(EventThreadControllerObject.EVENTTYPE type, String message) {
		for (EventThreadControllerListener listener : this.evtThreadListener) {
			listener.onEventThreadControllerChanged(new EventThreadControllerObject(this, type, message));
		}
	}
	
	private void notifyEvtThreadListener(EventThreadControllerObject.EVENTTYPE type, Person person) {
		for (EventThreadControllerListener listener : this.evtThreadListener) {
			listener.onEventThreadControllerChanged(new EventThreadControllerObject(this, type, person));
		}
	}
	
	public ArrayList<Person> getPersonsSorted() {
		ArrayList<Person> ret = new ArrayList<>(this.persons);
		
		ret.sort(new PersonComparator());
		
		return ret;
	}
	
	public ArrayList<Person> getPersonsSortedByHealth() {
		ArrayList<Person> ret = new ArrayList<>(this.persons);
		
		ret.sort(new PersonHealthComparator());
		
		return ret;
	}
 }
