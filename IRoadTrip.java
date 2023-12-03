import java.util.List;

public class IRoadTrip {


    public IRoadTrip (String [] args) {
        // Replace with your code
    }


    public int getDistance (String country1, String country2) {
        // Replace with your code
        return -1;
    }


    public List<String> findPath (String country1, String country2) {
        // First put source country ID into visited states as we start off with it.
        // Then from the source country, we put the ids of all of its neighbooring states into the unvisited (bordersList).
            // Of the unvisited states, we choose the one with the shortest border distance and move it into the visited. (Create a new Route and place it into the route list).
        
        ArrayList<Integer> visitedStatesId = new ArrayList<Integer>();
        ArrayList<Integer> unvisitedStatesId = new ArrayList<Integer>();
        
        ArrayList<Route> routes = new ArrayList<Route>();
        
        int smallestBorderDist = -1;
        int smallestCountryId;
        
        int currrentCountryId = graph.getStateHash(graph.getStateHashId(country1)).getId();
        int destinationCountryId = graph.getStateHash(graph.getStateHashId(country2)).getId();
        
        visitedStatesId.add(currrentCountryId);
        
        for(int i = 0; i < graph.getStateHash(graph.getStateHashId(country1)).getBorderSize(); i++) { // Adds all bordering states to the unvisited.
            unvisitedStatesId.add(graph.getStateHash(graph.getStateHashId(country1)).getBorderState(i).getState().getId());
        }
        routes.add(new Route(-1, currrentCountryId, 0)); // First source state from itself to itself. Has no previous state. Distance 0.
        
        
        // Either the unvisited states id list is empty or the next new route ends with the destination.
        while(!unvisitedStatesId.isEmpty()) { // While there are adjacent states to check...
            smallestBorderDist = -1;
            smallestCountryId = -1;
            
            for(int i = 0; i < unvisitedStatesId.size(); i++) { // From the list of unvisitedStates, i.e. bordering states...
                
                if(!visitedStatesId.contains(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId())) { // If the border hasn't been visited already...
                    // We only consider the adjancent states that have not been visited yet...
                    if(smallestBorderDist == -1) { // Base case, we have no data yet.
                        smallestBorderDist = graph.getStateHash(currrentCountryId).getBorderState(i).getDistance();
                        smallestCountryId = graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId();
                    }
                    else {
                        if(smallestBorderDist > graph.getStateHash(currrentCountryId).getBorderState(i).getDistance()) {
                            smallestBorderDist = graph.getStateHash(currrentCountryId).getBorderState(i).getDistance();
                            smallestCountryId = graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId();
                        }
                    }
                }
                /*
                else { // Remove the state id from the unvisitedStatesId..
                    unvisitedStatesId.remove(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId()); // We have visited the countryId so now we remove it from unvisited.
                    
                    // We start the loop over, save for the change that the loop is now one adjacent state shorter.
                    smallestBorderDist = -1;
                    smallestCountryId = -1;
                    i = 0; // reset i to 0.
                }*/
            }
            
            if(smallestBorderDist != -1) { // If the shortest and unvisited border has been found...
                if(smallestCountryId == destinationCountryId) { // if the next route takes us to our destination, we add the new route and break.
                    routes.add(new Route(currrentCountryId, smallestCountryId, routes.get(routes.size()-1).distanceSoFar+smallestBorderDist));
                    // For distance, consider the distance from the last route along with the distance of this current smallestBorderDist.
                    break;
                }
                else {
                    routes.add(new Route(currrentCountryId, smallestCountryId, routes.get(routes.size()-1).distanceSoFar+smallestBorderDist)); // Add the route normally.
                    currrentCountryId = smallestCountryId; // Set the current country to the next.
                    unvisitedStatesId = new ArrayList<Integer>(); // Clear unvisited states...
                    
                    for(int i = 0; i < graph.getStateHash(currrentCountryId).getBorderSize(); i++) { // Adds all of the new bordering states to the unvisited.
                        unvisitedStatesId.add(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId());
                    }
                }
            }
            else { // We've visited all of the states, so we break.
                break;
            }
        }
        return null;
    }


