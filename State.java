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
	public int getId() {
		return id;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getOfficialName() {
		return namesList.get(0);
	}
	
	public boolean isThereBordering() {
		return borders.isEmpty();
	}
	
	public Border getBorderState(int index) {
		if(borders.size() != 0) {
			return borders.get(index);
		}
		else {
			return null;
		}
	}
	
	public int findBorderStateDistance(State state) { // Returns the distance of the border..
		for(Border b: borders) {
			if(b.getState().getId() == state.getId()) {
				return b.getDistance();
			}
		}
		return -1;
	}
	
	public int getBorderSize() {
		return borders.size();
	}
	
	// Add to the Arraylist namesList.
	public void addToNamesList(String input) {
		namesList.add(input);
	}
	
	// Add to Arraylist borders
	public void addToBordersList(Border newBorder) {
		borders.add(newBorder);
	}
	
	public boolean nameMatchInList(String nameToBeMatched) {
		for(String e: namesList) {
			if(e.toLowerCase().equals(nameToBeMatched.toLowerCase())) {
				return true; // Return true if the name is within the list. 
			}
		}
		return false; // Return false by default.
	}
	
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
