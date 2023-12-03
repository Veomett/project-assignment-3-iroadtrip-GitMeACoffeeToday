package proj3;

public class Route {
	
	int prevStateID;
	
	int currStateID;
	
	int distanceSoFar; // Total Weight

	
	public Route(int prevStateID, int currStateID, int currDistance){
		this.prevStateID = prevStateID;
		
		this.currStateID = currStateID;
		
		distanceSoFar += currDistance;
	}
}

