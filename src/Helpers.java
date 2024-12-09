import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Helpers {

    static Scanner input = new Scanner(System.in);

    static int mainMenu() {
        int choice;
        System.out.println("\n============Main Menu=============");
        System.out.println("1- Display Term Frequency Matrix");
        System.out.println("2- Display Weightened Term Frequency Matrix");
        System.out.println("3- Display DF and IDF Matrix");
        System.out.println("4- Display TF * IDF Matrix ");
        System.out.println("5- Display Documemt length Matrix");
        System.out.println("6- Display Normalized TF * IDF");
        System.out.println("0- Exit");
        System.out.print("\nEnter your choice: ");
        choice = input.nextInt();
        return choice;
    }

    static void showMatrix(HashMap<String, ArrayList<Double>> termFrequency) {
        int numberOfDocuments = termFrequency.values().iterator().next().size();

        // Calculate the maximum width for words and values for proper alignment
        int maxWordLength = 15; // Fixed column width for terms
        int columnWidth = 12; // Fixed column width for values

        // Print table header
        System.out.printf("%-" + maxWordLength + "s", "Term");
        for (int docId = 1; docId <= numberOfDocuments; docId++) {
            System.out.printf("%" + columnWidth + "s", "d" + docId);
        }
        System.out.println();

        // Print each term and its frequencies
        for (String word : termFrequency.keySet()) {
            // Print the term
            System.out.printf("%-" + maxWordLength + "s", word);

            // Print the frequencies for the term
            ArrayList<Double> frequencies = termFrequency.get(word);
            for (double frequency : frequencies) {
                System.out.printf("%" + columnWidth + ".7f", frequency); // Round to 7 decimal places
            }
            System.out.println();
        }
    }

    static void showMatrixInteger(HashMap<String, ArrayList<Integer>> termFrequency) {
        int numberOfDocuments = termFrequency.values().iterator().next().size();

        // Calculate the maximum width for words and values for proper alignment
        int maxWordLength = 15; // Fixed column width for terms
        int columnWidth = 12; // Fixed column width for values

        // Print table header
        System.out.printf("\n%-" + maxWordLength + "s", "Term");
        for (int docId = 1; docId <= numberOfDocuments; docId++) {
            System.out.printf("%" + columnWidth + "s", "d" + docId);
        }
        System.out.println("\n");

        // Print each term and its frequencies
        for (String word : termFrequency.keySet()) {
            // Print the term
            System.out.printf("%-" + maxWordLength + "s", word);

            // Print the frequencies for the term
            ArrayList<Integer> frequencies = termFrequency.get(word);
            for (int frequency : frequencies) {
                System.out.printf("%" + columnWidth + "d", frequency); // Format as integer
            }
            System.out.println();
        }
    }

    static void generateDFIDFMatrix(HashMap<String, Integer> documentFrequency, HashMap<String, Double> idfMap) {
        // Print the header row
        System.out.printf("%-15s %-10s %-10s%n", "Word", "DF", "IDF");

        // Iterate over each word in the document frequency map
        for (String word : documentFrequency.keySet()) {
            // Get the DF and IDF for the current word
            int df = documentFrequency.get(word);
            double idf = idfMap.get(word);

            // Print the word, DF, and IDF in matrix format
            System.out.printf("%-15s %-10d %-10.4f%n", word, df, idf);
        }
    }

    static void displayDocumentLengths(HashMap<Integer, Double> documentLengths) {
        System.out.println("Document Lengths:");
        for (int docId : documentLengths.keySet()) {
            System.out.printf("d%d length: %.6f\n", docId, documentLengths.get(docId));
        }
    }

}
