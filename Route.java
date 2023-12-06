/**
 * A route is simply an 'edge' between two nodes (states / countries), which, as its defining quality, 
 * carries the accumlative weight of the routes leading up to it prior. The pathfinding algorithm in this program
 * relies on a collection of routes to determine the fastest path from source to destination.
 * @author michael
 */
public class Route {
	
	int prevStateID;
	
	int currStateID;
	
	int distanceSoFar; // Total Weight

	/**
	 * Creates a new Route object with the ID of the previous state, the ID of the current state, and the distanceSoFar for the particular route ( edge weight ).
	 * @param prevStateID ID (state number) of the previous state.
	 * @param currStateID ID (state number) of the current state.
	 * @param currDistance The distance so far (accumulative), or edge weight.
	 */
	public Route(int prevStateID, int currStateID, int currDistance){
		this.prevStateID = prevStateID;
		
		this.currStateID = currStateID;
		
		distanceSoFar += currDistance;
	}
	
	/**
	 * Returns the previous state ID of this route.
	 * @return
	 */
	public int getPrevStateID() {
		return prevStateID;
	}
	
	/**
	 * Returns the current state ID of this route.
	 * @return
	 */
	public int getCurrStateID() {
		return currStateID;
	}
	
	/**
	 * Returns the distance so far (accumulative), or edge weight
	 * @return
	 */
	public int getDistanceSoFar() {
		return distanceSoFar;
	}
	
	/**
	 * Sets the ID of the previous state for this route object.
	 * @param stateID
	 */
	public void setPrevStateID(int stateID) {
		prevStateID = stateID;
	}
	
	/**
	 * Sets the the distance so far (accumulative), or edge weight. 
	 * @param distance
	 */
	public void setDistanceSoFar(int distance) {
		distanceSoFar = distance;
	}
}

