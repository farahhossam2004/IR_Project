import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class queries {
    HashMap<String, HashMap<Integer, ArrayList<Integer>>> wordMap;

    static void queryProcessing(ArrayList<String> query) {
        
        ArrayList<String> words = new ArrayList<>();
        
        HashMap<String , Integer> operations = new HashMap<>();

        for (int i = 0; i < query.size(); i++) {
            if (i < query.size() - 1 && query.get(i).equals("and") && query.get(i + 1).equals("not")) {
                operations.put("and not" , i);
                i++; 
            } else if (query.get(i).equals("and")) {
                operations.put("and" , i);
            } else if (query.get(i).equals("or")) {
                operations.put("or" , i);
            } else if (query.get(i).equals("not")) {
                operations.put("not" , i);
            } else {
                words.add(query.get(i));
            }
        }
        
        if(operations.isEmpty()){
            Handlequery(words);
        }else{
            
            HandleOperations(query);
            
        }

    }


    private static void HandleOperations(ArrayList<String> Query){
        ArrayList<String> Stack = new ArrayList<>();
        int counter = 1;
        HashMap<Integer,  HashMap<Integer, Double>> operations = new HashMap<>(); 
        
        for(int i = 0 ; i < Query.size() ; i++){
            if(Query.get(i).equals("and") && Query.get(i).equals("not")){
                
            }else if(Query.get(i).equals("and")){
                Stack.add(Query.get(i+1));
                System.out.println(Stack);
                HashMap<Integer, Double> op = Handlequery(Stack);
                operations.put(counter, op);
                counter++;
                Stack.clear();
                i++;
            }else if(Query.get(i).equals("or")){
                
            }else{
                Stack.add(Query.get(i));
            }
        }

    }


    private static HashMap<Integer, Double> Handlequery(ArrayList<String> words) {
        HashMap<String , Integer> termAndFrequency = CalculateTermFreq(words);
        System.out.println("Term frequency : "+termAndFrequency);
        //==============================================
        HashMap<String, Double> termandWeight = CalculateTermFrequencyWeight(termAndFrequency);
        System.out.println("Term weight : "+termandWeight);
        //==============================================
        HashMap<String, Double> Weight_X_IDF = Calculate_WtermFrequency_X_Idf(termandWeight, Constants.filepath, Constants.numberOfDocuments);
        System.out.println("Tf*idf" +Weight_X_IDF);
        //==============================================
        Double DocLength = CalculateDocLength(Weight_X_IDF);
        System.out.println("Doc Length :" + DocLength);
        //==============================================
        HashMap<String, Double> TermAndNormalization = CalculateTermAndNormalization(Weight_X_IDF, DocLength);
        System.out.println("Normalized: " +TermAndNormalization);
        //===============================================
        HashMap<String, HashMap<Integer, ArrayList<Double>>> FinalProduct = CalculateDocumentProduct(TermAndNormalization);
        System.out.println(FinalProduct);
        //================================================
        HashMap<Integer, Double> BestDocuments = CalculatetheBestDocumentForTheQuery(FinalProduct);
        System.out.println("Relevant Docs : ");
        System.out.println(BestDocuments);

        return BestDocuments;
    }


    static HashMap<String , Integer> CalculateTermFreq(ArrayList<String> words){
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


static HashMap<String, Double> CalculateTermFrequencyWeight(HashMap<String, Integer> TermAndFrequency) {
    HashMap<String, Double> weightedTermFrequency = new HashMap<>();

    for (String term : TermAndFrequency.keySet()) {
        int frequency = TermAndFrequency.get(term);

        double weight = 1 + Math.log(frequency);

        weightedTermFrequency.put(term, weight);
    }
    return weightedTermFrequency;
}

static HashMap<String, Double> Calculate_WtermFrequency_X_Idf(HashMap<String, Double> wordsweight, String FilePath, int noOfDocument) {
    
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


static Double CalculateDocLength(HashMap<String, Double> Weight_X_IDF) {
    Double DocLength = 0.0;

    for (Double value : Weight_X_IDF.values()) {
        DocLength += value * value;
    }

    DocLength = Math.sqrt(DocLength);

    return DocLength;
}


static  HashMap<String, Double> CalculateTermAndNormalization( HashMap<String, Double> Term_and_Tf_idf , double doclength){
    HashMap<String, Double> TermAndNormalization = new HashMap<>();

    for (String term : Term_and_Tf_idf.keySet()) {
        double value = Term_and_Tf_idf.get(term);

        double Normalized_Value = value/doclength;

        TermAndNormalization.put(term, Normalized_Value);
    }

    return TermAndNormalization;
}


static HashMap<String, HashMap<Integer, ArrayList<Double>>> CalculateDocumentProduct(HashMap<String, Double> Normalized_values) {
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

static HashMap<Integer, Double> CalculatetheBestDocumentForTheQuery(HashMap<String, HashMap<Integer, ArrayList<Double>>> finalresults) {
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
    ArrayList<String> query = new ArrayList<>();
            query.add("caeser");
            query.add("and");
            query.add("worser");

            queries.queryProcessing(query);



}

}
