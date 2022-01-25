/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgController;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import pkgMisc.CellFactoryListView;
import pkgMisc.EventThreadControllerListener;
import pkgMisc.EventThreadControllerObject;
import pkgMisc.SimulationConstants;
import pkgMisc.EventThreadControllerObject.EVENTTYPE;
import pkgMisc.IImageAnimation;
import pkgMisc.MapLoader;
import pkgSubjects.Person.HEALTHSTATUS;

/**
 *
 * @author schueler
 */
public class GuiController implements Initializable, EventThreadControllerListener {

	@FXML
	private Button btnGenerate;

	@FXML
	private Button btnStart;

	@FXML
	private Button btnStop;

	@FXML
	private Button btnLog;

	@FXML
	private ListView<String> lstView;

	@FXML
	private Pane simulationArea;
	    
    @FXML
    void onBtnClicked(ActionEvent event) {    	    	
    	try {
	    	if (event.getSource().equals(btnGenerate)) {	    		    			    		
	    		tc.generateThreads();
	    	} else if (event.getSource().equals(btnStart)) {
	    		tc.startThreads();
	    		//this.startTime = System.currentTimeMillis();
	    	} else if (event.getSource().equals(btnStop)) {
	    		//tc.stopThreads();
	    		
	    		//long endTime = System.currentTimeMillis();
	    		
	    		//Platform.runLater(() -> this.lblMessage.setText("simulation lasted " + (endTime - this.startTime) + " msec."));
	    	}
    	} catch (Exception ex) {
    		//this.lblMessage.setText(ex.getMessage());
    		ex.printStackTrace();
    	}
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	try {
    		MapLoader ml = new MapLoader();
    		ml.loadMap(simulationArea);
    		
    		imgHealthy = new Image(getClass().getResourceAsStream(SimulationConstants.FILE_PERSON_HEALTHY));
    		imgInfected = new Image(getClass().getResourceAsStream(SimulationConstants.FILE_PERSON_INFECTED));
    		imgInfective = new Image(getClass().getResourceAsStream(SimulationConstants.FILE_PERSON_INFECTIVE));
    		imgSuspect = new Image(getClass().getResourceAsStream(SimulationConstants.FILE_PERSON_SUSPECT));
    		
    		tc = new ThreadController();
    		tc.addEventThreadControllerListener(this);
    		
    		obsStrings = FXCollections.observableArrayList();
    		lstView.setCellFactory(new CellFactoryListView());
    		lstView.setItems(obsStrings);
    	} catch (Exception ex) {
    		//this.lblMessage.setText(ex.getMessage());
    		ex.printStackTrace();
    	}
    }
    /**
     * non gui attributes
     */
    
    private ThreadController tc = null;
    private ObservableList<String> obsStrings;
    
    private Image imgHealthy;
    private Image imgInfected;
    private Image imgInfective;
    private Image imgSuspect;

	@Override
	public void onEventThreadControllerChanged(EventThreadControllerObject event) {
		if (event.getEventThreadType() == EVENTTYPE.ADD_LOG) {
			Platform.runLater(() -> obsStrings.add(event.getMessage()));
		} else if (event.getEventThreadType() == EVENTTYPE.CREATE_PERSON) {
			IImageAnimation ia = event.getAnimation();
			ImageView img = new ImageView();
			
			img.setFitHeight(SimulationConstants.IMG_HEIGHT);
			img.setFitWidth(SimulationConstants.IMG_WIDTH);
			img.setX(ia.getCord().getX());
			img.setY(ia.getCord().getY());
			img.setImage(getImageFromHealth(ia.getHealthStatus()));
			
			simulationArea.getChildren().add(img);
			
			ia.setImageView(img);
			
		} else if (event.getEventThreadType() == EVENTTYPE.UPDATE_PERSON) {
			IImageAnimation ia = event.getAnimation();
			ImageView img = ia.getImageView();
			
			System.out.println(ia.getOldCord() + " => " + ia.getCord());
			
			Path path = new Path();
			path.getElements().add(new MoveTo(ia.getOldCord().getX(), ia.getOldCord().getY()));
			path.getElements().add(new LineTo(ia.getCord().getX(), ia.getCord().getY()));

			PathTransition pathTransition = new PathTransition();
			pathTransition.setDuration(Duration.millis(SimulationConstants.ANIMATION_DURATION * 1000));
			pathTransition.setPath(path);
			pathTransition.setNode(img);
			

			pathTransition.setCycleCount(1);
			pathTransition.setAutoReverse(false);
			
			ia.setMoving(true);
			
			pathTransition.setOnFinished(new EventHandler<ActionEvent>() {
			    @Override
			    public void handle(ActionEvent event) {
			        ia.setMoving(false);
			        ia.checkEnvironment();
			    }
			});
			
			pathTransition.play();
			
			ia.setMoving(true);
			
		} else if (event.getEventThreadType() == EVENTTYPE.UPDATE_HEALTH) {
			IImageAnimation ia = event.getAnimation();
			ImageView img = ia.getImageView();
			img.setImage(getImageFromHealth(ia.getHealthStatus()));
		} else {
			//Platform.runLater(() -> this.lblMessage.setText("Threads: " + event.getEventThreadType().name()));	
		}
	}
	
	private Image getImageFromHealth(HEALTHSTATUS status) {
		Image ret = null;
		
		if (status  == HEALTHSTATUS.HEALTHY) {
			ret = imgHealthy;
		} else if (status == HEALTHSTATUS.INFECTED) {
			ret = imgInfected;
		} else if (status == HEALTHSTATUS.INFECTIVE) {
			ret = imgInfective;
		} else if (status == HEALTHSTATUS.SUSPECT) {
			ret = imgSuspect;
		}		
		
		return ret;
	}
}
