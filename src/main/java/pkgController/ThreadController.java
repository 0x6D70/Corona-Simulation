package pkgController;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import pkgData.Coordinate;
import pkgMisc.SimulationConstants;
import pkgMisc.EventThreadControllerListener;
import pkgMisc.EventThreadControllerObject;
import pkgMisc.MapLoader;
import pkgMisc.EventThreadControllerObject.EVENTTYPE;
import pkgMisc.MapLoader.TILE_TYPES;
import pkgMisc.PersonComparator;
import pkgSubjects.Person;
import pkgSubjects.Person.HEALTHSTATUS;
import pkgSubjects.Person.JOBSTATUS;

public class ThreadController implements PropertyChangeListener {
	private ArrayList<Person> persons = new ArrayList<>();
	private ArrayList<Person> personsInQuarantine = new ArrayList<>();
	private ArrayList<EventThreadControllerListener> evtThreadListener = new ArrayList<>();
	private ArrayList<Coordinate> teacherSeats = new ArrayList<>();
	
	Thread thread = null;
	
	private Coordinate entrance = null;
		
	public ThreadController() throws IOException {
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
				
		int randnumber;
		
		for (Person p : persons) {
			randnumber = getRandNumber(100);
			
			if (randnumber <= SimulationConstants.getVaccinated()) {
				p.setVaccinated(true);
			}
			
			randnumber = getRandNumber(100);
			
			if (randnumber <= SimulationConstants.getInfective()) {
				p.setHealthStatus(HEALTHSTATUS.INFECTIVE);
			}			
			
			p.setCoordinate(entrance);
			notifyEvtThreadListener(EVENTTYPE.CREATE_PERSON, p);
		}		
		
		notifyEvtThreadListener(EventThreadControllerObject.EVENTTYPE.ALL_GENERATED);
	}
	
	public void startThreads() throws Exception {
		
		// sleep without blocking the thread
		thread = new Thread( new Runnable() {
	        public void run()  {	
	        	//Start new Day automatically
	        	try {
	        		int countDays = 0;	
		    		while(true) {
		    			
		    			countDays++;
		    			notifyEvtThreadListener(EVENTTYPE.NEW_DAY, countDays);				    			
		    			
			            //Get People out of Quarantine
			            for (int i = 0; i < personsInQuarantine.size(); i++) {
			            	Person p = personsInQuarantine.get(i);
			            	if (countDays - p.getQuarantineDay() >= SimulationConstants.DAYS_IN_QUARANTINE) {
			            		p.setHealthStatus(HEALTHSTATUS.HEALTHY);
			            		persons.add(p);
			            		personsInQuarantine.remove(i);
			            		notifyEvtThreadListener(EVENTTYPE.PERSON_OUT_OF_QUARANTINE, p);
			            	}
			            }

		    			// do movements to Main Position
		    			for (Person p : persons) {
		    				p.setCoordinate(new Coordinate(p.getMainPosition()));
		    				//new Infected Persons every day (1/10 of infected people at start)
		    				if (getRandNumber(100) <= SimulationConstants.AMOUNT_NEW_INFECTED) {
		    					p.setHealthStatus(HEALTHSTATUS.INFECTIVE);
		    					notifyEvtThreadListener(EVENTTYPE.UPDATE_HEALTH, p);
		    				}
		    				//Infected Persons getting Infective
		    				if (getRandNumber(100) <= SimulationConstants.INFECTED_PEOPLE_BECOME_INFECTIVE && p.getHealthStatus() == HEALTHSTATUS.INFECTED) {
		    					p.setHealthStatus(HEALTHSTATUS.INFECTIVE);
		    					notifyEvtThreadListener(EVENTTYPE.UPDATE_HEALTH, p);
		    				}
		    				
		    				notifyEvtThreadListener(EVENTTYPE.UPDATE_PERSON, p);
		    			}		
		    			
			            Thread.sleep(SimulationConstants.SLEEP_BETWEEN_ANIMATION); // 2s Animation time -> pupil sit for
			            
			            //Implementation tests
			            int randNumber;
			            
			            for (int i = 0; i < persons.size(); i++) {
			            	randNumber = getRandNumber(100);
			            	Person p = persons.get(i);
			            	if (p.getHealthStatus() == HEALTHSTATUS.INFECTIVE && randNumber < SimulationConstants.getTestsUsefull()) {
			            		p.setCoordinate(entrance);
			            		p.setQuarantineDay(countDays);
			            		personsInQuarantine.add(p);
			            		persons.remove(i);
			            		i--;
			            		notifyEvtThreadListener(EVENTTYPE.QUARANTINE_PERSON, p);
			            	}
			            }
			            
			            //Generall Movements
			            ArrayList<Person> teachers = new ArrayList<Person>();
			            
			            for (Person p : persons) {
			            	if (p.getJobStatus() == JOBSTATUS.TEACHER) {
			            		teachers.add(p);
			            	}
			            }
			            
			            for (int i = 0; i < SimulationConstants.NUMBER_OF_LESSONS; i++) {	
			            	
			            	// Teachers
			            	int amountTeachers = teachers.size() >= teacherSeats.size() ? teacherSeats.size() : teachers.size();
			            	for (int u = 0; u < amountTeachers; u++) {		            	
			            		do{
			            			randNumber = getRandNumber(teachers.size());
			            		}while (!teachers.get(randNumber).getCoordinate().equals(teachers.get(randNumber).getMainPosition()));
			            		Person teacher = teachers.get(randNumber);
			            		teacher.setCoordinate(teacherSeats.get(u));
			            		notifyEvtThreadListener(EVENTTYPE.UPDATE_PERSON, teacher);
			            		teacher.checkEnvironment();
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
			            
			            Thread.sleep(SimulationConstants.SLEEP_BETWEEN_DAYS); //Sleep two seconds between days
	        		}
	        	} catch (Exception ex) {
	        		ex.printStackTrace();
	        	}
	        }
	    });
		
		thread.start();
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
			
			//if person in entrance, no infection possible
			if (!source.getCoordinate().equals(entrance)) {
				for (Person p : persons) {
					if (p == source || p.getMoving() || source.getMoving())
						continue;
					
					
					// check if p is within the radius
					double xDiff = newCord.getX() - p.getCord().getX();
					double yDiff = newCord.getY() - p.getCord().getY();
					
					double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
					
					
					// following rules: if following rules, distance will be doubled
					if (getRandNumber(100) < SimulationConstants.getFollowingRules()) {
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
	
	private void notifyEvtThreadListener(EventThreadControllerObject.EVENTTYPE type, int day ) {
		for (EventThreadControllerListener listener : this.evtThreadListener) {
			listener.onEventThreadControllerChanged(new EventThreadControllerObject(this, type, day));
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
	
	public void stopThread() {
		thread.interrupt();
	}
	
	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	Random rand = new Random();
	
	private int getRandNumber(int upperBound){
		return rand.nextInt(upperBound);
	}
 }
