package pkgData;

public class Settings {
	
	private int infective;
	private int followingRules;
	private int testsUsefull;
	private int vaccinated;
	
	public Settings(int infective, int followingRules, int testsUsefull, int vaccinated) {
		super();
		this.infective = infective;
		this.followingRules = followingRules;
		this.testsUsefull = testsUsefull;
		this.vaccinated = vaccinated;
	}
	
	public int getInfective() {
		return infective;
	}
	public void setInfective(int infective) {
		this.infective = infective;
	}
	public int getFollowingRules() {
		return followingRules;
	}
	public void setFollowingRules(int followingRules) {
		this.followingRules = followingRules;
	}
	public int getTestsUsefull() {
		return testsUsefull;
	}
	public void setTestsUsefull(int testsUsefull) {
		this.testsUsefull = testsUsefull;
	}
	public int getVaccinated() {
		return vaccinated;
	}
	public void setVaccinated(int vaccinated) {
		this.vaccinated = vaccinated;
	}
}
