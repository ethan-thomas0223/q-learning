package robosim.core;

public interface Controller {
	void control(Simulator sim);

	default String getStatus() {return "";}
}
