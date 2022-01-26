package pkgController;



import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import pkgData.Coordinate;
import pkgData.Settings;
import pkgMisc.EventThreadControllerListener;
import pkgMisc.EventThreadControllerObject;
import pkgMisc.MapLoader;
import pkgMisc.EventThreadControllerObject.EVENTTYPE;
import pkgMisc.MapLoader.TILE_TYPES;
import pkgMisc.PersonComparator;
import pkgMisc.PersonHealthComparator;
import pkgMisc.SimulationConstants;
import pkgSubjects.Person;
import pkgSubjects.Person.HEALTHSTATUS;
import pkgSubjects.Person.JOBSTATUS;

public class ThreadController implements PropertyChangeListener {
	private ArrayList<Person> persons = new ArrayList<>();
	private ArrayList<EventThreadControllerListener> evtThreadListener = new ArrayList<>();
	private ArrayList<Coordinate> teacherSeats = new ArrayList<>();
	
	private Settings settings; 
	
	private Coordinate entrance = null;
		
	public ThreadController() throws IOException {
		File file = new File(SimulationConstants.logFile);
		file.delete();
		file.createNewFile();
	}
	
	public void generateThreads() {
		persons.clear();
		Person.resetDataPool();
		
		TILE_TYPES[][] tileTypes = MapLoader.getTileTypes();
		
		
		for (int y = 0; y < SimulationConstants.TILE_COUNT_HEIGHT; y++) {
			for (int x = 0; x < SimulationConstants.TILE_COUNT_WIDTH; x++) {
				TILE_TYPES type = tileTypes[y][x];
				
				if (type == TILE_TYPES.PUPIL_SEAT) {
					Person p = new Person();
					p.setJobStatus(JOBSTATUS.PUPIL);
					p.setMainPosition(new Coordinate(
						SimulationConstants.TILE_WIDTH * x, SimulationConstants.TILE_HEIGHT * y
					));
					p.addPropertyChangeListener(this);
					persons.add(p);
				} else if (type == TILE_TYPES.TEACHER_SEAT_CHAMBER) {
					Person p = new Person();
					p.setJobStatus(JOBSTATUS.TEACHER);
					p.setMainPosition(new Coordinate(
						SimulationConstants.TILE_WIDTH * x, SimulationConstants.TILE_HEIGHT * y
					));
					p.addPropertyChangeListener(this);
					persons.add(p);
				} else if (type == TILE_TYPES.ENTRANCE && entrance == null) {
					entrance = new Coordinate(
						SimulationConstants.TILE_WIDTH * x, SimulationConstants.TILE_HEIGHT * y
					);
				} else if (type == TILE_TYPES.TEACHER_SEAT_CLASS) {
					teacherSeats.add(new Coordinate(
						SimulationConstants.TILE_WIDTH * x, SimulationConstants.TILE_HEIGHT * y
					));
				}
			}
		}
		
		//entrance.setX(SimulationConstants.TILE_WIDTH);
		
		int randnumber;
		
		for (Person p : persons) {
			randnumber = getRandNumber(100);
			
			if (randnumber <= settings.getVaccinated()) {
				p.setVaccinated(true);
			}
			
			randnumber = getRandNumber(100);
			
			if (randnumber <= settings.getInfective()) {
				p.setHealthStatus(HEALTHSTATUS.INFECTIVE);
			}			
			
			p.setCoordinate(entrance);
			notifyEvtThreadListener(EVENTTYPE.CREATE_PERSON, p);
		}		
		
		notifyEvtThreadListener(EventThreadControllerObject.EVENTTYPE.ALL_GENERATED);
	}
	
