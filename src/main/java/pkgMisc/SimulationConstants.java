package pkgMisc;

public class SimulationConstants {
	
	public static final int X_COO_SHOP = 600; // pixel		add it to moving-pos of customer
	public static final int Y_COO_SHOP = 140; // pixel 		add it to moving-pos of customer
	public static final int IMG_HEIGHT = 24;
	public static final int IMG_WIDTH = 24;
	public static final long ANIMATION_DURATION = 2;
	
	public static final String FILE_PERSON_HEALTHY = "/images/healthy.jpg";
	public static final String FILE_PERSON_INFECTED = "/images/infected.jpg";
	public static final String FILE_PERSON_INFECTIVE = "/images/infective.jpg";
	public static final String FILE_PERSON_SUSPECT = "/images/suspect.jpg";
	
	public static final String FILE_MAP = "/map/map.tmx";
	public static final String FILE_TILE_SET = "/map/tileset.tsx";
	public static final String DIR_MAP_IMG_FILES = "/map/";
	
	public static final int TILE_HEIGHT = 8;
	public static final int TILE_WIDTH = 8;
	
	public static final int TILE_COUNT_WIDTH = 140;
	public static final int TILE_COUNT_HEIGHT = 83;
	
	public static final int TILE_ID_FLOOR = 0;
	public static final int TILE_ID_WALL = 1;
	public static final int TILE_ID_TOILET = 2;
	public static final int TILE_ID_PUPIL_SEAT = 4;
	public static final int TILE_ID_TEACHER_SEAT_CLASS = 5;
	public static final int TILE_ID_TEACHER_SEAT_CHAMBER = 6;
	public static final int TILE_ID_ENTRANCE = 7;
	
	public static double DangerousDistance = 70;
	
	//----------------------Personal Settings-----------------------//
	private static int infective = 10;
	private static int followingRules = 50;
	private static int testsUsefull = 50;
	private static int vaccinated = 60;
	//----------------------Static settings-------------------------//
	public static final int AMOUNT_NEW_INFECTED = infective / 10;
	public static final int DAYS_IN_QUARANTINE = 5;
	public static final int INFECTED_PEOPLE_BECOME_INFECTIVE = 30;
	public static final int NUMBER_OF_LESSONS = 1;
	
	public static final int MIN_CONTACTS_TILL_SUSPECTED = 1;
	public static final int MIN_CONTACTS_TILL_INFECTED = 1;
	public static final int MIN_CONTACTS_TILL_INFECTIVE = 1;
	
	public static final int SLEEP_BETWEEN_ANIMATION = 5000;
	//--------------------------------------------------------------//
	
	
	public static void setInfective(int i) {
		infective = i;
	}
	public static void setFollowingrules(int i) {
		followingRules = i;
	}
	public static void setTestsusefull(int i) {
		testsUsefull = i;
	}
	public static void setVaccinated(int i) {
		vaccinated = i;
	}
	public static int getFollowingRules() {
		return followingRules;
	}
	public static int getTestsUsefull() {
		return testsUsefull;
	}
	public static int getInfective() {
		return infective;
	}
	public static int getVaccinated() {
		return vaccinated;
	}
	
	public static double getCurrentDangerousDistance() {
		return DangerousDistance;
	}
	public static void setCurrentDangerousDistance(double currentDangerousDistance) {
		SimulationConstants.DangerousDistance = currentDangerousDistance;
	}
}
