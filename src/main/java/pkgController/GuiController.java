/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgController;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import pkgData.Coordinate;
import pkgMisc.CellFactoryListView;
import pkgMisc.EventThreadControllerListener;
import pkgMisc.EventThreadControllerObject;
import pkgMisc.SimulationConstants;
import pkgMisc.EventThreadControllerObject.EVENTTYPE;
import pkgMisc.IImageAnimation;
import pkgMisc.MapLoader;
import pkgMisc.PathFinder;
import pkgSubjects.Person;
import pkgSubjects.Person.HEALTHSTATUS;

/**
 *
 * @author schueler
 */
public class GuiController implements Initializable, EventThreadControllerListener {

	@FXML
	private Slider sliderTests;

	@FXML
	private Slider sliderVaccinated;

	@FXML
	private Slider sliderFollowingRules;

	@FXML
	private Slider sliderInfective;
	
	@FXML
	private Button btnGenerate;

	@FXML
	private Button btnStart;

	@FXML
	private ListView<String> lstView;

	@FXML
	private Pane simulationArea;
	
    @FXML
    private Label labelCountDay;
	    
    @FXML
    void onBtnClicked(ActionEvent event) {    	    	
    	try {
	    	if (event.getSource().equals(btnGenerate)) {
	    		ArrayList<Person> persons = tc.getPersonsSorted();
	    		
	    		for (Person p : persons) {
	    			p.getImageView().setVisible(false);
	    		}
	    		
	    		if (tc.getThread() != null) {
		    		tc.stopThread();
	    		}
	    		
	    		lstView.getItems().clear();
	    		
	    		tc = new ThreadController();
	    		tc.addEventThreadControllerListener(this);
	    		tc.generateThreads();
	    		
	    		obsStrings = FXCollections.observableArrayList();
	    		lstView.setCellFactory(new CellFactoryListView());
	    		lstView.setItems(obsStrings);

	    	} else if (event.getSource().equals(btnStart)) {
	    		
	    		tc.startThreads();
	    	}
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	try {
    		ml = new MapLoader();
    		ml.loadMap(simulationArea);
    		
    		pf = new PathFinder(ml.getTileTypes());
    		
    		imgHealthy = new Image(getClass().getResourceAsStream(SimulationConstants.FILE_PERSON_HEALTHY));
    		imgInfected = new Image(getClass().getResourceAsStream(SimulationConstants.FILE_PERSON_INFECTED));
    		imgInfective = new Image(getClass().getResourceAsStream(SimulationConstants.FILE_PERSON_INFECTIVE));
    		imgSuspect = new Image(getClass().getResourceAsStream(SimulationConstants.FILE_PERSON_SUSPECT));
    		
    		tc = new ThreadController();
    		tc.addEventThreadControllerListener(this);
    		
    		obsStrings = FXCollections.observableArrayList();
    		lstView.setCellFactory(new CellFactoryListView());
    		lstView.setItems(obsStrings);
    		
	        sliderFollowingRules.valueProperty().addListener((observable, oldValue, newValue) -> {
	            newFollowingRulesValue(newValue.intValue());
	        });
	        sliderInfective.valueProperty().addListener((observable, oldValue, newValue) -> {
	            newInfectiveValue(newValue.intValue());
	        });
	        sliderTests.valueProperty().addListener((observable, oldValue, newValue) -> {
	            newTestsValue(newValue.intValue());
	        });
	        sliderVaccinated.valueProperty().addListener((observable, oldValue, newValue) -> {
	            newVaccinatedValue(newValue.intValue());
	        });
	        
    	} catch (Exception ex) {
    		//this.lblMessage.setText(ex.getMessage());
    		ex.printStackTrace();
    	}
    }
    
    //Slider functions
    
    private void newFollowingRulesValue (int i) {
    	SimulationConstants.setFollowingrules(i);
    }
    private void newInfectiveValue (int i) {
    	SimulationConstants.setInfective(i);
    }
    private void newTestsValue (int i) {
    	SimulationConstants.setTestsusefull(i);
    }
    private void newVaccinatedValue (int i) {
    	SimulationConstants.setVaccinated(i);
    }
    
    
    /**
     * non gui attributes
     */
    
    private ThreadController tc = null;
    private ObservableList<String> obsStrings;
    private MapLoader ml = null;
    private PathFinder pf = null;
    
    private Image imgHealthy;
    private Image imgInfected;
    private Image imgInfective;
    private Image imgSuspect;

	@Override
	public void onEventThreadControllerChanged(EventThreadControllerObject event) {
		if (event.getEventThreadType() == EVENTTYPE.ADD_LOG) {
			Platform.runLater(() -> obsStrings.add(event.getMessage()));
		} else if (event.getEventThreadType() == EVENTTYPE.NEW_DAY) {
			Platform.runLater(() -> labelCountDay.setText(Integer.toString(event.getDay())));
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
			/*
			path.getElements().add(new MoveTo(ia.getOldCord().getX(), ia.getOldCord().getY()));
			path.getElements().add(new LineTo(ia.getCord().getX() + (SimulationConstants.TILE_WIDTH/2), ia.getCord().getY() + (SimulationConstants.TILE_HEIGHT / 2)));
			*/
			
			ArrayList<Coordinate> p = pf.calculatePath(ia.getOldCord(), ia.getCord());
			
			if (p.size() == 0) {
				System.out.println("fuck");
				Platform.exit();
				System.exit(0);
				return;
			}
			
			path.getElements().add(new MoveTo(p.get(0).getX(), p.get(0).getY()));
			p.remove(0);
			
			for (Coordinate c : p) {
				path.getElements().add(new LineTo(c.getX() + (SimulationConstants.TILE_WIDTH/2), c.getY() + (SimulationConstants.TILE_HEIGHT / 2)));
			}
			
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
		} else if (event.getEventThreadType() == EVENTTYPE.QUARANTINE_PERSON) {
			IImageAnimation ia = event.getAnimation();
			ImageView img = ia.getImageView();
			
			System.out.println(ia.getOldCord() + " => " + ia.getCord());
			
			Path path = new Path();
			//path.getElements().add(new MoveTo(ia.getOldCord().getX(), ia.getOldCord().getY()));
			//path.getElements().add(new LineTo(ia.getCord().getX(), ia.getCord().getY()));
			
			// TODO: code duplication
			
			ArrayList<Coordinate> p = pf.calculatePath(ia.getOldCord(), ia.getCord());
			
			path.getElements().add(new MoveTo(p.get(0).getX(), p.get(0).getY()));
			p.remove(0);
			
			for (Coordinate c : p) {
				path.getElements().add(new LineTo(c.getX() + (SimulationConstants.TILE_WIDTH/2), c.getY() + (SimulationConstants.TILE_HEIGHT / 2)));
			}

			// end code duplication
			
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
			        img.setVisible(false);
			    }
			});
			
			pathTransition.play();
			
			ia.setMoving(true);
		} else if (event.getEventThreadType() == EVENTTYPE.PERSON_OUT_OF_QUARANTINE){
			IImageAnimation ia = event.getAnimation();
			ImageView img = ia.getImageView();
			img.setImage(getImageFromHealth(ia.getHealthStatus()));
			img.setVisible(true);
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
