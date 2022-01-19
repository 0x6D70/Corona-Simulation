package pkgMisc;

import java.util.EventListener;

public interface EventThreadControllerListener extends EventListener {
	void onEventThreadControllerChanged(EventThreadControllerObject event);
}
