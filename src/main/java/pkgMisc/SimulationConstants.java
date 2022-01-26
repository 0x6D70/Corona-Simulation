package pkgMisc;

public class SimulationConstants {
	public static final int MAX_LENGTH_ROOM = 600;
	public static final int MAX_WIDTH_ROOM = 500;
	public static final int MAX_DURATION_STAYING = 6;
	public static final int MIN_DURATION_STAYING = 3;
	public static final int MIN_CONTACTS_TILL_SUSPECTED = 1;
	public static final int MIN_CONTACTS_TILL_INFECTED = 1;
	public static final int MIN_CONTACTS_TILL_INFECTIVE = 1;
	public static final String logFile = "log.txt";
	
	public static final int X_COO_SHOP = 600; // pixel		add it to moving-pos of customer
	public static final int Y_COO_SHOP = 140; // pixel 		add it to moving-pos of customer
	public static final int IMG_HEIGHT = 30;
	public static final int IMG_WIDTH = 30;
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
	
	public static final int NUMBER_OF_LESSONS = 3;
	public static final int SLEEP_BETWEEN_ANIMATION = 5000;
	
	private static double currentDangerousDistance = 70;
	private static int percentInfectedAtStart = 0;
	
	public static int infective = 5;
	public static int followingRules = 75;
	public static int testsUsefull = 50;
	public static int vaccinated = 75;
	
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
		return currentDangerousDistance;
	}
	public static void setCurrentDangerousDistance(double currentDangerousDistance) {
		SimulationConstants.currentDangerousDistance = currentDangerousDistance;
	}
	public static int getPercentInfectedAtStart() {
		return percentInfectedAtStart;
	}
	public static void setPercentInfectedAtStart(int percentInfectedAtStart) {
		SimulationConstants.percentInfectedAtStart = percentInfectedAtStart;
	}
}
