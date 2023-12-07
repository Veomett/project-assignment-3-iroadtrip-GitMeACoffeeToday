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
     * Returns the distance between the two specified countries' capitals if both exist AND share a land border.
     * -1 is returned if both conditions aren't satisfied.
     * getDistance 
     * 
     * @param country1 The String for the first country.
     * @param country2 The String for the second country.
     * @return Returns -1 if conditions were invalid or no land border existed, or returns the total distance of the path.
     */
    public int getDistance (String country1, String country2) { // Get distance between two countries.
        if(graph.findStateIndex(country1) == -1 || (graph.findStateIndex(country2) == -1)) { // If either of the two countries are invalid country names..
            return -1;
        }
        else if(graph.findStateIndex(country1) == graph.findStateIndex(country2)) { // If both countries are the same, we return 0.
            return 0;
        }
        else { // We have two countries which are valid and not the same, -1 is returned if the countries do not border each other.
            return graph.getStateHash(graph.getStateHashCode(country1)).findBorderStateDistance(graph.getStateHash(graph.getStateHashCode(country2)));
        }
    }

    /**
     * findPath uses a modified version of Diijstrka's Algorithm, the primary change being that it terminates once a path to the destination country,
     * country 2, is found. Other than that and a few other minor changes, findPath follows the general rules and patterns of the original algorithm.
     * 
     * findPath works with four things, a list of visitedStates codes, unvisitedStates codes, a hashmap of routes, and an initial case for the starting country.
     * A route is essentially an edge between two nodes, or more specifically, a path that leads to a certain destination (country). A country only has one valid route to it and
     * that route, at the end of the algorithm, is assured to be the shortest / most-efficient path. 
     * 
     * Each route has a 'weight' that grows depending on the previous route that 'led' to it. We use this weight to determine when we update our routes weights and previous nodes.
     * (i.e. we have this route with this weight initially from one place to another, but then another 'new route' that leads to the same destination, yet has a shorter weight overall,
     * replaces this route by updating it with the better previous country and distance.
     * 
     * Every time we visit a country, we add it to our list visited state codes, first loop through all of the bordering countries of the current country, creating routes to them
     * if they don't already exist, or updating them if necessary. Secondly, we loop through the bordering countries again and decide which one to go to. We only visit countries that are not
     * included in the visitedStatesCode list (i.e. we don't visit the ones we visited already). This algorithm is greedy, so out of all of the bordering countries that are unvisited, we only 
     * consider the one with the 'shortest path'.
     * 
     * If it is the case that we don't manage to find a path to a bordering country, we will have run into a 'dead end' and must backtrack. Thus we return to the previously visited country and
     * repeat the same prior process, but this time with an updated visited states list that will exclude states we have already visited (we won't go back to those).
     * 
     * Each time we move on or go back, we clear our unvisitedStatesCode list (which only contains the bordering countries for a particular current country) and repopulate it with the presently 
     * bordering countries for the country we go to.
     * NOTE that we don't consider bordering countries that have a distance of -1. This is to account for countries that did not have valid data entries in borders.txt, like Kosovo. A border with
     * a negative edge distance is useless to us and can even break the program.
     * 
     * 
     * A hash map is used to store the Route objects that are created or updated as needed when we visit or return to a country.
     * @param country1 Starting country name
     * @param country2 Destination country name
     * @return A List (Empty if no path)
     */
    public List<String> findPath (String country1, String country2) { // Return a list containing the path from one country to another.
        // First put source country code into visited states as we start off with it.
        // Then from the source country, we put the ids of all of its neighbooring states into the unvisited (bordersList).
            // Of the unvisited states, we choose the one with the shortest border distance and move it into the visited. (Create a new Route and place it into the route list).
        
        ArrayList<String> visitedStatesCode = new ArrayList<String>();
        ArrayList<String> unvisitedStatesCode = new ArrayList<String>();
                
        HashMap<String, Route> hashRoutes = new HashMap<String, Route>();
        
        // These two entries keep track of the 'next' bordering country with the shortest path, if there is one. 
        // If there isn't, smallestBorderDist remains -1 to indicate so.
        int smallestBorderDist = -1;
        String smallestCountryCode; 
        
        String currentCountryCode = graph.getStateHash(graph.getStateHashCode(country1)).getCode(); // Declares and sets the current code of the country to be the source country (starting).
        String destinationCountryId = graph.getStateHash(graph.getStateHashCode(country2)).getCode();
                
        
        for(int i = 0; i < graph.getStateHash(graph.getStateHashCode(country1)).getBorderSize(); i++) { // Adds all bordering states to the unvisited.
            if(graph.getStateHash(graph.getStateHashCode(country1)).getBorderState(i).getDistance() != -1) { // If the border has a valid distance, we consider it. Otherwise we pass over it.
                unvisitedStatesCode.add(graph.getStateHash(graph.getStateHashCode(country1)).getBorderState(i).getState().getCode());
            }
        }
        
        
        visitedStatesCode.add(graph.getStateHash(graph.getStateHashCode(country1)).getCode());
        hashRoutes.put(graph.getStateHash(graph.getStateHashCode(country1)).getCode(), new Route("HEAD", graph.getStateHash(graph.getStateHashCode(country1)).getCode(), 0));
        // Create source route...
        
        for(int i = 0; i < graph.getStateHash(graph.getStateHashCode(country1)).getBorderSize(); i++) { // Initially adds all bordering state routes to the hashmap.
            if((graph.getStateHash(graph.getStateHashCode(country1)).getBorderState(i).distance != -1)) { // If the border has distance, we ingore it..
                hashRoutes.put(graph.getStateHash(graph.getStateHashCode(country1)).getBorderState(i).getState().getCode(), 
                        
                        new Route(graph.getStateHash(graph.getStateHashCode(country1)).getCode(), 
                                graph.getStateHash(graph.getStateHashCode(country1)).getBorderState(i).getState().getCode(), 
                                graph.getStateHash(graph.getStateHashCode(country1)).getBorderState(i).getDistance()));
            }
        } // Previous is source state, current is country1 code, distance is 0.
        
            
        // Either the unvisited states code list is empty or the next new route ends with the destination.
        // We loop until we truly run out of countries to visit. (Have to fully explore all bordering countries to explore all possible routes to obtain the shortest and most efficient)
        while(!unvisitedStatesCode.isEmpty()) { // While there are adjacent states to check...
            if(!visitedStatesCode.contains(currentCountryCode)) { // If the current country has not been visited, we add it to the visited as we are now at said country.
                visitedStatesCode.add(currentCountryCode);
            }
            
                        
            for(int i = 0; i < graph.getStateHash(currentCountryCode).getBorderSize(); i++) { // Initially adds all bordering state routes to the hashmap if a route doesn't exist already.
                if(hashRoutes.get(graph.getStateHash(currentCountryCode).getBorderState(i).getState().getCode()) == null) { // If there isn't a path already leading to the border country, we create and add a new one.
                    
                    if(graph.getStateHash(currentCountryCode).getBorderState(i).getDistance() != -1) {
                        hashRoutes.put(graph.getStateHash(currentCountryCode).getBorderState(i).getState().getCode(), 
                                new Route(graph.getStateHash(currentCountryCode).getCode(), 
                                        
                                        graph.getStateHash(currentCountryCode).getBorderState(i).getState().getCode(), 
                                        
                                        graph.getStateHash(currentCountryCode).getBorderState(i).getDistance() + 
                                        hashRoutes.get(graph.getStateHash(currentCountryCode).getCode()).getDistanceSoFar()));
                    }
                } // Add a new key-value pair to hashroutes that contains the route leading to the said border country.
                
                else { // Previous route found, compare distances and update if needed.
                    if((graph.getStateHash(currentCountryCode).getBorderState(i).getDistance() + 
                            hashRoutes.get(graph.getStateHash(currentCountryCode).getCode()).getDistanceSoFar()) 
                            
                            < hashRoutes.get(graph.getStateHash(currentCountryCode).getBorderState(i).getState().getCode()).getDistanceSoFar()) {
                        // If the current border's edge weight + the previous state's distanceSoFar so far is LESS THAN the total distanceSoFar found in the current route, we update.

                        hashRoutes.get(graph.getStateHash(currentCountryCode).getBorderState(i).getState().getCode()).setPrevStateCode(
                                graph.getStateHash(currentCountryCode).getCode());
                        
                        
                        hashRoutes.get(graph.getStateHash(currentCountryCode).getBorderState(i).getState().getCode()).setDistanceSoFar(
                                graph.getStateHash(currentCountryCode).getBorderState(i).getDistance() + 
                            hashRoutes.get(graph.getStateHash(currentCountryCode).getCode()).getDistanceSoFar());
                    }
                }
            }
            
            if(currentCountryCode.equals("HEAD")) { // We backed up to the source country, meaning there is no path. Break.
                break;
            }
            
            smallestBorderDist = -1;
            smallestCountryCode = null;
            
            for(int i = 0; i < unvisitedStatesCode.size(); i++) { // From the list of unvisitedStates, i.e. bordering states...
                                
                if(!visitedStatesCode.contains(graph.getStateHash(currentCountryCode).getBorderState(i).getState().getCode()) &&
                        graph.getStateHash(currentCountryCode).getBorderState(i).getDistance() != -1) { // If the border hasn't been visited already and has a valid distance..
                    // We only consider the adjancent states that have not been visited yet...
                    if(smallestBorderDist == -1) { // Base case, we have no data yet.
                        smallestBorderDist = graph.getStateHash(currentCountryCode).getBorderState(i).getDistance();
                        smallestCountryCode = graph.getStateHash(currentCountryCode).getBorderState(i).getState().getCode();
                    }
                    else {
                        if(smallestBorderDist > graph.getStateHash(currentCountryCode).getBorderState(i).getDistance()) {
                            smallestBorderDist = graph.getStateHash(currentCountryCode).getBorderState(i).getDistance();
                            smallestCountryCode = graph.getStateHash(currentCountryCode).getBorderState(i).getState().getCode();
                        }
                    }
                }
            }
            //System.out.println("NEXT COUNTRY: " + smallestCountryCode);
            //System.out.println("NEXT COUNTRY DIST.: " + smallestBorderDist);
            //System.out.println();
            
            
            if(smallestBorderDist != -1) { // If the shortest and unvisited border has been found...
                if(smallestCountryCode == destinationCountryId) { // if the next route takes us to our destination, we add the new route and break.
                    // For distance, consider the distance from the last route along with the distance of this current smallestBorderDist.
                
                    if(visitedStatesCode.indexOf(currentCountryCode)-1 != -1) {
                        currentCountryCode = visitedStatesCode.get(visitedStatesCode.indexOf(currentCountryCode)-1);
            
                        unvisitedStatesCode = new ArrayList<String>(); // Clear unvisited states...
                        for(int i = 0; i < graph.getStateHash(currentCountryCode).getBorderSize(); i++) { // Adds all of the new bordering states to the unvisited, assuming they have a valid distance.
                            if(graph.getStateHash(currentCountryCode).getBorderState(i).getDistance() != -1) {
                                unvisitedStatesCode.add(graph.getStateHash(currentCountryCode).getBorderState(i).getState().getCode());
                            }
                        }
                    }
                    else {
                        break; // Destination reached!
                    }
                }
                else {
                    currentCountryCode = smallestCountryCode; // Set the current country to the next.
                    
                    unvisitedStatesCode = new ArrayList<String>(); // Clear unvisited states...
                    for(int i = 0; i < graph.getStateHash(currentCountryCode).getBorderSize(); i++) { // Adds all of the new bordering states to the unvisited
                        if(graph.getStateHash(currentCountryCode).getBorderState(i).distance != -1) { // Must have a valid distance...
                            unvisitedStatesCode.add(graph.getStateHash(currentCountryCode).getBorderState(i).getState().getCode());
                        }
                    }
                }
            }
            
            else { // We have no borders (unvisited) to go to. (Dead end)
                
                if(currentCountryCode.equals(graph.getStateHash(graph.getStateHashCode(country1)).getCode())) { // We end up back at the source country after attempting to pathfind to the destination..
                    visitedStatesCode.add(graph.getStateHash(graph.getStateHashCode(country1)).getCode()); // Add France to the visited.
                    break;
                }
                
                currentCountryCode = visitedStatesCode.get(visitedStatesCode.indexOf(currentCountryCode)-1);
                
                unvisitedStatesCode = new ArrayList<String>(); // Clear unvisited states...
                for(int i = 0; i < graph.getStateHash(currentCountryCode).getBorderSize(); i++) { // Adds all of the new bordering states to the unvisited, assuming they have a valid distance.
                    if(graph.getStateHash(currentCountryCode).getBorderState(i).getDistance() != -1) {
                        unvisitedStatesCode.add(graph.getStateHash(currentCountryCode).getBorderState(i).getState().getCode());
                    }
                }
            }
        }
        
        
        
        if(hashRoutes.get(graph.getStateHashCode(country2)) == null) { // We were unable to reach the destination, thus indicated by our routes list not having a path to the destination.
            return new ArrayList<String>();
        }
        
        else { // We were able to find a path, and now we process our routes for it, working backwards from the route to the destination to its starting point.
            ArrayList<String> finalizedPath = new ArrayList<String>();
            
            String prevCountry = hashRoutes.get(graph.getStateHashCode(country2)).getCurrStateCode();
            
            while(!prevCountry.equals("HEAD")) {
                if(!(hashRoutes.get(prevCountry).getPrevStateCode()).equals("HEAD")) {
                    finalizedPath.add(0, graph.getStateHash(hashRoutes.get(prevCountry).getPrevStateCode()).getOfficialName() 
                            + " --> " 
                            + graph.getStateHash(prevCountry).getOfficialName()
                            + " ("
                            + graph.getStateHash(hashRoutes.get(prevCountry).getPrevStateCode()).findBorderStateDistance(graph.getStateHash(prevCountry))
                            + " km.)"
                            );
                }
                prevCountry = hashRoutes.get(prevCountry).getPrevStateCode();
            }
            return finalizedPath;
        }
    }

    /**
     * Accepts the user input and loops until given the termination command by the user.
     * Responsible for printing out the output afterwards. 
     * Prints out both the path and the total distance (via path) from the source to the destination.
     */
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
               for(String p: findPath(country1, country2)) {
                   System.out.println(p);
               }
           }
           
           System.out.println();
           System.out.print("To continue, press ENTER. To exit, type EXIT: ");
           String input = scanner.nextLine();
           if(input.toLowerCase().equals("exit")) {
               System.out.println("Good Bye!    Do Widzenia!    مع السلامة! Viszontlátásra!");
               break;
           }
           System.out.println();
       }
       scanner.close();
    }


    // testing...
    public static void main(String[] args) throws IOException {
       IRoadTrip a3 = new IRoadTrip(args);
       a3.acceptUserInput();
        
    }
    
    
    /**
     * parseFiles is responsible for reading all three provided files, parsing their data and recording it in graph. Makes use of the FileReader class.
     * If the path is not found, by some directory failure, (I encountered this in Eclipse with my project files and data files being in the same package,
     * paths.get was unable to find the specific location unless I placed it explicitly in the project folder, PLEASE manually obtain the file path from
     * presumably a properties section for each of the three files and copy and paste it as a String into the other FileReader declaration and initialization
     * above. (Three parts).
     * 
     * @param graph Graph object to recieve data.
     * @param borders String name of the borders.txt file
     * @param capDist String name of the capdist.csv file
     * @param state_nam String name of the dataProblems.txt file
     * @throws IOException Exception thrown when file was failed to be read / located.
     */
    public static void parseFiles(Graph graph, String borders, String capDist, String state_nam) throws IOException {
                
        // First read state name for the state's name. Create and populate states.
        
        //FileReader reader = new FileReader("/Users/michael/eclipse-workspace/CS245Project3/src/proj3/state_name.tsv");
        FileReader reader = new FileReader(Paths.get(state_nam).toAbsolutePath().toString());

        BufferedReader bufferReader = new BufferedReader(reader);
        String lineReader = bufferReader.readLine();
        
        lineReader = bufferReader.readLine();
        
        String[] processedLine;
        int stateID;
        String stateCode;
        
        
        while(lineReader != null) { // Read the entire state_name.tsv file.
            processedLine = lineReader.split("  "); // Split up the line.
            stateID = Integer.parseInt(processedLine[0]); // First entry in the line is always the state code.
            stateCode = processedLine[1]; // State code is always the second in the line.
            
            if(processedLine[4].equals(DATE)) { // If the information corresponds to the most recent date, we proceed with the creation of a new state.
                String stateName = "";
                ArrayList<String> stateAlias = new ArrayList<String>();
                // Load up the state code and state CODE.
                
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
                    // We know the host state is valid as we have checked it at the start of the loop, thus we only need to add our bordering states.
                    // Index (position) of host state, state object.
                    
                    if(graph.findStateIndex(e) != -1) { // If the bordering state is valid...
                        Border newBorder = new Border(graph.getState(graph.findStateIndex(e)));
                        
                        if(graph.getStateHash(graph.getStateHashCode(hostState)).getCode().equals("DEN") 
                                && newBorder.getState().getCode().equals("CAN")) { // Accounting for Denmark's offshore territory, known as GREENLAND.
                            //We don't consider Denmark's border with Greenland.
                            continue;
                        }
                        graph.addToStateBorder(graph.findStateIndex(hostState), newBorder);
                    }
                }
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
            int distanceKm = Integer.parseInt(processedLine[4]);
            
            String hostStateCode = processedLine[1];
            String destStateCode = processedLine[3];
            
            if(graph.getStateHash(hostStateCode) != null && graph.getStateHash(destStateCode) != null) { // Initially, we check if both states exist.
                
                if(graph.getStateHash(hostStateCode).updateBorderDist(destStateCode, distanceKm)) { // If the second state shares a border with the host state, we add the edge weight.
                    // This statement will only execute if the distance was sucessfully updated.
                    // The host state's border with the destination state has its weight(distance) updated.
                    
                    graph.getStateHash(destStateCode).updateBorderDist(hostStateCode, distanceKm);
                    // The destination state's border with the host state has its weight(distance) updated.
                }
            }
            lineReader = bufferReader.readLine();
        }
        
        bufferReader.close();
        reader.close(); // Close the file before reading another.*/
    }
}