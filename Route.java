/**
 * A route is simply an 'edge' between two nodes (states / countries), which, as its defining quality, 
 * carries the accumlative weight of the routes leading up to it prior. The pathfinding algorithm in this program
 * relies on a collection of routes to determine the fastest path from source to destination.
 * @author michael
 */
public class Route {
	
	String prevStateCode;
	
	String currStateCode;
	
	int distanceSoFar; // Total Weight

	/**
	 * Creates a new Route object with the code of the previous state, the code of the current state, and the distanceSoFar for the particular route ( edge weight ).
	 * @param prevStateCode code (state code) of the previous state.
	 * @param currStateCode code (state code) of the current state.
	 * @param currDistance The distance so far (accumulative), or edge weight.
	 */
	public Route(String prevStateCode, String currStateCode, int currDistance){
		this.prevStateCode = prevStateCode;
		
		this.currStateCode = currStateCode;
		
		distanceSoFar += currDistance;
	}
	
	/**
	 * Returns the previous state ID of this route.
	 * @return
	 */
	public String getPrevStateCode() {
		return prevStateCode;
	}
	
	/**
	 * Returns the current state code of this route.
	 * @return
	 */
	public String getCurrStateCode() {
		return currStateCode;
	}
	
	/**
	 * Returns the distance so far (accumulative), or edge weight
	 * @return
	 */
	public int getDistanceSoFar() {
		return distanceSoFar;
	}
	
	/**
	 * Sets the Code of the previous state for this route object.
	 * @param stateID
	 */
	public void setPrevStateCode(String newStateCode) {
		prevStateCode = newStateCode;
	}
	
	/**
	 * Sets the the distance so far (accumulative), or edge weight. 
	 * @param distance
	 */
	public void setDistanceSoFar(int distance) {
		distanceSoFar = distance;
	}
}