	public void startThreads() throws Exception {
		// do movements
		for (Person p : persons) {
			p.setCoordinate(new Coordinate(p.getMainPosition()));
			notifyEvtThreadListener(EVENTTYPE.UPDATE_PERSON, p);
		}
		
		//Thread.sleep(10000);
		
		for (Person p : persons) {
			p.setCoordinate(new Coordinate(entrance));
			notifyEvtThreadListener(EVENTTYPE.UPDATE_PERSON, p);
			break;
		}
		
		
		// sleep without blocking the thread
		new Thread( new Runnable() {
	        public void run()  {
	        	try {
		            Thread.sleep(SimulationConstants.SLEEP_BETWEEN_ANIMATION); // 2s Animation time -> pupil sit for
		            
		            int randNumber;
		            for (int i = 0; i < persons.size(); i++) {
		            	randNumber = getRandNumber(100);
		            	Person p = persons.get(i);
		            	if (p.getHealthStatus() == HEALTHSTATUS.INFECTIVE && randNumber < settings.getTestsUsefull()) {
		            		p.setCoordinate(entrance);
		            		notifyEvtThreadListener(EVENTTYPE.UPDATE_PERSON, p);
		            		persons.remove(i);
		            		i--;
		            	}
		            }
		            
		            for (int i = 0; i < SimulationConstants.NUMBER_OF_LESSONS; i++) {
		            	int counter = 0;
		            	
		            	for (Person p : persons) {
		            		if (p.getJobStatus() == JOBSTATUS.TEACHER && counter != teacherSeats.size()) {
		            			p.setCoordinate(teacherSeats.get(counter));
		            			notifyEvtThreadListener(EVENTTYPE.UPDATE_PERSON, p);
		            			counter++;
		            		}
		            		p.checkEnvironment();
		            	}
		            	
		            	Thread.sleep(SimulationConstants.SLEEP_BETWEEN_ANIMATION);
		            	
		            	for (Person p : persons) {
		            		if (p.getJobStatus() == JOBSTATUS.TEACHER && !p.getCord().equals(p.getMainPosition()) ) {
		            			p.setCoordinate(p.getMainPosition());
		            			notifyEvtThreadListener(EVENTTYPE.UPDATE_PERSON, p);
		            		}
		            	}
		            	
		            	Thread.sleep(SimulationConstants.SLEEP_BETWEEN_ANIMATION);
		            }
	
		            // move back to entrance
		            for (Person p : persons) {
		    			p.setCoordinate(new Coordinate(entrance));
		    			notifyEvtThreadListener(EVENTTYPE.UPDATE_PERSON, p);
		    		}
	        	} catch (Exception ex) {
	        		ex.printStackTrace();
	        	}
	        }
	    } ).start();
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
			
			//if person in entrance, not infection possible
			if (!source.getCoordinate().equals(entrance)) {
				for (Person p : persons) {
					if (p == source || p.getMoving() || source.getMoving())
						continue;
					
					
					// check if p is within the radius
					double xDiff = newCord.getX() - p.getCord().getX();
					double yDiff = newCord.getY() - p.getCord().getY();
					
					double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
					
					
					// following rules: if following rules, distance will be doubled
					if (getRandNumber(100) < settings.getFollowingRules()) {
						distance += getRandNumber(50);
					}
					
					
					if (distance < SimulationConstants.getCurrentDangerousDistance()) {
						logMessage += " " + p.getPersonName() + ",";

						//rand number: if personen vaccinated, 50% less chance of infecting someone else
						
						if (source.getHealthStatus() == HEALTHSTATUS.INFECTIVE) {
							if (source.getVaccinated() && getRandNumber(2) == 1 || !source.getVaccinated()) {
								p.addInfectiveContacts();
								notifyEvtThreadListener(EVENTTYPE.UPDATE_HEALTH, p);
							}
						}				
						
						if (p.getHealthStatus() == HEALTHSTATUS.INFECTIVE) {
							if (p.getVaccinated() && getRandNumber(2) == 1 || !p.getVaccinated()) {
								source.addInfectiveContacts();
								notifyEvtThreadListener(EVENTTYPE.UPDATE_HEALTH, source);
							}
						}
						
						p.addContact();
						source.addContact();				
					}
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
	
	public void setSettings(Settings s) {
		this.settings = s;
	}
	private int getRandNumber(int upperBound){
		Random rand = new Random();
		return rand.nextInt(upperBound);
	}
 }
