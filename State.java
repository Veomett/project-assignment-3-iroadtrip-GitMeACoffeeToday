import java.util.ArrayList;

public class State {
	
	int id;
	
	String code;
	
	ArrayList<String> namesList = new ArrayList<String>(); // Index 0 is the 'official name'. Alias'
	
	ArrayList<Border> borders = new ArrayList<Border>();// Contains all borders that border this node.
	
	
	public State(int id, String code) {
		this.id = id;
		this.code = code;
		
	}
	
	// Getters / Setters
	/**
	 * Returns the State ID (number) of this State object.
	 * @return State id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the State Code of this State object.
	 * @return State code
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * Returns the 'official name' of this State object, which is always stored first in the first indice of the collection.
	 * @return Returns the first entry of the namesList storage, which is the 'official name' of the country / state.
	 */
	public String getOfficialName() {
		return namesList.get(0);
	}
	
	/**
	 * Returns true if this country has bordering countries. Subsequently returns false if there aren't.
	 * @return  boolean value
	 */
	public boolean isThereBordering() {
		return borders.isEmpty();
	}
	
	/**
	 * Returns the Border object at the specified index.
	 * @param index
	 * @return Border object
	 */
	public Border getBorderState(int index) {
		if(borders.size() != 0) {
			return borders.get(index);
		}
		else {
			return null;
		}
	}
	
	/**
	 * With the given state, findBorderStateDistance returns the distance, 
	 * if there exists a shared border with this state and the given state.
	 * Returns -1 if there is not a shared border.
	 * @param state
	 * @return Returns -1 if no border is shared, or the distance if otherwise.
	 */
	public int findBorderStateDistance(State state) { // Returns the distance of the border..
		for(Border b: borders) {
			if(b.getState().getId() == state.getId()) {
				return b.getDistance();
			}
		}
		return -1;
	}
	
	/**
	 * Returns the number of borders that this State possesses with other countries / states.
	 * @return Total number of borders for this State.
	 */
	public int getBorderSize() {
		return borders.size();
	}
	
	/**
	 * Appends the given State name to the namesList collection.
	 * @param State name as a String
	 */
	public void addToNamesList(String input) {
		namesList.add(input);
	}
	
	/**
	 * Appends the given Border object to the borders list for this State.
	 * @param New Border object to be added.
	 */
	public void addToBordersList(Border newBorder) {
		borders.add(newBorder);
	}
	
	/**
	 * With a given String, nameMatchInList determines if there is a match between the input String and any of the String names
	 * for this State. (There may be more than one name, and this function accounts for that possibility).
	 * nameMatchInList returns true if there is a match and subsequently, false, if there isn't.
	 * @param String
	 * @return Boolean value
	 */
	public boolean nameMatchInList(String nameToBeMatched) {
		for(String e: namesList) {
			if(e.toLowerCase().equals(nameToBeMatched.toLowerCase())) {
				return true; // Return true if the name is within the list. 
			}
		}
		return false; // Return false by default.
	}
	
	/**
	 * If there is a shared border with this State object and the given stateID (which is used to determine if there is a shared border with a comparison),
	 * then updateBorderDist will update the distance of the specified Border object. Only used during the file reading / parsing process.
	 * Returns true if successful, and false if not.
	 * @param stateID
	 * @param distance
	 * @return Boolean values
	 */
	public boolean updateBorderDist(int stateID, int distance) { // We should have access to their state ID at this point.
		for(Border a: borders) {
			if(a.getState().getId() == stateID) { // If the state id matches the one found in borders..
				a.setDistance(distance); // Set the distance of the shared border.
				return true;
			}
		}
		return false; // Return false if we weren't able to find the state and update accordingly.
	}
}
