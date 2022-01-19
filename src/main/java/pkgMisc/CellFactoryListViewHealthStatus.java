package pkgMisc;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import pkgSubjects.Person;
import pkgSubjects.Person.HEALTHSTATUS;

public class CellFactoryListViewHealthStatus implements Callback<ListView<Person>, ListCell<Person>> {
	
	private final String DEFAULT_CONTROL_INNER_BACKGROUND = "derive(-fx-base,80%)";
	private final String RED_CONTROL_INNER_BACKGROUND = "derive(red,50%)";
	private final String ORANGE_CONTROL_INNER_BACKGROUND = "derive(orange,50%)";
	private final String YELLOW_CONTROL_INNER_BACKGROUND = "derive(yellow,50%)";
	private final String GREEN_CONTROL_INNER_BACKGROUND = "derive(palegreen,50%)";

	@Override
	public ListCell<Person> call(ListView<Person> param) {
		return new ListCell<>() {
			@Override
			public void updateItem(Person item, boolean empty) {
				super.updateItem(item, empty);
		
				if (item == null || empty) {
					setText(null);
					setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");
				} else {
					setText(item.toString());
					if (item.getHealthStatus() == HEALTHSTATUS.INFECTIVE) {
						setStyle("-fx-control-inner-background: " + RED_CONTROL_INNER_BACKGROUND + ";");
					} else if (item.getHealthStatus() == HEALTHSTATUS.INFECTED) {
						setStyle("-fx-control-inner-background: " + ORANGE_CONTROL_INNER_BACKGROUND + ";");
					} else if (item.getHealthStatus() == HEALTHSTATUS.SUSPECT) {
						setStyle("-fx-control-inner-background: " + YELLOW_CONTROL_INNER_BACKGROUND + ";");
					} else if (item.getHealthStatus() == HEALTHSTATUS.HEALTHY) {
						setStyle("-fx-control-inner-background: " + GREEN_CONTROL_INNER_BACKGROUND + ";");
					} else {
						setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");
					}
				}
			}
		};
	}
}

