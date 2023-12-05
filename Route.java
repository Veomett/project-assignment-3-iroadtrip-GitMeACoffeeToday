public class Route {
	
	int prevStateID;
	
	int currStateID;
	
	int distanceSoFar; // Total Weight

	
	public Route(int prevStateID, int currStateID, int currDistance){
		this.prevStateID = prevStateID;
		
		this.currStateID = currStateID;
		
		distanceSoFar += currDistance;
	}
	
	public int getPrevStateID() {
		return prevStateID;
	}
	public int getCurrStateID() {
		return currStateID;
	}
	
	
	public int getDistanceSoFar() {
		return distanceSoFar;
	}
	
	public void setPrevStateID(int stateID) {
		prevStateID = stateID;
	}
	public void setDistanceSoFar(int distance) {
		distanceSoFar = distance;
	}
}