import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class queries {
    HashMap<String, HashMap<Integer, ArrayList<Integer>>> wordMap;

    public static void queryProcessing(ArrayList<String> query) {
        
        ArrayList<String> words = new ArrayList<>();
        
        boolean operator = false;

        for (int i = 0; i < query.size(); i++) {
            if (i < query.size() - 1 && query.get(i).equals("and") && query.get(i + 1).equals("not")) {
                operator = true;
            } else if (query.get(i).equals("and")) {
                operator = true;
            } else if (query.get(i).equals("or")) {
                operator = true;
            } else if (query.get(i).equals("not")) {
                operator= true;
            } else {
                words.add(query.get(i));
            }
        }
        
        if(operator==false){
            Handlequery(words);
        }else{
            HandleOperations(query);
        }

    }


    private static void HandleOperations(ArrayList<String> Query){
        ArrayList<String> Stack = new ArrayList<>();
        // fools fear in and not fools
        for(int i = 0 ; i < Query.size() ; i++){
            // ========================================And Not operator ==================================
            if(Query.get(i).equals("and") && Query.get(i+1).equals("not")){
                
                System.out.println("=========================== And Not Operator =======================");
                HandlequeryForAndNot(Stack, Query.get(i+2));
                i+=2;
            // ====================================== And Operator =======================================
            }else if(Query.get(i).equals("and")){
                Stack.add(Query.get(i+1));
                HashMap<Integer, Double> op = Handlequery(Stack);
                Stack.clear();
                i++;
            // ============================================== Or Operator ================================================
            }else if(Query.get(i).equals("or")){
                ArrayList<String> Stack2 = new ArrayList<>();
                HashMap<Integer, Double> op1 = Handlequery(Stack);
                HashMap<Integer, Double> op2 = new HashMap<>();
                i++;
                boolean anotheroperation = false;
                outerloop : while (i<Query.size()) {
                    if(Query.get(i).equals("and") && Query.get(i+1).equals("not")){
                        op2 = HandlequeryForAndNot(Stack2, Query.get(i+2));
                        i+=2;
                        anotheroperation = true;
                        break outerloop;
                    }else{
                    Stack2.add(Query.get(i));
                    i++;
                    }
                    
                }
                i--;
                if(anotheroperation == false){
                    op2 = Handlequery(Stack2);
                }
                // Merge op1 and op2 into a new HashMap
                HashMap<Integer, Double> op3 = new HashMap<>(op1); // Copy op1
                op2.forEach((k, v) -> op3.merge(k, v, Double::sum)); // Merge values if keys overlap

                System.out.println("All relevant docs are: " + op3);

            }else{
                Stack.add(Query.get(i));
            }
        }

    }

    //
    private static HashMap<Integer, Double> HandlequeryForAndNot(ArrayList<String> words , String word) {
        HashMap<String, Integer> termAndFrequency = CalculateTermFreq(words);
        System.out.println("\n--- Term Frequency ---");
        System.out.printf("%-15s | %-10s\n", "Term", "Frequency");
        System.out.println("-------------------------------");
        termAndFrequency.forEach((term, freq) -> 
            System.out.printf("%-15s | %-10d\n", term, freq)
        );
    
        //==============================================
        HashMap<String, Double> termAndWeight = CalculateTermFrequencyWeight(termAndFrequency);
        System.out.println("\n--- Term Weight ---");
        System.out.printf("%-15s | %-10s\n", "Term", "Weight");
        System.out.println("-------------------------------");
        termAndWeight.forEach((term, weight) -> 
            System.out.printf("%-15s | %-10.4f\n", term, weight)
        );
    
        //==============================================
        HashMap<String, Double> weightXIdf = Calculate_WtermFrequency_X_Idf(termAndWeight, Constants.filepath, Constants.numberOfDocuments);
        System.out.println("\n--- Term TF*IDF ---");
        System.out.printf("%-15s | %-10s\n", "Term", "TF*IDF");
        System.out.println("-------------------------------");
        weightXIdf.forEach((term, tfIdf) -> 
            System.out.printf("%-15s | %-10.4f\n", term, tfIdf)
        );
    
        //==============================================
        Double docLength = CalculateDocLength(weightXIdf);
        System.out.printf("\nDocument Length: %.4f\n", docLength);
    
        //==============================================
        HashMap<String, Double> termAndNormalization = CalculateTermAndNormalization(weightXIdf, docLength);
        System.out.println("\n--- Normalized TF*IDF ---");
        System.out.printf("%-15s | %-10s\n", "Term", "Normalized");
        System.out.println("-------------------------------");
        termAndNormalization.forEach((term, normalized) -> 
            System.out.printf("%-15s | %-10.4f\n", term, normalized)
        );
    
        //===============================================
        HashMap<String, HashMap<Integer, ArrayList<Double>>> finalProduct = CalculateDocumentProductAndNotOperators(termAndNormalization , word);
        System.out.println("\n--- Document Products ---");
        System.out.printf("%-15s | %-10s | %-10s\n", "Term", "Doc ID", "Product");
        System.out.println("-----------------------------------------");
        finalProduct.forEach((term, docMap) -> {
            docMap.forEach((docId, products) -> 
                System.out.printf("%-15s | %-10d | %-10s\n", term, docId, products.toString())
            );
        });
    
        //================================================
        HashMap<Integer, Double> bestDocuments = CalculatetheBestDocumentForTheQuery(finalProduct);
        System.out.println("\n--- Best Documents ---");
        System.out.printf("%-10s | %-10s\n", "Doc ID", "Relevance");
        System.out.println("-----------------------------");
        bestDocuments.forEach((docId, relevance) -> 
            System.out.printf("%-10d | %-10.4f\n", docId, relevance)
        );
    
        return bestDocuments;
    }
    
    //==========================================================
    private static HashMap<Integer, Double> Handlequery(ArrayList<String> words) {
        HashMap<String, Integer> termAndFrequency = CalculateTermFreq(words);
        System.out.println("\n--- Term Frequency ---");
        System.out.printf("%-15s | %-10s\n", "Term", "Frequency");
        System.out.println("-------------------------------");
        termAndFrequency.forEach((term, freq) -> 
            System.out.printf("%-15s | %-10d\n", term, freq)
        );
    
        //==============================================
        HashMap<String, Double> termAndWeight = CalculateTermFrequencyWeight(termAndFrequency);
        System.out.println("\n--- Term Weight ---");
        System.out.printf("%-15s | %-10s\n", "Term", "Weight");
        System.out.println("-------------------------------");
        termAndWeight.forEach((term, weight) -> 
            System.out.printf("%-15s | %-10.4f\n", term, weight)
        );
    
        //==============================================
        HashMap<String, Double> weightXIdf = Calculate_WtermFrequency_X_Idf(termAndWeight, Constants.filepath, Constants.numberOfDocuments);
        System.out.println("\n--- Term TF*IDF ---");
        System.out.printf("%-15s | %-10s\n", "Term", "TF*IDF");
        System.out.println("-------------------------------");
        weightXIdf.forEach((term, tfIdf) -> 
            System.out.printf("%-15s | %-10.4f\n", term, tfIdf)
        );
    
        //==============================================
        Double docLength = CalculateDocLength(weightXIdf);
        System.out.printf("\nDocument Length: %.4f\n", docLength);
    
        //==============================================
        HashMap<String, Double> termAndNormalization = CalculateTermAndNormalization(weightXIdf, docLength);
        System.out.println("\n--- Normalized TF*IDF ---");
        System.out.printf("%-15s | %-10s\n", "Term", "Normalized");
        System.out.println("-------------------------------");
        termAndNormalization.forEach((term, normalized) -> 
            System.out.printf("%-15s | %-10.4f\n", term, normalized)
        );
    
        //===============================================
        HashMap<String, HashMap<Integer, ArrayList<Double>>> finalProduct = CalculateDocumentProduct(termAndNormalization);
        System.out.println("\n--- Document Products ---");
        System.out.printf("%-15s | %-10s | %-10s\n", "Term", "Doc ID", "Product");
        System.out.println("-----------------------------------------");
        finalProduct.forEach((term, docMap) -> {
            docMap.forEach((docId, products) -> 
                System.out.printf("%-15s | %-10d | %-10s\n", term, docId, products.toString())
            );
        });
    
        //================================================
        HashMap<Integer, Double> bestDocuments = CalculatetheBestDocumentForTheQuery(finalProduct);
        System.out.println("\n--- Best Documents ---");
        System.out.printf("%-10s | %-10s\n", "Doc ID", "Relevance");
        System.out.println("-----------------------------");
        bestDocuments.forEach((docId, relevance) -> 
            System.out.printf("%-10d | %-10.4f\n", docId, relevance)
        );
    
        return bestDocuments;
    }


    private static HashMap<String , Integer> CalculateTermFreq(ArrayList<String> words){
        HashMap<String , Integer> termAndFrequency= new HashMap<>(); 
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if (termAndFrequency.containsKey(word)) {
                termAndFrequency.put(word, termAndFrequency.get(word) + 1);
            } else {
                termAndFrequency.put(word, 1);
            }
        }
        return termAndFrequency;
    }


 private static HashMap<String, Double> CalculateTermFrequencyWeight(HashMap<String, Integer> TermAndFrequency) {
    HashMap<String, Double> weightedTermFrequency = new HashMap<>();

    for (String term : TermAndFrequency.keySet()) {
        int frequency = TermAndFrequency.get(term);

        double weight = 1 + Math.log(frequency);

        weightedTermFrequency.put(term, weight);
    }
    return weightedTermFrequency;
}

