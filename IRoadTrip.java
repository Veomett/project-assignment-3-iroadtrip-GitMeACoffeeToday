import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class IRoadTrip {
    
    static String DATE = "2020-12-31";
    
    Graph graph = new Graph();
    


    public IRoadTrip (String [] args) throws IOException { // Constructor
        // Replace with your code
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
                    //System.out.println(p.split("-->")[1].substring(p.split("-->")[1].indexOf("(")+1, p.split("-->")[1].indexOf(")")));
                    totalDist += Integer.parseInt(p.split("-->")[1].substring(p.split("-->")[1].indexOf("(")+1, p.split("-->")[1].indexOf("k")));
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
        
        ArrayList<Route> routes = new ArrayList<Route>();
        
        int smallestBorderDist = -1;
        int smallestCountryId;
        int prevStateId;
        
        int currrentCountryId = graph.getStateHash(graph.getStateHashId(country1)).getId();
        int destinationCountryId = graph.getStateHash(graph.getStateHashId(country2)).getId();
        
        //visitedStatesId.add(currrentCountryId);
        
        for(int i = 0; i < graph.getStateHash(graph.getStateHashId(country1)).getBorderSize(); i++) { // Adds all bordering states to the unvisited.
            unvisitedStatesId.add(graph.getStateHash(graph.getStateHashId(country1)).getBorderState(i).getState().getId());
        }
        routes.add(new Route(-1, currrentCountryId, 0)); // First source state from itself to itself. Has no previous state. Distance 0.
        
        
        // Either the unvisited states id list is empty or the next new route ends with the destination.
        while(!unvisitedStatesId.isEmpty()) { // While there are adjacent states to check...
            
            if(!visitedStatesId.contains(currrentCountryId)) { // If the current country has not been visited...
                visitedStatesId.add(currrentCountryId);
            }
            
            if(currrentCountryId == -1) { // We backed up to the source country, meaning there is no path. Break.
                break;
            }
            
            prevStateId = currrentCountryId;
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
                    //routes.add(new Route(currrentCountryId, smallestCountryId, routes.get(routes.size()-1).distanceSoFar+smallestBorderDist));
                    routes.add(new Route(currrentCountryId, smallestCountryId, smallestBorderDist));
                    // For distance, consider the distance from the last route along with the distance of this current smallestBorderDist.
                    break;
                }
                else {
                    //routes.add(new Route(currrentCountryId, smallestCountryId, routes.get(routes.size()-1).distanceSoFar+smallestBorderDist)); // Add the route normally.
                    routes.add(new Route(currrentCountryId, smallestCountryId, smallestBorderDist));
                    currrentCountryId = smallestCountryId; // Set the current country to the next.
                    unvisitedStatesId = new ArrayList<Integer>(); // Clear unvisited states...
                    
                    for(int i = 0; i < graph.getStateHash(currrentCountryId).getBorderSize(); i++) { // Adds all of the new bordering states to the unvisited.
                        unvisitedStatesId.add(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId());
                    }
                }
            }
            
            else { // We have no borders (unvisited) to go to. (Dead end)
                for(Route r: routes) {
                    if(r.getCurrStateID() == currrentCountryId) {
                        
                        currrentCountryId = r.getPrevStateID(); // Go back to the previous state.
                        if(currrentCountryId == -1) { // We are stuck at the starting country. no path. Break!
                            break;
                        }
                        
                        unvisitedStatesId = new ArrayList<Integer>(); // Clear unvisited states...
                        
                        for(int i = 0; i < graph.getStateHash(currrentCountryId).getBorderSize(); i++) { // Adds all of the new bordering states to the unvisited.
                            unvisitedStatesId.add(graph.getStateHash(currrentCountryId).getBorderState(i).getState().getId());
                        }
                        //System.out.println("Going back to " + graph.getStateHash(currrentCountryId).getOfficialName());
                        routes.remove(r);
                        break;
                    }
                }
            }
        }
        
        
        /*
        for(Route r: routes) {
            if(r.getPrevStateID() == -1) { // First node..
                System.out.println("START" + " -> ");
            }
            else {
                System.out.println(graph.getStateHash(r.getPrevStateID()).getOfficialName() + " -> " + graph.getStateHash(r.getCurrStateID()).getOfficialName() + " " + r.distanceSoFar + "km");
            }
        }*/
        
        
        if(routes.size() == 1) { // Only source node in the route.
            System.out.println("No path found.");
            return new ArrayList<String>();
        }
        else {
            ArrayList<String> finalizedPath = new ArrayList<String>();
            for(Route r: routes) {
                if(r.getPrevStateID() == -1) {
                    continue;
                }
                finalizedPath.add(graph.getStateHash(r.getPrevStateID()).getOfficialName() + " --> " + graph.getStateHash(r.getCurrStateID()).getOfficialName()
                        + "(" + r.distanceSoFar + "km.)");
                //System.out.println(graph.getStateHash(r.getPrevStateID()).getOfficialName() + " --> " + graph.getStateHash(r.getCurrStateID()).getOfficialName()
                //      + " (" + r.distanceSoFar + "km.)");
                
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
           System.out.println("The distance between " + graph.getState(graph.findStateIndex(country1)).getOfficialName() + " and " 
           + graph.getState(graph.findStateIndex(country2)).getOfficialName() + " is " + getDistance(country1, country2) + "km.\n");
           
           
           for(String p: findPath(country1, country2)) {
               System.out.println(p);
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
