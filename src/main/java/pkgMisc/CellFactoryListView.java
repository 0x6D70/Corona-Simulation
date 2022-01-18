package pkgMisc;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class CellFactoryListView implements Callback<ListView<String>, ListCell<String>> {
	
	private final String DEFAULT_CONTROL_INNER_BACKGROUND = "derive(-fx-base,80%)";
	private final String HIGHLIGHTED_CONTROL_INNER_BACKGROUND = "derive(palegreen,50%)";

	@Override
	public ListCell<String> call(ListView<String> param) {
		return new ListCell<>() {
			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
		
				if (item == null || empty) {
					setText(null);
					setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");
				} else {
					setText(item);
					if (item.contains(",")) {
						setStyle("-fx-control-inner-background: " + HIGHLIGHTED_CONTROL_INNER_BACKGROUND + ";");
					} else {
						setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");
					}
				}
			}
		};
	}
}