private static HashMap<String, Double> Calculate_WtermFrequency_X_Idf(HashMap<String, Double> wordsweight, String FilePath, int noOfDocument) {
    
    HashMap<String, Double> TermIdfMultiTermFreqWeight = new HashMap<>();

    try {
        HashMap<String, HashMap<Integer, ArrayList<Integer>>> wordMap = readfile.processFile(FilePath);

        HashMap<String, ArrayList<Integer>> freq = DocumentsCalculations.Term_frequency(wordMap, noOfDocument);

        HashMap<String, Integer> df = DocumentsCalculations.Document_Frequency(freq);

        HashMap<String, Double> idf = DocumentsCalculations.Inverse_Document_Frequency(df, noOfDocument);

        for (String word : wordsweight.keySet()) {
            if (idf.containsKey(word)) {
                double newWeight = wordsweight.get(word) * idf.get(word);

                TermIdfMultiTermFreqWeight.put(word, newWeight);
            }
        }
    } catch (Exception e) {
        System.out.println("Something went wrong: " + e.getMessage());
    }

    return TermIdfMultiTermFreqWeight;
}


private static Double CalculateDocLength(HashMap<String, Double> Weight_X_IDF) {
    Double DocLength = 0.0;

    for (Double value : Weight_X_IDF.values()) {
        DocLength += value * value;
    }

    DocLength = Math.sqrt(DocLength);

    return DocLength;
}


