package pkgData;

public class DataPool {
	String[] names = {
			"Hugo",
			"Eva",
			"Andreas",
			"Herbert",
			"Stefan",
			"Markus",
			"Luca",
			"David",
			"Leon",
			"Ricardo",
			"Noah",
			"Jakob",
			"Marcel",
			"Tobias",
			"Adrian",
			"Theo",
			"Raphael",
			"Daniel",
			"Elias",
			"Tim",
			"Mathias",
			"Elisabeth",
			"Lisa",
			"Hildegard",
			"Maria",
			"Hannah",
			"Leonie",
			"Katharina",
			"Yvonne",
			"Stefan",
			"Sfeannie",
			"Nicole"
	};
	
	int index;
	
	public DataPool() {
		this.index = 0;
	}
	
	public String getNextName() {
		String ret;
		if (index >= names.length) {
			ret = "Peter#" + index;
		} else {
			ret = names[index];
		}
		
		index++;
		
		return ret;
	}
	
	public void reset() {
		this.index = 0;
	}
}
