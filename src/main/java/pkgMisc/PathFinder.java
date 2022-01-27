package pkgMisc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pkgPathFinder.AStar;
import pkgPathFinder.Node;

import pkgData.Coordinate;
import pkgMisc.MapLoader.TILE_TYPES;

public class PathFinder {
	
	private AStar aStar;
	private TILE_TYPES[][] tileTypes;
	private HashMap<Integer, ArrayList<Coordinate>> moves = new HashMap<>();
	
	public PathFinder(TILE_TYPES[][] tileTypes) {
		this.tileTypes = tileTypes;
	}
	
	public ArrayList<Coordinate> calculatePath(Coordinate start, Coordinate end) {
		int identifier = start.hashCode() * 13 + end.hashCode();
		
		if (moves.containsKey(identifier)) {
			return moves.get(identifier);
		}
		
		aStar = new AStar(tileTypes.length, tileTypes[0].length,
				new Node(start.getY() / SimulationConstants.TILE_HEIGHT, start.getX() / SimulationConstants.TILE_WIDTH),
				new Node(end.getY() / SimulationConstants.TILE_HEIGHT, end.getX() / SimulationConstants.TILE_WIDTH)
		);
		
		for (int y = 0; y < SimulationConstants.TILE_COUNT_HEIGHT; y++) {
			for (int x = 0; x < SimulationConstants.TILE_COUNT_WIDTH; x++) {
				TILE_TYPES type = tileTypes[y][x];
				
				if (type == TILE_TYPES.WALL) {
					aStar.setBlocks( new int[][]{{y, x}});
				}
				
				if (type == TILE_TYPES.PUPIL_SEAT) {
					aStar.setBlocks( new int[][]{{y-1, x}});
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
		
		List<Node> path = aStar.findPath();
		
		ArrayList<Coordinate> ret = new ArrayList<>();
		
        for (Node node : path) {
        	System.out.println(node);
            ret.add(new Coordinate(node.getCol() * SimulationConstants.TILE_WIDTH, node.getRow() * SimulationConstants.TILE_HEIGHT));
        }
        
        moves.put(identifier, ret);
		
		return ret;
	}
}
