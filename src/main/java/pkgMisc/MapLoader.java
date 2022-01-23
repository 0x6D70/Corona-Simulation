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
}
