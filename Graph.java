package proj3;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {
	
	ArrayList<State> states = new ArrayList<State>(); // Each state has their own list of bordering borders.
	
	HashMap<Integer, State> idStateMap = new HashMap<Integer, State>();
	
	HashMap<String, Integer> nameExceptions = new HashMap<String, Integer>();
	
	
	public Graph() {}
	
	/*
	 * Appends a state to the graph.
	 */
	public void addToGraph(State state) {
		states.add(state);
		idStateMap.put(state.getId(), state);
	}
	
	public Integer getStateHashId(String key) {
		if(nameExceptions.get(key) != null) { // If it is one of the exceptions...
			return nameExceptions.get(key);
		}
		else {
			if(findStateIndex(key) != -1) {
				return states.get(findStateIndex(key)).getId();
			}
			else {
				return -1;
			}
		}
	}
	
	
	public int findStateIndex(String stateName) {
		int stateIndex = 0;
		
		if(nameExceptions.get(stateName) != null) { // If the exception exists.. we check one last time.
			
			for(State e: states) { // We always start with the first state object.
				// nameExceptions.get(stateName)
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
		
		//System.out.println(stateName);
		return -1; // returns -1 by default
	}
	
	public State getState(int index) {
		return states.get(index);
	}
	
	public State getStateHash(int key) {
		return idStateMap.get(key);
	}
	
	public void addToStateBorder(int stateIndex, Border newBorder) {
		getState(stateIndex).addToBordersList(newBorder);
	}
	
	
	// TESTING.
	public State returnFirstStateTest() {
		return states.get(0);
	}
	
	
	public void exceptionHandling() {		
		nameExceptions.put("Burkina Faso", 439);
		nameExceptions.put("Bosnia and Herzegovina", 346);
		nameExceptions.put("Romania", 360);
		nameExceptions.put("Democratic Republic of the Congo", 490);
		nameExceptions.put("Republic of the Congo", 484);
		nameExceptions.put("US", 2);
		nameExceptions.put("North Korea", 731);
		nameExceptions.put("Kyrgyzstan", 703);
		nameExceptions.put("Congo, Democratic Republic of the", 490);
		nameExceptions.put("Congo, Republic of the", 484);
		nameExceptions.put("Cote d'Ivoire", 437);
		nameExceptions.put("Czechia", 316);
		nameExceptions.put("Germany", 260);
		nameExceptions.put("Eswatini", 560);
		nameExceptions.put("North Macedonia", 343);
		nameExceptions.put("Holy See (Vatican City)", 325);
		nameExceptions.put("Timor-Leste", 860);
		nameExceptions.put("UK", 200);
		nameExceptions.put("Korea, North", 731);
		nameExceptions.put("Vietnam", 816);
		nameExceptions.put("Macau", 710);
		nameExceptions.put("UAE", 696);
		nameExceptions.put("United States", 2);
	}
}
