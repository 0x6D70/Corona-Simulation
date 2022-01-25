package pkgSubjects;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javafx.scene.image.ImageView;
import pkgData.Coordinate;
import pkgData.DataPool;
import pkgMisc.IImageAnimation;
import pkgMisc.SimulationConstants;

public class Person implements IImageAnimation {
	public enum HEALTHSTATUS { INFECTIVE, INFECTED, SUSPECT, HEALTHY };
	public enum JOBSTATUS { TEACHER, PUPIL };
	
	private String name;
	private Coordinate cord = null;
	private Coordinate oldCord = null;
	private Coordinate mainPosition = null;

	private int contacts;
	private int infectiveContacts;
	private HEALTHSTATUS health = HEALTHSTATUS.HEALTHY;
	private JOBSTATUS jobStatus = JOBSTATUS.PUPIL;

	private ImageView imgView = null;
	private boolean isMoving = false;
	
	private static DataPool dp = null;
	
	private PropertyChangeSupport pcsCord = null;
	
	public Person() {
		if (dp == null)
			dp = new DataPool();
		
		this.name = dp.getNextName();
		this.cord = new Coordinate(0,0);
		this.pcsCord = new PropertyChangeSupport(this);
		this.contacts = 0;
		this.infectiveContacts = 0;
	}
	
	public int getInfectiveContacts() {
		return infectiveContacts;
	}

	public void addInfectiveContacts() {
		this.infectiveContacts++;
		
		if (this.health == HEALTHSTATUS.INFECTIVE) {
			// do nothing
		} else if (this.infectiveContacts >= SimulationConstants.MIN_CONTACTS_TILL_INFECTED + SimulationConstants.MIN_CONTACTS_TILL_SUSPECTED) {
			this.health = HEALTHSTATUS.INFECTED;
		} else if (this.infectiveContacts >= SimulationConstants.MIN_CONTACTS_TILL_SUSPECTED) {
			this.health = HEALTHSTATUS.SUSPECT;
		}
	}

	public static void resetDataPool() {
		if (dp != null)
			dp.reset();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.pcsCord.addPropertyChangeListener(pcl);
	}
	
	/*
	private void calculateNewPos() throws InterruptedException {		
		int posX = rnd.nextInt(SimulationConstants.MAX_LENGTH_ROOM);
		int posY = rnd.nextInt(SimulationConstants.MAX_WIDTH_ROOM);
		
		Coordinate newCord = new Coordinate(posX, posY);
		
		this.pcsCord.firePropertyChange(this.getPersonName(), cord, newCord);
	}
	
	private void simulateLingering() throws InterruptedException {
		long sleep = (long) (Math.random() * ((SimulationConstants.MAX_DURATION_STAYING - SimulationConstants.MIN_DURATION_STAYING) * 1000) + SimulationConstants.MIN_DURATION_STAYING * 1000);
		Thread.sleep(sleep);
	}
	*/
	
	public JOBSTATUS getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(JOBSTATUS jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	public Coordinate getMainPosition() {
		return mainPosition;
	}

	public void setMainPosition(Coordinate mainPosition) {
		this.mainPosition = mainPosition;
	}
	
	public Coordinate getCord() {
		Coordinate ret = new Coordinate(this.cord.getX(), this.cord.getY());
		return ret;
	}
	
	public Coordinate getOldCord() {
		Coordinate ret = new Coordinate(this.oldCord.getX(), this.oldCord.getY());
		return ret;
	}
	
	public String getPersonName() {
		return this.name;
	}

	public void setCoordinate(Coordinate newCord) {
		this.oldCord = new Coordinate(this.cord);
		this.cord = newCord;		
	}
	
	public void addContact() {
		this.contacts++;
	}
	
	public int getContacts() {
		return this.contacts;
	}

	@Override
	public String toString() {
		return this.getPersonName() + ", #contacts=" + this.getContacts() + "/" + this.getInfectiveContacts() + " => " + health.name();
	}

	@Override
	public ImageView getImageView() {
		return imgView;
	}

	@Override
	public void setImageView(ImageView iv) {
		this.imgView = iv;
	}

	@Override
	public HEALTHSTATUS getHealthStatus() {
		return this.health;
	}
	
	public void setHealthStatus(HEALTHSTATUS hs) {
		this.health = hs;
	}

	@Override
	public void setMoving(boolean moving) {
		this.isMoving = moving;		
	}

	@Override
	public boolean getMoving() {
		return this.isMoving;
	}

	@Override
	public void checkEnvironment() {
		System.out.println("not checking environment");
		//this.pcsCord.firePropertyChange(this.getPersonName(), null, null);
	}
}
