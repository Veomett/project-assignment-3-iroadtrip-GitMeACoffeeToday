import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Graph class is at the center of this pathfinding program, and contains ALL of the data read and parsed during the start.
 * It contains the states, their hash map equivalent (for internal processing), and additionally, contains extra name exceptions for
 * accounting for unconventional names that were not present in the state_name.tsv file, but were used in borders.txt.
 * Only one Graph object should be created.
 * @author michael
 *
 */
public class Graph {
	/**
	 * ArrayList of the countries / states. 
	 */
	ArrayList<State> states = new ArrayList<State>(); // Each state has their own list of bordering borders.
	
	/**
	 * HashMap of the states. Mostly used by the program itself when determining a path.
	 */
	HashMap<String, State> idStateMap = new HashMap<String, State>();
	
	/**
	 * HashMap of state names that may not be initially recognized by the program, due to unusual circumstances and
	 * discrepancies concerning the borders.txt file's contents.
	 */
	HashMap<String, String> nameExceptions = new HashMap<String, String>();
	
	
	/**
	 * Graph constructor. Requires no inputs. Only a Graph object must be created to start recording data.
	 */
	public Graph() {}
	
	/**
	 * Appends a new State object to the states ArrayList.
	 * @param State object
	 */
	public void addToGraph(State state) {
		states.add(state);
		idStateMap.put(state.getCode(), state);
	}
	
	/**
	 * Given a String of a State name, graph will attempt to look through its states ArrayList to find the corresponding
	 * State object (accounts for possible alias' and different namaes). If the state is found, then its state ID is returned,
	 * which serves as the hash key for the hash map.
	 * @param State name
	 * @return State ID (Hash key)
	 */
	public String getStateHashCode(String key) {
		if(nameExceptions.get(key) != null) { // If it is one of the exceptions...
			return nameExceptions.get(key);
		}
		else {
			if(findStateIndex(key) != -1) {
				return states.get(findStateIndex(key)).getCode();
			}
			else {
				return "NOT-FOUND";
			}
		}
	}
	
	/**
	 * Given a String of a State name, findStateIndex will attempt to locate the index of the State with the String
	 * if it exists. -1 is returned if no state is found otherwise.
	 * @param stateName String of the State name
	 * @return Index of the specified State.
	 */
	public int findStateIndex(String stateName) {
		int stateIndex = 0;
		
		if(nameExceptions.get(stateName) != null) { // If the exception exists.. we check one last time.
			
			for(State e: states) { // We always start with the first state object.
				if(e.nameMatchInList(getStateHash(nameExceptions.get(stateName)).getOfficialName())) {
					return stateIndex;
				}
				else {
					stateIndex++;
				}
			}
		}
		else {
			for(State e: states) { // We always start with the first state object.
				if(e.nameMatchInList(stateName)) {
					return stateIndex;
				}
				else {
					stateIndex++;
				}
			}
		}
		return -1; // returns -1 by default
	}
	
	/**
	 * Returns the State at the specified index.
	 * @param index indexindexindexindexindex...?ItsAnIndex.
	 * @return State object
	 */
	public State getState(int index) {
		return states.get(index);
	}
	
	/**
	 * Returns the State hash value corresponding with the given hash key, the State ID
	 * @param key State code
	 * @return State object assosiated with hash key.
	 */
	public State getStateHash(String key) {
		return idStateMap.get(key);
	}
	
	/**
	 * Adds a new Border object to the specified State object at the state index, used only during the data reading and parsing process.
	 * @param stateIndex Index of the state, usually combined with findStateIndex to streamline processing.
	 * @param newBorder The new Border object to be added to the specified State's Border list.
	 */
	public void addToStateBorder(int stateIndex, Border newBorder) {
		getState(stateIndex).addToBordersList(newBorder);
	}
	
	/**
	 * exceptionHandling should always be called on the Graph object at some point, as it provides leeway for state names
	 * that may be less than conventional (less adhering to what was found in the state_name.tsv file).
	 * 
	 * At its very core, exceptionHandling adds to the HashMap nameExceptions, a specific State name String and its corresponding
	 * state ID, which serves as a hash key to use with the idStateMap hash map.
	 */
	public void exceptionHandling() {		
		nameExceptions.put("Burkina Faso", "BFO");
		nameExceptions.put("Bosnia and Herzegovina", "BOS");
		nameExceptions.put("bosnia and herzegovina", "BOS");
		
		nameExceptions.put("Romania", "RUM");
		nameExceptions.put("romania", "RUM");
		
		nameExceptions.put("Democratic Republic of the Congo", "DRC");
		nameExceptions.put("Republic of the Congo", "CON");
		nameExceptions.put("Congo, Democratic Republic of the", "DRC");
		nameExceptions.put("Congo, Republic of the", "CON");
		
		nameExceptions.put("US", "USA");
		nameExceptions.put("us", "USA");
		nameExceptions.put("United States", "USA");
		nameExceptions.put("united states", "USA");
		
		nameExceptions.put("Korea, North", "PRK");
		nameExceptions.put("north korea", "PRK");
		nameExceptions.put("North Korea", "PRK");
		nameExceptions.put("Democratic People's Republic of Korea", "PRK");
		
		nameExceptions.put("Korea, South", "ROK");
		nameExceptions.put("South Korea", "ROK");
		nameExceptions.put("south korea", "ROK");
		
		nameExceptions.put("Kyrgyzstan", "KYR");
		nameExceptions.put("Cote d'Ivoire", "CDI");
		nameExceptions.put("Czechia", "CZR");
		nameExceptions.put("Germany", "GFR");

		nameExceptions.put("North Macedonia", "MAC");
		nameExceptions.put("Timor-Leste", "ETM");
		nameExceptions.put("UK", "UKG");
		nameExceptions.put("uk", "UKG");
		
		nameExceptions.put("Vietnam", "DRV");
		nameExceptions.put("North Vietnam", "DRV");
		
		nameExceptions.put("Spain (Ceuta)", "SPN");
		nameExceptions.put("Morocco (Ceuta)", "MOR");
		
		nameExceptions.put("Turkey (Turkiye)", "TUR");
		nameExceptions.put("Turkiye", "TUR");
	}
}
