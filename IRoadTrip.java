import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class IRoadTrip {
    
    /**
     * The most up-to-date data in state_name.tsv is concerned if it ends with this specific date.
     * Other lines of data in state_name.tsv that end with older or different dates are ignored.
     */
    static String DATE = "2020-12-31";
    
    Graph graph = new Graph();
    
    /**
     * The parsing is done with the parseFiles method which handles much of the work of parsing.
     * This is done primarily for saving one's eyes from the bundle of code lodged in the constructor.
     * Exception handling is also called.
     * @param args
     * @throws IOException
     */
    public IRoadTrip (String [] args) throws IOException { // Constructor
        graph.exceptionHandling();
        parseFiles(graph, args[0], args[1], args[2]);
    }
    
    /**
     * Returns the distance between the two specified countries if both exist AND share a land border.
     * -1 is returned if both conditions aren't satisfied.
     * getDistance makes a call to findPath, which returns a list of Strings which contain both the names of the countries
     * in the path and their distances in between, the latter of which is parsed and gathered and later returned.
     * 
     * @param country1 The String for the first country.
     * @param country2 The String for the second country.
     * @return Returns -1 if conditions were invalid or no land border existed, or returns the total distance of the path.
     */
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
                    System.out.println(p.split("-->")[1].substring(p.split("-->")[1].indexOf("(")+1, p.split("-->")[1].indexOf(")")));
                    totalDist += Integer.parseInt(p.split("-->")[1].substring(p.split("-->")[1].indexOf("(")+1, p.split("-->")[1].indexOf("k")-1));
                }
                return totalDist;
            }
        }
    }

    /**
     * findPath uses a modified version of Diijstrka's Algorithm, the primary change being that it terminates once a path to the destination country,
     * country 2, is found. Other than that and a few other minor changes, findPath follows the general rules and patterns of the original algorithm.
     * 
     * A hash map is used to store the Route objects that are created or updated as needed when we visit or return to a country.
     * @param country1
     * @param country2
     * @return
     */
    public List<String> findPath (String country1, String country2) { // Return a list containing the path from one country to another.
        // First put source country ID into visited states as we start off with it.
        // Then from the source country, we put the ids of all of its neighbooring states into the unvisited (bordersList).
            // Of the unvisited states, we choose the one with the shortest border distance and move it into the visited. (Create a new Route and place it into the route list).
        
        ArrayList<Integer> visitedStatesId = new ArrayList<Integer>();
        ArrayList<Integer> unvisitedStatesId = new ArrayList<Integer>();
                
        HashMap<Integer, Route> hashRoutes = new HashMap<Integer, Route>();
        
        
        int smallestBorderDist = -1;
        int smallestCountryId;
        
        int currrentCountryId = graph.getStateHash(graph.getStateHashId(country1)).getId(); // Declares and sets the current id of the country to be the source country (starting).
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
            if(!visitedStatesId.contains(currrentCountryId)) { // If the current country has not been visited, we add it to the visited as we are now at said country.
                visitedStatesId.add(currrentCountryId);
            }
            
            
            
            /*
            for(int d: visitedStatesId) {
                System.out.println(graph.getStateHash(d).getOfficialName());
            }
            System.out.println();*/
            
                        
            for(int i = 0; i < graph.getStateHash(currrentCountryId).getBorderSize(); i++) { // Initially adds all bordering state routes to the hashmap if a route doesn't exist already.
                if(hashRoutes.get(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId()) == null) { // If there isn't a path already leading to the border country, we create and add a new one.
                    hashRoutes.put(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId(), 
                            new Route(graph.getStateHash(currrentCountryId).getId(), 
                                    
                                    graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId(), 
                                    
                                    graph.getStateHash(currrentCountryId).getBorderState(i).getDistance() + 
                                    hashRoutes.get(graph.getStateHash(currrentCountryId).getId()).getDistanceSoFar()));
                    
                } // Add a new key-value pair to hashroutes that contains the route leading to the said border country.
                
                else { // Previous route found, compare distances and update if needed.
                    //System.out.println(hashRoutes.get(graph.getStateHash(currrentCountryId).getId()));
                    //System.out.println(graph.getStateHash(currrentCountryId).getId());
                    if((graph.getStateHash(currrentCountryId).getBorderState(i).getDistance() + 
                            hashRoutes.get(graph.getStateHash(currrentCountryId).getId()).getDistanceSoFar()) 
                            
                            < hashRoutes.get(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId()).getDistanceSoFar()) {
                        // If the current border's edge weight + the previous state's distanceSoFar so far is LESS THAN the total distanceSoFar found in the current route, we update.

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
            
            System.out.println("VISITED STATES: ");
            for(int e: visitedStatesId) {
                System.out.println(graph.getStateHash(e).getOfficialName());
            }
            System.out.println();
            
           
            System.out.println("CURRENT BORDER STATES: " + unvisitedStatesId.size());
            for(int i = 0; i < unvisitedStatesId.size(); i++) { // From the list of unvisitedStates, i.e. bordering states...
                
                System.out.println(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getOfficialName());
                
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
            System.out.println("NEXT COUNTRY: " + smallestCountryId);
            System.out.println("NEXT COUNTRY DIST.: " + smallestBorderDist);
            
            System.out.println();
            
            
            if(smallestBorderDist != -1) { // If the shortest and unvisited border has been found...
                System.out.println("CASE 1");
                if(smallestCountryId == destinationCountryId) { // if the next route takes us to our destination, we add the new route and break.
                    // For distance, consider the distance from the last route along with the distance of this current smallestBorderDist.
                    break; // Destination reached!
                }
                else {
                    System.out.println("BEFORE: "+graph.getStateHash(currrentCountryId).getOfficialName());
                    currrentCountryId = smallestCountryId; // Set the current country to the next.
                    System.out.println("AFTER: " +graph.getStateHash(currrentCountryId).getOfficialName());
                    System.out.println("--------------------------------");
                    
                    unvisitedStatesId = new ArrayList<Integer>(); // Clear unvisited states...
                    for(int i = 0; i < graph.getStateHash(currrentCountryId).getBorderSize(); i++) { // Adds all of the new bordering states to the unvisited
                        unvisitedStatesId.add(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId());
                    }
                }
            }
            
            else { // We have no borders (unvisited) to go to. (Dead end)
                System.out.println("CASE 2");
                currrentCountryId = hashRoutes.get(currrentCountryId).getPrevStateID();
                if(currrentCountryId == -1) { // We end up back at the source country after attempting to pathfind to the destination..
                    System.out.println("Returned to source..");
                    break;
                }
                System.out.println("BACKING UP TO " + graph.getStateHash(currrentCountryId).getOfficialName());
                System.out.println();
                
                unvisitedStatesId = new ArrayList<Integer>(); // Clear unvisited states...
                for(int i = 0; i < graph.getStateHash(currrentCountryId).getBorderSize(); i++) { // Adds all of the new bordering states to the unvisited.
                    
                    
                    unvisitedStatesId.add(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId());
                }
            }
        }
        
        
        
        
        
        if(hashRoutes.get(graph.getStateHashId(country2)) == null) { // We were unable to reach the destination.
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
           System.out.print("Starting Country: ");
           String country1 = scanner.nextLine();
           while(graph.findStateIndex(country1) == -1) {
               System.out.println("Invalid starting country name. Please re-enter.");
               System.out.print("First Country: ");
               country1 = scanner.nextLine();
           }
           
           System.out.print("Ending Country: ");
           String country2 = scanner.nextLine();
           while(graph.findStateIndex(country2) == -1) {
               System.out.println("Invalid ending country name. Please re-enter.");
               System.out.print("Second Country: ");
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
               System.out.println("Path:");
               for(String p: findPath(country1, country2)) {
                   System.out.println(p);
               }
           }
           
           System.out.println();
           System.out.print("To continue, press ENTER. To exit, type EXIT: ");
           String input = scanner.nextLine();
           if(input.toLowerCase().equals("exit")) {
               break;
           }
           System.out.println();
       }
       scanner.close();
    }


    // testing...
    public static void main(String[] args) throws IOException {
        
        
        Graph testGraph = new Graph();
        testGraph.exceptionHandling();
        parseFiles(testGraph, "state_name.tsv", "borders.txt", "capdist.csv");
        
                
        //System.out.println(testGraph.getStateHash(testGraph.getStateHashId("China")).getBorderState(14).getState().getOfficialName());
        
        
       IRoadTrip a3 = new IRoadTrip(args);
       //List<String> array = a3.findPath("Germany", "Argentina");
       List<String> array = a3.findPath("Ukraine", "France");
       
       for(String a: array) {
           System.out.println(a);
       }
       
       //a3.acceptUserInput();
        
    }
    
    // Seperate function for processing Strings into data.
    
    public static void parseFiles(Graph graph, String state_num, String borders, String capDist) throws IOException {
                
        // First read state name for the state's name. Create and populate states.
        
        //FileReader reader = new FileReader("/Users/michael/eclipse-workspace/CS245Project3/src/proj3/state_name.tsv");
        FileReader reader = new FileReader(Paths.get(state_num).toAbsolutePath().toString());

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
        //reader = new FileReader("/Users/michael/eclipse-workspace/CS245Project3/src/proj3/borders.txt");
        reader = new FileReader(Paths.get(borders).toAbsolutePath().toString());
        bufferReader = new BufferedReader(reader);
        lineReader = bufferReader.readLine();
        
        ArrayList<String> BorderingState;
        String hostState;
        
        while(lineReader != null) {
            processedLine = lineReader.split(";");
            BorderingState = new ArrayList<String>();
            
            String concatString = "";
            
            hostState = processedLine[0].substring(0, processedLine[0].indexOf("=")-1);
            
            if(graph.findStateIndex(hostState) == -1) { // We ignore certain countries / states.
                
                lineReader = bufferReader.readLine(); // Read another line...
                continue;
            }
            //System.out.println("HOST COUNTRY: " + hostState);
            /*System.out.println("Borders: " + graph.getState(graph.findStateIndex(hostState)).getBorderSize());
            if(hostState.equals("South Africa")) {
                System.out.println(graph.getState(graph.findStateIndex(hostState)).getBorderState(0).getState().getOfficialName());
                System.out.println(graph.getState(graph.findStateIndex(hostState)).getBorderState(1).getState().getOfficialName());
            }*/
            
            
            
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
                    //System.out.println(graph.getStateHash(710).getBorderSize());
                    
                    // We know the host state is valid as we have checked it at the start of the loop, thus we only need to add our bordering states.
                    // Index (position) of host state, state object.
                    
                    if(graph.findStateIndex(e) != -1) { // If the bordering state is valid...
                        Border newBorder = new Border(graph.getState(graph.findStateIndex(e)));
                        if(newBorder.getState().getId() == 20) { // Accounting for Denmark's offshore territory, known as GREENLAND.
                            // We don't consider Denmark's border with Greenland.
                            continue;
                        }
                        graph.addToStateBorder(graph.findStateIndex(hostState), newBorder);
                        
                        //System.out.println("PARENT: " + graph.getState(graph.findStateIndex(hostState)).getOfficialName());
                        //System.out.println("BORDER: " + newBorder.getState().getOfficialName());
                        
                        /*
                        if(!e.equals(graph.getState(graph.findStateIndex(e)).getOfficialName())) {
                            
                            System.out.println("TEST1: " + e);
                            System.out.println("TEST2: " + graph.getState(graph.findStateIndex(e)).getOfficialName());
                        }*/
                    }

                    
                    
                    /*
                    if(graph.findStateIndex(hostState) != -1 && graph.findStateIndex(e) != -1) { // If the state exists in the file..
                        graph.addToStateBorder(graph.findStateIndex(hostState), new Border(graph.getState(graph.findStateIndex(e))));
                        //System.out.println(graph.getState(graph.findStateIndex(hostState)).getBorderSize());
                    }
                    
                    else if(graph.findStateIndex(hostState) != -1) { // If only the bordering name doesn't match..]
                        if(graph.getStateHashId(e) != -1) { // If there exists an exception name...
                            graph.addToStateBorder(graph.findStateIndex(hostState), new Border(graph.getStateHash(graph.getStateHashId(e))));
                            //System.out.println(graph.getState(graph.findStateIndex(hostState)).getBorderSize());
                        }
                    }
                    
                    else if(graph.findStateIndex(e) != -1) { // If only the host state name doesn't match..
                        if(graph.getStateHashId(hostState) != -1) { // If there exists an exception name...
                            graph.addToStateBorder(graph.findStateIndex(graph.getStateHash(graph.getStateHashId(hostState)).getOfficialName()), 
                                    new Border(graph.getState(graph.findStateIndex(e))));
                            //System.out.println(graph.getState(graph.findStateIndex(hostState)).getBorderSize());
                        }
                    }
                    
                    else { // If both the host and state name doesn't match...
                        if(graph.getStateHashId(hostState) != -1 && graph.getStateHashId(e) != -1) { // If there exists an exception name for both host and bordering...
                            graph.addToStateBorder(graph.findStateIndex(hostState), new Border(graph.getStateHash(graph.getStateHashId(e))));
                            //System.out.println(graph.getState(graph.findStateIndex(hostState)).getBorderSize());
                        }
                    }
                    */
                }
                
                //if(hostState.equals("South Africa")) {
                //  System.out.println("CUHH");
                    //System.out.println("TESTING: " + graph.getState(graph.findStateIndex(hostState)).getBorderState(7).getState().getOfficialName());
                //}
                //System.out.println();
            }
            lineReader = bufferReader.readLine(); // Read another line...
        }
        
        bufferReader.close();
        reader.close(); // Close the file before reading another.
        
        
        
        
        // Now we will read capdist.csv
        //reader = new FileReader("/Users/michael/eclipse-workspace/CS245Project3/src/proj3/capdist.csv");
        reader = new FileReader(Paths.get(capDist).toAbsolutePath().toString());
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