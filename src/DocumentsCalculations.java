import java.util.ArrayList;
import java.util.HashMap;

public class DocumentsCalculations {

    static HashMap<String, ArrayList<Integer>> Term_frequency(HashMap<String, HashMap<Integer, ArrayList<Integer>>> map,
            int NoofDocument) {

        HashMap<String, ArrayList<Integer>> result = new HashMap<>();

        for (String word : map.keySet()) {
            // Initialize an array list for the current word
            ArrayList<Integer> frequencyList = new ArrayList<>();

            HashMap<Integer, ArrayList<Integer>> innerMap = map.get(word);

            // Check each document ID from 1 to NoofDocument
            for (int docId = 1; docId <= NoofDocument; docId++) {
                // If the document ID exists in the inner map, count how many times the word
                // appears in that document
                if (innerMap.containsKey(docId)) {
                    ArrayList<Integer> positions = innerMap.get(docId);

                    frequencyList.add(positions.size());
                } else {
                    // word does not appear add 0
                    frequencyList.add(0);
                }
            }
            result.put(word, frequencyList);
        }

        return result;
    }

    static HashMap<String, ArrayList<Double>> Weighted_Term_Frequency(
            HashMap<String, ArrayList<Integer>> termFrequency) {

        HashMap<String, ArrayList<Double>> weightedResult = new HashMap<>();

        for (String word : termFrequency.keySet()) {

            ArrayList<Double> weightedFrequencyList = new ArrayList<>();

            ArrayList<Integer> frequencyList = termFrequency.get(word);

            for (Integer tf : frequencyList) {
                double weightedTf = (tf > 0) ? 1 + Math.log(tf) : 0;
                weightedFrequencyList.add(weightedTf);
            }

            weightedResult.put(word, weightedFrequencyList);
        }
        return weightedResult;
    }

    static HashMap<String, Integer> Document_Frequency(HashMap<String, ArrayList<Integer>> termFrequency) {
        // Initialize a new HashMap to store the document frequency of each word
        HashMap<String, Integer> documentFrequency = new HashMap<>();

        // Iterate over each word in the term frequency map
        for (String word : termFrequency.keySet()) {
            // Get the list of term frequencies for the current word
            ArrayList<Integer> frequencyList = termFrequency.get(word);

            // Count how many documents contain the word (i.e., tf > 0)
            int count = 0;
            for (Integer tf : frequencyList) {
                if (tf > 0) {
                    count++;
                }
            }

            // Add the document frequency to the result map
            documentFrequency.put(word, count);
        }

        return documentFrequency;
    }

    static HashMap<String, Double> Inverse_Document_Frequency(HashMap<String, Integer> documentFrequency,
            int totalDocuments) {
        // Initialize a new HashMap to store the IDF for each word
        HashMap<String, Double> idfMap = new HashMap<>();

        // Iterate over each word in the document frequency map
        for (String word : documentFrequency.keySet()) {
            // Get the document frequency for the current word
            int df = documentFrequency.get(word);

            // Calculate the IDF using the formula
            double idf = Math.log10((double) totalDocuments / df);

            // Store the result in the IDF map
            idfMap.put(word, idf);
        }

        return idfMap;
    }

    static HashMap<String, ArrayList<Double>> computeTFIDF(
            HashMap<String, ArrayList<Integer>> termFrequency,
            HashMap<String, Double> idfMap) {

        // Initialize the result map to store TF-IDF values
        HashMap<String, ArrayList<Double>> tfidfMatrix = new HashMap<>();

        // Iterate over each word in the termFrequency map
        for (String word : termFrequency.keySet()) {
            // Get the term frequency list for the word
            ArrayList<Integer> tfList = termFrequency.get(word);

            // Get the IDF value for the word
            double idf = idfMap.get(word);

            // Initialize a list to store TF-IDF values for this word
            ArrayList<Double> tfidfList = new ArrayList<>();

            // Calculate TF-IDF for each document
            for (int tf : tfList) {
                // If TF > 0, apply TF-IDF formula; else, set TF-IDF to 0
                double tfidf = (tf > 0) ? tf * idf : 0.0;
                tfidfList.add(tfidf);
            }

            // Add the TF-IDF list to the result map
            tfidfMatrix.put(word, tfidfList);
        }

        return tfidfMatrix;
    }

    static HashMap<Integer, Double> computeDocumentLengths(HashMap<String, ArrayList<Double>> tfIdf) {
        // Initialize a map to store the document lengths
        HashMap<Integer, Double> documentLengths = new HashMap<>();

        // Determine the number of documents
        int numberOfDocuments = tfIdf.values().iterator().next().size();

        // Initialize the lengths to 0
        for (int docId = 1; docId <= numberOfDocuments; docId++) {
            documentLengths.put(docId, 0.0);
        }

        // Iterate through each term and its tf-idf list
        for (ArrayList<Double> tfIdfValues : tfIdf.values()) {
            for (int docId = 0; docId < tfIdfValues.size(); docId++) {
                // Add the square of the tf-idf value to the corresponding document length
                double currentLength = documentLengths.get(docId + 1);
                documentLengths.put(docId + 1, currentLength + Math.pow(tfIdfValues.get(docId), 2));
            }
        }

        // Take the square root of the sums to get the final lengths
        for (int docId = 1; docId <= numberOfDocuments; docId++) {
            documentLengths.put(docId, Math.sqrt(documentLengths.get(docId)));
        }

        return documentLengths;
    }

    static HashMap<String, ArrayList<Double>> computeNormalizedTFIDF(
            HashMap<String, ArrayList<Double>> tfidfValues,
            HashMap<Integer, Double> documentLengths) {

        // Initialize a HashMap to store the normalized TF-IDF values
        HashMap<String, ArrayList<Double>> normalizedTFIDF = new HashMap<>();

        // Iterate through each term in the TF-IDF map
        for (String term : tfidfValues.keySet()) {
            ArrayList<Double> termTfidfValues = tfidfValues.get(term);
            ArrayList<Double> normalizedValues = new ArrayList<>();

            // Normalize each TF-IDF value by dividing by the corresponding document length
            for (int docId = 0; docId < termTfidfValues.size(); docId++) {
                double tfidf = termTfidfValues.get(docId);
                double docLength = documentLengths.get(docId + 1); // Document IDs start from 1

                // Avoid division by zero
                double normalizedValue = (docLength != 0) ? (tfidf / docLength) : 0.0;

                // Round to 7 decimal places
                normalizedValue = Math.round(normalizedValue * 1_000_000.0) / 1_000_000.0;

                normalizedValues.add(normalizedValue);
            }

            // Add the normalized TF-IDF values for the term to the result map
            normalizedTFIDF.put(term, normalizedValues);
        }

        return normalizedTFIDF;
    }
}
