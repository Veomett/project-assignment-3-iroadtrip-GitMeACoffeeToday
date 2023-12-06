/**
 * The Border class is intended to serve as a means of storing data about a country's borders (all of them).
 * Each State object has a list of Border objects, which store the name of the bordering state and the distance between them.
 * This data can be accessed and partly modified throughout the program (modified only during the file reading and parsing stage).
 * @author michael
 */
public class Border {
	
	State borderingState;
	
	int distance = -1;
	
	/**
	 * A state object initially consists of just the bordering state, as one file, borders.txt, 
	 * only provides the names of the borders while the other, capdist.csv, provides the actual distance.
	 * @param state
	 */
	public Border(State state) {
		borderingState = state;
	}
	
	/**
	 * Sets the distance of the border object between the 'parent' State object's state and its associated Border object's state.
	 * @param dist
	 */
	public void setDistance(int dist) {
		distance = dist;
	}
	
	/**
	 * Returns the distance of the border. Will return -1 until data has been properly read into the border.
	 * @return
	 */
	public int getDistance() {
		return distance;
	}
	
	/**
	 * Returns the State object associated with the Border object.
	 * @return
	 */
	public State getState() {
		return borderingState;
	}
}
