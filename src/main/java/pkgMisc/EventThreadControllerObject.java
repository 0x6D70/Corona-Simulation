package pkgMisc;

import java.util.EventObject;

public class EventThreadControllerObject extends EventObject {
	private static final long serialVersionUID = 1L;
	public enum EVENTTYPE {ALL_STARTED, ALL_STOPPED, ALL_GENERATED, ADD_LOG, CREATE_PERSON, UPDATE_PERSON, UPDATE_HEALTH, QUARANTINE_PERSON, PERSON_OUT_OF_QUARANTINE, NEW_DAY, UNDEFINED};
	
	private final EVENTTYPE eventThreadMessageType;
	private String message;
	private IImageAnimation ia;
	private int day;
	
	public EventThreadControllerObject(Object source) {
		this(source, EVENTTYPE.UNDEFINED);
		this.message = "";
	}
	
	public EventThreadControllerObject(Object source, EVENTTYPE eventDBType) {
		super(source);
		this.eventThreadMessageType = eventDBType;
	}
	
	public EventThreadControllerObject(Object source, EVENTTYPE eventDBType, int newday) {
		super(source);
		this.eventThreadMessageType = eventDBType;
		this.day = newday;
	}
	
	public int getDay() {
		return day;
	}

	public EventThreadControllerObject(Object source, EVENTTYPE eventDBType, String message) {
		super(source);
		this.eventThreadMessageType = eventDBType;
		this.message = message;
	}
	
	public EventThreadControllerObject(Object source, EVENTTYPE eventDBType, IImageAnimation ia) {
		super(source);
		this.eventThreadMessageType = eventDBType;
		this.ia = ia;
	}

	public EVENTTYPE getEventThreadType() {
		return eventThreadMessageType;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public IImageAnimation getAnimation() {
		return this.ia;
	}
}