private static  HashMap<String, Double> CalculateTermAndNormalization( HashMap<String, Double> Term_and_Tf_idf , double doclength){
    HashMap<String, Double> TermAndNormalization = new HashMap<>();

    for (String term : Term_and_Tf_idf.keySet()) {
        double value = Term_and_Tf_idf.get(term);

        double Normalized_Value = value/doclength;

        TermAndNormalization.put(term, Normalized_Value);
    }

    return TermAndNormalization;
}


private static HashMap<String, HashMap<Integer, ArrayList<Double>>> CalculateDocumentProduct(HashMap<String, Double> Normalized_values) {
    HashMap<String, HashMap<Integer, ArrayList<Integer>>> wordMap = new HashMap<>();
    HashMap<String, HashMap<Integer, ArrayList<Double>>> WordAndDocsProductValue = new HashMap<>();

    try {
        wordMap = readfile.processFile(Constants.filepath);
        
    } catch (IOException e) {
        System.out.println("Wrong Path");
        e.printStackTrace();
    }

    HashMap<String, ArrayList<Integer>> termFreq = DocumentsCalculations.Term_frequency(wordMap, Constants.numberOfDocuments);
    HashMap<String, Integer> docFreq = DocumentsCalculations.Document_Frequency(termFreq);
    HashMap<String, Double> idf = DocumentsCalculations.Inverse_Document_Frequency(docFreq, Constants.numberOfDocuments);
    HashMap<String, ArrayList<Double>> termFreqInIDF = DocumentsCalculations.computeTFIDF(termFreq, idf);
    HashMap<Integer, Double> docLength = DocumentsCalculations.computeDocumentLengths(termFreqInIDF);
    HashMap<String, ArrayList<Double>> normalizedTFinIDF = DocumentsCalculations.computeNormalizedTFIDF(termFreqInIDF, docLength);

    for (String word : Normalized_values.keySet()) {
        if (normalizedTFinIDF.containsKey(word)) {
            double normalizedValue = Normalized_values.get(word);
            ArrayList<Double> tfIdfValues = normalizedTFinIDF.get(word);
            
            HashMap<Integer, ArrayList<Double>> docProductMap = new HashMap<>();

            for (int docId = 0; docId < tfIdfValues.size(); docId++) {
                double tfIdfValue = tfIdfValues.get(docId);
                double productValue = normalizedValue * tfIdfValue;

                docProductMap.computeIfAbsent(docId+1, k -> new ArrayList<>()).add(productValue);
            }

            WordAndDocsProductValue.put(word, docProductMap);
        }
    }
    return WordAndDocsProductValue;
}

