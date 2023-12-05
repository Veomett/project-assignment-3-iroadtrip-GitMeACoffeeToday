import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class IRoadTrip {
    
    static String DATE = "2020-12-31";
    
    Graph graph = new Graph();
    


    public IRoadTrip (String [] args) throws IOException { // Constructor
        graph.exceptionHandling();
        parseFiles(graph);
    }


    public int getDistance (String country1, String country2) { // Get distance between two countries.
        if(graph.findStateIndex(country1) == -1 || (graph.findStateIndex(country2) == -1)) { // If either of the two countries are invalid country names..
            return -1;
        }
        else if(graph.findStateIndex(country1) == graph.findStateIndex(country2)) {
            return 0;
        }
        else {
            List<String> path = findPath(country1, country2);
            if(path.isEmpty()) { // If the country names were valid, yet no path existed between them, return -1.
                return -1;
            }
            else {
                int totalDist = 0;
                for(String p: path) {
                    totalDist += Integer.parseInt(p.split("-->")[1].substring(p.split("-->")[1].indexOf("(")+1, p.split("-->")[1].indexOf("k")-1));
                }
                return totalDist;
            }
        }
    }


    public List<String> findPath (String country1, String country2) { // Return a list containing the path from one country to another.
        // First put source country ID into visited states as we start off with it.
        // Then from the source country, we put the ids of all of its neighbooring states into the unvisited (bordersList).
            // Of the unvisited states, we choose the one with the shortest border distance and move it into the visited. (Create a new Route and place it into the route list).
        
        ArrayList<Integer> visitedStatesId = new ArrayList<Integer>();
        ArrayList<Integer> unvisitedStatesId = new ArrayList<Integer>();
                
        HashMap<Integer, Route> hashRoutes = new HashMap<Integer, Route>();
        
        
        int smallestBorderDist = -1;
        int smallestCountryId;
        
        int currrentCountryId = graph.getStateHash(graph.getStateHashId(country1)).getId();
        int destinationCountryId = graph.getStateHash(graph.getStateHashId(country2)).getId();
                
        
        for(int i = 0; i < graph.getStateHash(graph.getStateHashId(country1)).getBorderSize(); i++) { // Adds all bordering states to the unvisited.
            unvisitedStatesId.add(graph.getStateHash(graph.getStateHashId(country1)).getBorderState(i).getState().getId());
        }
        
        
        visitedStatesId.add(graph.getStateHash(graph.getStateHashId(country1)).getId());
        hashRoutes.put(graph.getStateHash(graph.getStateHashId(country1)).getId(), new Route(-1, graph.getStateHash(graph.getStateHashId(country1)).getId(), 0));
        // Create source route...
        
        for(int i = 0; i < graph.getStateHash(graph.getStateHashId(country1)).getBorderSize(); i++) { // Initially adds all bordering state routes to the hashmap.
            hashRoutes.put(graph.getStateHash(graph.getStateHashId(country1)).getBorderState(i).getState().getId(), 
                    
                    new Route(graph.getStateHash(graph.getStateHashId(country1)).getId(), 
                            graph.getStateHash(graph.getStateHashId(country1)).getBorderState(i).getState().getId(), 
                            graph.getStateHash(graph.getStateHashId(country1)).getBorderState(i).getDistance()));
            
        } // Previous is source state, current is country1 ID, distance is 0.        
        
        // Either the unvisited states id list is empty or the next new route ends with the destination.
        while(!unvisitedStatesId.isEmpty()) { // While there are adjacent states to check...
            
            if(!visitedStatesId.contains(currrentCountryId)) { // If the current country has not been visited...
                visitedStatesId.add(currrentCountryId);
            }
            
            for(int i = 0; i < graph.getStateHash(currrentCountryId).getBorderSize(); i++) { // Initially adds all bordering state routes to the hashmap.
                            
                if(hashRoutes.get(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId()) == null) { // If there isn't a path already leading to the border country..
                    hashRoutes.put(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId(), 
                            new Route(graph.getStateHash(currrentCountryId).getId(), 
                                    
                                    graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId(), 
                                    
                                    graph.getStateHash(currrentCountryId).getBorderState(i).getDistance() + 
                                    hashRoutes.get(graph.getStateHash(currrentCountryId).getId()).getDistanceSoFar()));
                } // Add a new key-value pair to hashroutes that contains the route leading to the said border country.
                
                else { // Previous route found, compare distances and update if needed.
                    if((graph.getStateHash(currrentCountryId).getBorderState(i).getDistance() + 
                            hashRoutes.get(graph.getStateHash(currrentCountryId).getId()).getDistanceSoFar()) 
                            
                            < hashRoutes.get(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId()).getDistanceSoFar()) {
                        // If the current border's edge weight + the previous state's distanceSoFar so far is LESS THAN the total distanceSoFar found in the current route, we update.
                        // hashRoutes.get(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId())).getDistanceSoFar()
                        
                        hashRoutes.get(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId()).setPrevStateID(
                                graph.getStateHash(currrentCountryId).getId());
                        
                        hashRoutes.get(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId()).setDistanceSoFar(
                                graph.getStateHash(currrentCountryId).getBorderState(i).getDistance() + 
                            hashRoutes.get(graph.getStateHash(currrentCountryId).getId()).getDistanceSoFar());
                    }
                }
            }
            
            if(currrentCountryId == -1) { // We backed up to the source country, meaning there is no path. Break.
                break;
            }
            
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
            }
            
            
            if(smallestBorderDist != -1) { // If the shortest and unvisited border has been found...
                if(smallestCountryId == destinationCountryId) { // if the next route takes us to our destination, we add the new route and break.
                    break; // Destination reached!
                }
                else {
                    currrentCountryId = smallestCountryId; // Set the current country to the next.
                    unvisitedStatesId = new ArrayList<Integer>(); // Clear unvisited states...
                    
                    for(int i = 0; i < graph.getStateHash(currrentCountryId).getBorderSize(); i++) { // Adds all of the new bordering states to the unvisited.
                        unvisitedStatesId.add(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId());
                    }
                }
            }
            
            else { // We have no borders (unvisited) to go to. (Dead end)
                currrentCountryId = hashRoutes.get(currrentCountryId).getPrevStateID();
                
                unvisitedStatesId = new ArrayList<Integer>(); // Clear unvisited states...
                
                for(int i = 0; i < graph.getStateHash(currrentCountryId).getBorderSize(); i++) { // Adds all of the new bordering states to the unvisited.
                    unvisitedStatesId.add(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId());
                }
            }
        }
        
        
        if(hashRoutes.size() == 1) { // Only source node in the route.
            System.out.println("No path found.");
            return new ArrayList<String>();
        }
        
        else {
            ArrayList<String> finalizedPath = new ArrayList<String>();
            
            int prevCountry = hashRoutes.get(graph.getStateHashId(country2)).getCurrStateID();
            
            while(prevCountry != -1) {
                //System.out.println();
                //System.out.println(graph.getStateHash(prevCountry).getOfficialName());
                if(hashRoutes.get(prevCountry).getPrevStateID() != -1) {
                    finalizedPath.add(0, graph.getStateHash(hashRoutes.get(prevCountry).getPrevStateID()).getOfficialName() 
                            + " --> " 
                            + graph.getStateHash(prevCountry).getOfficialName()
                            + " ("
                            + graph.getStateHash(hashRoutes.get(prevCountry).getPrevStateID()).findBorderStateDistance(graph.getStateHash(prevCountry))
                            + " km.)"
                            );
                }
                prevCountry = hashRoutes.get(prevCountry).getPrevStateID();
            }
            return finalizedPath;
        }
    }


    public void acceptUserInput() {
       Scanner scanner = new Scanner(System.in);
       
       while(true) {
           System.out.print("First Country: ");
           String country1 = scanner.nextLine();
           while(graph.findStateIndex(country1) == -1) {
               System.out.println("Invalid country name! Please try again!");
               System.out.print("First Country: ");
               country1 = scanner.nextLine();
           }
           
           System.out.print("Second Country: ");
           String country2 = scanner.nextLine();
           while(graph.findStateIndex(country2) == -1) {
               System.out.println("Invalid country name! Please try again!");
               System.out.print("First Country: ");
               country2 = scanner.nextLine();
           }
           
           System.out.println();
           
           if(getDistance(country1, country2) != -1) {
               System.out.println("The distance between " + graph.getState(graph.findStateIndex(country1)).getOfficialName() + " and " 
                       + graph.getState(graph.findStateIndex(country2)).getOfficialName() + " is " + getDistance(country1, country2) + "km.\n");
           }
           
           if(findPath(country1, country2).isEmpty()) {
               System.out.println("No path exists between " + graph.getState(graph.findStateIndex(country1)).getOfficialName() 
                       + " and " 
                       + graph.getState(graph.findStateIndex(country2)).getOfficialName() 
                       + ".");
           }
           else {
               System.out.println("Path Found!");
               for(String p: findPath(country1, country2)) {
                   System.out.println(p);
               }
           }
           
           System.out.println();
           System.out.print("Do you wish to quit? (Y/N): ");
           if(scanner.nextLine().equals("Y") || (scanner.nextLine().equals("y"))) {
               break;
           }
       }
       scanner.close();
       //System.out.println("IRoadTrip - skeleton");
    }


    // testing...
    public static void main(String[] args) throws IOException {

        IRoadTrip a3 = new IRoadTrip(args);
        //a3.findPath("United States", "Belize");
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
                    
                    // Consider host name
                    // Consider border (e)
                    
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
