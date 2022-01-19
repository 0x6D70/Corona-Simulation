package pkgMisc;

import java.util.EventObject;

public class EventThreadControllerObject extends EventObject {
	private static final long serialVersionUID = 1L;
	public enum EVENTTYPE {ALL_STARTED, ALL_STOPPED, ALL_GENERATED, ADD_LOG, CREATE_PERSON, UPDATE_PERSON, UPDATE_HEALTH, UNDEFINED};
	
	private final EVENTTYPE eventThreadMessageType;
	private String message;
	private IImageAnimation ia;
	
	public EventThreadControllerObject(Object source) {
		this(source, EVENTTYPE.UNDEFINED);
		this.message = "";
	}
	
	public EventThreadControllerObject(Object source, EVENTTYPE eventDBType) {
		super(source);
		this.eventThreadMessageType = eventDBType;
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
