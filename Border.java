public class Border {
	
	State borderingState;
	
	int distance = -1;
	
	
	public Border(State state) {
		borderingState = state;
	}
	
	public void setDistance(int dist) {
		distance = dist;
	}
	
	// Get the weight of the edge.
	public int getDistance() {
		return distance;
	}
	
	public State getState() {
		return borderingState;
	}
}
