import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class readfile {
    
    static HashMap<String, HashMap<Integer, ArrayList<Integer>>> wordMap;

    public static HashMap<String, HashMap<Integer, ArrayList<Integer>>> processFile(String filePath) throws IOException {

        HashMap<String, HashMap<Integer, ArrayList<Integer>>> map = new HashMap<>();

        // Read the file line by line
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                
                
                if (parts.length != 2) continue; 

                String word = parts[0]; // the word
                String documentPositions = parts[1]; // rest document positions
                
                HashMap<Integer, ArrayList<Integer>> innerMap = new HashMap<>();

                // Split document positions 
                String[] docEntries = documentPositions.split(";");

                for (String entry : docEntries) {
                    String[] docParts = entry.split(":");

                    
                    if (docParts.length != 2) continue;

                    int docId = Integer.parseInt(docParts[0]);
                    String[] positions = docParts[1].split(",");

                    //positions to integers and add them to the list
                    ArrayList<Integer> positionList = innerMap.getOrDefault(docId, new ArrayList<>());
                    for (String pos : positions) {
                        positionList.add(Integer.parseInt(pos));
                    }

                    innerMap.put(docId, positionList);
                }

                // Add the inner map to the main map
                map.put(word, innerMap);
            }
        }

        return map;
    }
}


