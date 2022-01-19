package pkgMisc;

import javafx.scene.image.ImageView;
import pkgData.Coordinate;
import pkgSubjects.Person.HEALTHSTATUS;

public interface IImageAnimation {
	ImageView getImageView();
	void setImageView(ImageView iv);
	Coordinate getOldCord();
	Coordinate getCord();
	HEALTHSTATUS getHealthStatus();
	void setMoving(boolean moving);
	boolean getMoving();
	void checkEnvironment();
}