    public void acceptUserInput() {
        // Replace with your code
        System.out.println("IRoadTrip - skeleton");
    }


    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);

        a3.acceptUserInput();
    }

    // Seperate function for processing Strings into data.
    
    public static void parseFiles(Graph graph) throws IOException {
                
        // First read state name for the state's name. Create and populate states.
        
        FileReader reader = new FileReader("/Users/michael/eclipse-workspace/CS245Project3/src/proj3/state_name.tsv");
        BufferedReader bufferReader = new BufferedReader(reader);
        String lineReader = bufferReader.readLine();
        
        lineReader = bufferReader.readLine();
        
        String[] processedLine;
        int stateID;
        String stateCode;
        
        
        while(lineReader != null) { // Read the entire state_name.tsv file.
            processedLine = lineReader.split("  "); // Split up the line.
            stateID = Integer.parseInt(processedLine[0]); // First entry in the line is always the state ID.
            stateCode = processedLine[1]; // State code is always the second in the line.
            
            
            if(processedLine[4].equals(DATE)) { // If the information corresponds to the most recent date, we proceed with the creation of a new state.
                String stateName = "";
                ArrayList<String> stateAlias = new ArrayList<String>();
                // Load up the state ID and state CODE.
                
                if(processedLine[2].contains(",") && processedLine[2].contains("(")) { // If there is a comma... and an alias
                    stateName = stateName.concat(processedLine[2].substring(processedLine[2].indexOf(",")+2, processedLine[2].indexOf("("))
                            +processedLine[2].substring(0, processedLine[2].indexOf(",")));
                    stateAlias.add(processedLine[2].substring(processedLine[2].indexOf("(")+1, processedLine[2].length()-1));
                }
                else if(processedLine[2].contains(",")) { // Just a comma...
                    stateName = stateName.concat(processedLine[2].substring(processedLine[2].indexOf(",")+2, processedLine[2].length())
                            +" "+processedLine[2].substring(0, processedLine[2].indexOf(",")));
                }
                else if(processedLine[2].contains("(")) { // Just an alias...
                    stateName = stateName.concat(processedLine[2].split(" ")[0]);
                    stateAlias.add(processedLine[2].substring(processedLine[2].indexOf("(")+1, processedLine[2].length()-1));
                }
                else { // No comma or alias...
                    if(processedLine[2].contains("/")) { // Does have a slash?
                        stateName = stateName.concat(processedLine[2].substring(0, processedLine[2].indexOf("/")));
                        stateAlias.add(processedLine[2].substring(processedLine[2].indexOf("/")+1, processedLine[2].length()));
                    }
                    else { // Doesn't have a slash?
                        stateName = stateName.concat(processedLine[2]);
                    }
                }
                State newState = new State(stateID, stateCode);
                
                //System.out.println("HOST: " + stateName);
                newState.addToNamesList(stateName); // First add the official state name.
                
                for(String a: stateAlias) { // Secondly, add the alias' for the state.
                    //System.out.println(a);
                    newState.addToNamesList(a);
                }
                graph.addToGraph(newState); // Finally, add the state to the graph.
            }
            lineReader = bufferReader.readLine(); // Read another line...
        }
        bufferReader.close();
        reader.close(); // Close the file before reading another.
        
        
        
        
        // Now we will read borders.txt
        reader = new FileReader("/Users/michael/eclipse-workspace/CS245Project3/src/proj3/borders.txt");
        bufferReader = new BufferedReader(reader);
        lineReader = bufferReader.readLine();
        
        ArrayList<String> BorderingState;
        String hostState;
        
        while(lineReader != null) {
            processedLine = lineReader.split(";");
            BorderingState = new ArrayList<String>();
            
            String concatString = "";
            
            hostState = processedLine[0].substring(0, processedLine[0].indexOf("=")-1);
            //System.out.println("HOST COUNTRY: " + hostState);
            
            if(!((processedLine[0].indexOf("=") + 1) == processedLine[0].length()-1)) {
                
                int d = 0; // Index location of the = in the subarray of the array created by the split.
                for(String c: processedLine[0].split(" ")) { // Used to find the location of the '=' character in the array created by the split..
                    if(c.contains("=")) {
                        d++;
                        break;
                    }
                    else {
                        d++;
                    }
                }
            
                
                
                for(int a = d; a < processedLine[0].split(" ").length; a++) {
                    if(processedLine[0].split(" ")[a+2].equals("km")) {
                        concatString = concatString.concat(processedLine[0].split(" ")[a]);
                        BorderingState.add(concatString);
                        concatString = "";
                        break;
                    }
                    else {
                        concatString = concatString.concat(processedLine[0].split(" ")[a] + " ");
                        //BorderingState.add(processedLine[0].split(" ")[a]);
                    }
                }
                
                            
                for(int i = 1; i <= processedLine.length-1; i++) {    
                    for(int a = 0; a < processedLine[i].split(" ").length; a++) {
                        //System.out.println(processedLine[i].split(" ")[a]);
                        
                        if(processedLine[i].split(" ")[a+2].equals("km")) {
                            concatString = concatString.concat(processedLine[i].split(" ")[a]);
                            concatString = concatString.substring(1, concatString.length());
                            BorderingState.add(concatString);
                            concatString = "";
                            break;
                        }
                        else {
                            concatString = concatString.concat(processedLine[i].split(" ")[a] + " ");
                        }
                    }
                }
                
                
                for(String e: BorderingState) {
                    //System.out.println(e);
                    if(graph.findStateIndex(hostState) != -1 && graph.findStateIndex(e) != -1) { // If the state exists in the file..
                        graph.addToStateBorder(graph.findStateIndex(hostState), new Border(graph.getState(graph.findStateIndex(e))));
                    }
                    else if(graph.findStateIndex(hostState) != -1) { // If only the bordering name doesn't match..]
                        if(graph.getStateHashId(e) != -1) { // If there exists an exception name...
                            graph.addToStateBorder(graph.findStateIndex(hostState), new Border(graph.getStateHash(graph.getStateHashId(e))));
                        }
                    }
                    else if(graph.findStateIndex(e) != -1) { // If only the host state name doesn't match..
                        if(graph.getStateHashId(hostState) != -1) { // If there exists an exception name...
                            
                            graph.addToStateBorder(graph.findStateIndex(graph.getStateHash(graph.getStateHashId(hostState)).getOfficialName()), 
                                    new Border(graph.getState(graph.findStateIndex(e))));
                        }
                    }
                    else { // If both the host and state name doesn't match...
                        if(graph.getStateHashId(hostState) != -1 && graph.getStateHashId(e) != -1) { // If there exists an exception name for both host and bordering...
                            graph.addToStateBorder(graph.findStateIndex(hostState), new Border(graph.getStateHash(graph.getStateHashId(e))));
                        }
                    }
                }
            }
            
            lineReader = bufferReader.readLine(); // Read another line...
        }
        
        bufferReader.close();
        reader.close(); // Close the file before reading another.
        
        
        // Now we will read capdist.csv
        reader = new FileReader("/Users/michael/eclipse-workspace/CS245Project3/src/proj3/capdist.csv");
        bufferReader = new BufferedReader(reader);
        bufferReader.readLine(); // Skip past the top of the program.
        
        lineReader = bufferReader.readLine();
        
        while(lineReader != null) {
            processedLine = lineReader.split(",");
            //Integer.parseInt(processedLine[0])
            int distanceKm = Integer.parseInt(processedLine[4]);
            
            int hostStateId = Integer.parseInt(processedLine[0]);
            int destStateId = Integer.parseInt(processedLine[2]);
            
            if(graph.getStateHash(hostStateId) != null && graph.getStateHash(destStateId) != null) { // Initially, we check if both states exist.
                
                if(graph.getStateHash(hostStateId).updateBorderDist(destStateId, distanceKm)) { // If the second state shares a border with the host state, we add the edge weight.
                    // This statement will only execute if the distance was sucessfully updated.
                    // The host state's border with the destination state has its weight(distance) updated.
                    
                    graph.getStateHash(destStateId).updateBorderDist(hostStateId, distanceKm);
                    // The destination state's border with the host state has its weight(distance) updated.
                }
            }
            lineReader = bufferReader.readLine();
        }
        bufferReader.close();
        reader.close(); // Close the file before reading another.*/
    }
}

