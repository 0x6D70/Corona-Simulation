package pkgMisc;

import java.util.ArrayList;
import java.util.List;

import pkgPathFinder.AStar;
import pkgPathFinder.Node;

import pkgData.Coordinate;
import pkgMisc.MapLoader.TILE_TYPES;

public class PathFinder {
	
	private AStar aStar;
	private TILE_TYPES[][] tileTypes;
	
	public PathFinder(TILE_TYPES[][] tileTypes) {
		this.tileTypes = tileTypes;
		/*
		aStar = new AStar(tileTypes.length, tileTypes[0].length, new Node(0,0), new Node(0,0));
		
		for (int y = 0; y < SimulationConstants.TILE_COUNT_HEIGHT; y++) {
			for (int x = 0; x < SimulationConstants.TILE_COUNT_WIDTH; x++) {
				TILE_TYPES type = tileTypes[y][x];
				
				if (type != TILE_TYPES.FLOOR) {
					//aStar.setBlocks( new int[][]{{y, x}});
				}
			}
		}
		*/
	}
	
	public ArrayList<Coordinate> calculatePath(Coordinate start, Coordinate end) {
		aStar = new AStar(tileTypes.length, tileTypes[0].length, new Node(start.getY() / 8, start.getX() / 8), new Node(end.getY() / 8, end.getX() / 8));
		
		for (int y = 0; y < SimulationConstants.TILE_COUNT_HEIGHT; y++) {
			for (int x = 0; x < SimulationConstants.TILE_COUNT_WIDTH; x++) {
				TILE_TYPES type = tileTypes[y][x];
				
				if (type == TILE_TYPES.WALL) {
					aStar.setBlocks( new int[][]{{y, x}}); // TODO: block if next to wall
				}
				
				if (!(y + 1 == tileTypes[0].length) && y > 0 && x > 0 && !(x +1 == tileTypes.length)) {
					try {
						if (tileTypes[y+1][x] == TILE_TYPES.WALL ||
								tileTypes[y-1][x] == TILE_TYPES.WALL ||
								tileTypes[y][x+1] == TILE_TYPES.WALL ||
								tileTypes[y][x-1] == TILE_TYPES.WALL) {
							aStar.setBlocks( new int[][]{{y, x}}); 
						}
					} catch (Exception ex) {}
				}	
			}
		}
		
		//aStar.setInitialNode(new Node(start.getY() / 8, start.getX() / 8));
		//aStar.setFinalNode(new Node(end.getY() / 8, end.getX() / 8));
		
		List<Node> path = aStar.findPath();
		
		ArrayList<Coordinate> ret = new ArrayList<>();
		
        for (Node node : path) {
        	System.out.println(node);
            ret.add(new Coordinate(node.getCol() * SimulationConstants.TILE_WIDTH, node.getRow() * SimulationConstants.TILE_HEIGHT));
        }
		
		return ret;
	}
}
