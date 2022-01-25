package pkgData;

public class Coordinate {
	int x;
	int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Coordinate(Coordinate c) {
		this.x = c.x;
		this.y = c.y;
	}
	
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public Coordinate getCoordinate() {
		return new Coordinate(x, y);
	}

	@Override
	public String toString() {
		//return "Coordinate [x=" + x + ", y=" + y + "]";
		return "(" + x + "/" + y + ")";
	}
	
	public String toStringShort() {
		return x + "/" + y;
	}
}
