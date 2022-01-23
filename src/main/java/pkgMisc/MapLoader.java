package pkgMisc;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class MapLoader {
	
	public enum TILE_TYPES {
		FLOOR,
		WALL,
		TOILET,
		PUPIL_SEAT,
		TEACHER_SEAT_CLASS,
		TEACHER_SEAT_CHAMBER,
		ENTRANCE
	}
	
	TILE_TYPES[][] tileTypes = new TILE_TYPES[SimulationConstants.TILE_COUNT_HEIGHT][SimulationConstants.TILE_COUNT_WIDTH];
	
	public TILE_TYPES[][] getTileTypes() {
		return tileTypes;
	}

	public void loadMap(Pane pane) {
		
		long start = System.nanoTime();
		
		pane.getChildren().clear();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			HashMap<Integer, Image> images = new HashMap<>();
		
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			// load tile set
	        Document doc = db.parse(getClass().getResourceAsStream(SimulationConstants.FILE_TILE_SET));
	        doc.getDocumentElement().normalize();
	        
	        NodeList list = doc.getElementsByTagName("tile");
	        NodeList listSources = doc.getElementsByTagName("image");
	        
	        for (int i = 0; i < list.getLength(); i++) {
	        	Node node = list.item(i);
	        	
	        	int id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
	        	
	        	String sourceFile = listSources.item(i).getAttributes().getNamedItem("source").getNodeValue();
	        	
	        	images.put(id, new Image(getClass().getResourceAsStream(SimulationConstants.DIR_MAP_IMG_FILES + sourceFile)));
	        }
	        
	        // load map file
	        
	        doc = db.parse(getClass().getResourceAsStream(SimulationConstants.FILE_MAP));
	        doc.getDocumentElement().normalize();
	        
	        list = doc.getElementsByTagName("data");
	        
	        for (int i = 0; i < list.getLength(); i++) {	        	
	        	Node node = list.item(i);
	        	
	        	String content = node.getTextContent();
	        	
	        	String[] lines = content.trim().split("\n");
	        	
	        	for (int y = 0; y < lines.length; y++) {	        		
	        		String[] numbers = lines[y].split(",");
	        		
	        		for (int x = 0; x < numbers.length; x++) {	        			
	        			int number = Integer.parseInt(numbers[x]);
	        			number -= (i + 1); // subtract layer id (starts at 1)
	        			
	        			if (number != -1) {
	        				ImageView img = new ImageView();
	        				
	        				img.setFitHeight(SimulationConstants.TILE_HEIGHT);
	        				img.setFitWidth(SimulationConstants.TILE_WIDTH);
	        				img.setX(x * SimulationConstants.TILE_WIDTH);
	        				img.setY(y * SimulationConstants.TILE_HEIGHT);
	        				img.setImage(images.get(number));
	        				
	        				pane.getChildren().add(img);
	        				
	        				tileTypes[y][x] = getTileType(number);
	        			}
	        		}
	        	}
	        }        
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		long end = System.nanoTime();
		
		System.out.println("Map loading took " + (end - start) / 1000 / 1000 + " ms");
	}
	
	private TILE_TYPES getTileType(int number) throws Exception {
		TILE_TYPES ret;
		
		if (number == SimulationConstants.TILE_ID_FLOOR) {
			ret = TILE_TYPES.FLOOR;
		} else if (number == SimulationConstants.TILE_ID_WALL) {
			ret = TILE_TYPES.WALL;
		} else if (number == SimulationConstants.TILE_ID_TOILET) {
			ret = TILE_TYPES.TOILET;
		} else if (number == SimulationConstants.TILE_ID_PUPIL_SEAT) {
			ret = TILE_TYPES.PUPIL_SEAT;
		} else if (number == SimulationConstants.TILE_ID_TEACHER_SEAT_CLASS) {
			ret = TILE_TYPES.TEACHER_SEAT_CLASS;
		} else if (number == SimulationConstants.TILE_ID_TEACHER_SEAT_CHAMBER) {
			ret = TILE_TYPES.TEACHER_SEAT_CHAMBER;
		} else if (number == SimulationConstants.TILE_ID_ENTRANCE) {
			ret = TILE_TYPES.ENTRANCE;
		} else {
			throw new Exception("Unknown Tile ID");
		}
		
		return ret;
	}
}