private static HashMap<String, HashMap<Integer, ArrayList<Double>>> CalculateDocumentProductAndNotOperators(HashMap<String, Double> Normalized_values, String wordtodrop) {
    HashMap<String, HashMap<Integer, ArrayList<Integer>>> wordMap = new HashMap<>();
    HashMap<String, HashMap<Integer, ArrayList<Double>>> WordAndDocsProductValue = new HashMap<>();
    ArrayList<Integer> dropedid = new ArrayList<>();
    try {
        wordMap = readfile.processFile(Constants.filepath);
        dropedid = dropDocuFromSearch(wordMap, wordtodrop);
    } catch (IOException e) {
        System.out.println("Wrong Path");
        e.printStackTrace();
    }

    HashMap<String, ArrayList<Integer>> termFreq = DocumentsCalculations.Term_frequency(wordMap, Constants.numberOfDocuments);
    HashMap<String, Integer> docFreq = DocumentsCalculations.Document_Frequency(termFreq);
    HashMap<String, Double> idf = DocumentsCalculations.Inverse_Document_Frequency(docFreq, Constants.numberOfDocuments);
    HashMap<String, ArrayList<Double>> termFreqInIDF = DocumentsCalculations.computeTFIDF(termFreq, idf);
    HashMap<Integer, Double> docLength = DocumentsCalculations.computeDocumentLengths(termFreqInIDF);
    HashMap<String, ArrayList<Double>> normalizedTFinIDF = DocumentsCalculations.computeNormalizedTFIDF(termFreqInIDF, docLength);

    // Loop through normalized values of the query terms
    for (String word : Normalized_values.keySet()) {
        if (normalizedTFinIDF.containsKey(word)) {
            double normalizedValue = Normalized_values.get(word);
            ArrayList<Double> tfIdfValues = normalizedTFinIDF.get(word);
            
            HashMap<Integer, ArrayList<Double>> docProductMap = new HashMap<>();
            System.out.println(dropedid);
            
            // Iterate over the documents and calculate product values
            for (int docId = 0; docId < tfIdfValues.size(); docId++) { // 0-indexed loop
                int documentId = docId + 1;  // Doc ID starts from 1, so adjust here

                if (!dropedid.contains(documentId)) {  // Exclude documents in 'dropedid'
                    double tfIdfValue = tfIdfValues.get(docId);
                    double productValue = normalizedValue * tfIdfValue;

                    // Add the product value to the corresponding document's list
                    docProductMap.computeIfAbsent(documentId, k -> new ArrayList<>()).add(productValue);
                }
            }

            // Put the word and its document product map in the final result
            WordAndDocsProductValue.put(word, docProductMap);
        }
    }
    
    return WordAndDocsProductValue;
}


private static ArrayList<Integer> dropDocuFromSearch(HashMap<String, HashMap<Integer, ArrayList<Integer>>> map, String word) {
    ArrayList<Integer> documentsDropped = new ArrayList<>();

    if (map.containsKey(word)) {
        
        HashMap<Integer, ArrayList<Integer>> innerMap = map.get(word);

        documentsDropped.addAll(innerMap.keySet());
    }

    return documentsDropped;
}



private static HashMap<Integer, Double> CalculatetheBestDocumentForTheQuery(HashMap<String, HashMap<Integer, ArrayList<Double>>> finalresults) {
    HashMap<Integer, Double> documentSums = new HashMap<>(); // To store the sum of values for each document

    // Loop through finalresults to calculate document sums
    for (HashMap<Integer, ArrayList<Double>> docMap : finalresults.values()) {
        for (Map.Entry<Integer, ArrayList<Double>> entry : docMap.entrySet()) {
            int docId = entry.getKey();
            double sum = entry.getValue().stream().mapToDouble(Double::doubleValue).sum();
            documentSums.put(docId, documentSums.getOrDefault(docId, 0.0) + sum);
        }
    }

    // Sort and pick the top 3 documents
    List<Map.Entry<Integer, Double>> sortedEntries = documentSums.entrySet().stream()
        .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())) // Sort by value descending
        .toList(); // Collect to a list

    HashMap<Integer, Double> bestDocuments = new HashMap<>();

    // Retrieve the top 3 entries
    for (int i = 0; i < Math.min(3, sortedEntries.size()); i++) {
        Map.Entry<Integer, Double> entry = sortedEntries.get(i);
        bestDocuments.put(entry.getKey(), entry.getValue());
    }

    return bestDocuments;
}



public static void main(String[] args) {
    //fools fear in and not fools
    // ArrayList<String> query = new ArrayList<>();
            
            // query.add("fools");
            // query.add("fear");
            // query.add("in");
            // query.add("and");
            // query.add("not");
            // query.add("rush");
            // query.add("to");
            // query.add("tread");
            // query.add("where");

            // query.add("antony");
            // query.add("brutus");
            // query.add("caeser");
            // query.add("and");
            // query.add("not");
            // query.add("mercy");
            
            // System.out.println(query);
            // queries.queryProcessing(query);

}

}
