import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class App {
    public static void main(String[] args) throws Exception {

        int NoofDocumens = 10;
        HashMap<String, HashMap<Integer, ArrayList<Integer>>> wordMap;
        HashMap<String, ArrayList<Integer>> termFreq = new HashMap<>();
        HashMap<String, ArrayList<Double>> weightedTermFrq = new HashMap<>();
        HashMap<String, Integer> docFreq = new HashMap<>();
        HashMap<String, Double> idf = new HashMap<>();
        HashMap<String, ArrayList<Double>> termFreqInIDF = new HashMap<>();
        HashMap<Integer, Double> docLength = new HashMap<>();
        HashMap<String, ArrayList<Double>> normalizedTFinIDF = new HashMap<>();
        
        try {
            wordMap = readfile.processFile("D:\\Junior Level\\Data Storage and Retrival(IR)\\IR_Project\\docs\\output2.txt");

            boolean isRunning = true;

            while(isRunning) {

                int choice = Helpers.mainMenu();

                switch (choice) {
                    case 1:
                        termFreq = DocumentsCalculations.Term_frequency(wordMap, NoofDocumens);
                        Helpers.showMatrixInteger(termFreq);
                        break;
                    case 2:
                        if(termFreq.isEmpty()) {
                            termFreq = DocumentsCalculations.Term_frequency(wordMap, NoofDocumens);
                            weightedTermFrq = DocumentsCalculations.Weighted_Term_Frequency(termFreq);
                        }
                        else {
                            weightedTermFrq = DocumentsCalculations.Weighted_Term_Frequency(termFreq);
                        }
                        Helpers.showMatrix(weightedTermFrq);
                        break;
                    case 3:
                        if(termFreq.isEmpty()){
                            termFreq = DocumentsCalculations.Term_frequency(wordMap, NoofDocumens);
                            docFreq = DocumentsCalculations.Document_Frequency(termFreq);
                            idf = DocumentsCalculations.Inverse_Document_Frequency(docFreq, NoofDocumens);
                        }
                        else {
                            docFreq = DocumentsCalculations.Document_Frequency(termFreq);
                            idf = DocumentsCalculations.Inverse_Document_Frequency(docFreq, NoofDocumens);
                        }
                        Helpers.generateDFIDFMatrix(docFreq, idf);
                        break;

                    case 4:
                        if(termFreq.isEmpty() && idf.isEmpty()) {
                            termFreq = DocumentsCalculations.Term_frequency(wordMap, NoofDocumens);
                            docFreq = DocumentsCalculations.Document_Frequency(termFreq);
                            idf = DocumentsCalculations.Inverse_Document_Frequency(docFreq, NoofDocumens);
                        }
                        termFreqInIDF = DocumentsCalculations.computeTFIDF(termFreq, idf);
                        Helpers.showMatrix(termFreqInIDF);

                        break;

                    case 5: 
                        if(termFreqInIDF.isEmpty()) {
                            termFreq = DocumentsCalculations.Term_frequency(wordMap, NoofDocumens);
                            docFreq = DocumentsCalculations.Document_Frequency(termFreq);
                            idf = DocumentsCalculations.Inverse_Document_Frequency(docFreq, NoofDocumens);
                            termFreqInIDF = DocumentsCalculations.computeTFIDF(termFreq, idf);
                        }
                        docLength = DocumentsCalculations.computeDocumentLengths(termFreqInIDF);
                        Helpers.displayDocumentLengths(docLength);
                        
                        break;

                    case 6:
                        if(termFreqInIDF.isEmpty()){
                            termFreq = DocumentsCalculations.Term_frequency(wordMap, NoofDocumens);
                            docFreq = DocumentsCalculations.Document_Frequency(termFreq);
                            idf = DocumentsCalculations.Inverse_Document_Frequency(docFreq, NoofDocumens);
                            termFreqInIDF = DocumentsCalculations.computeTFIDF(termFreq, idf);
                            docLength = DocumentsCalculations.computeDocumentLengths(termFreqInIDF);
                        }
                        normalizedTFinIDF = DocumentsCalculations.computeNormalizedTFIDF(termFreqInIDF, docLength);
                        Helpers.showMatrix(normalizedTFinIDF);
                        break;

                    case 0:
                        isRunning = false;
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }
}
