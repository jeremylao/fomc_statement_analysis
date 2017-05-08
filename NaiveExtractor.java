import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
/**
 * Created by yonghong on 4/11/17.
 */
public class NaiveExtractor {

    public static void main (String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("PosTagger takes 1 arguments:  java FeatureBuilder training.directory");
            System.exit(1);
        }

        extract(args[0]);
    }

    private static void extract(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        List<String> results = new ArrayList<>();
        List<String> other = new ArrayList<>();
        Map<String, List<List<String>>> fileContents = new TreeMap<>();
        File[] files = directory.listFiles();
        Arrays.sort(files);
        for (int i = 0; i < files.length; i++) {
            File statement = files[i];
            List<String> lines = Files.readAllLines(statement.toPath(), StandardCharsets.UTF_8);
            List<String> words = new ArrayList<>();
            List<List<String>> sentences = new ArrayList<>();
            String fileName = statement.getName().substring(0, statement.getName().indexOf("."));
            System.out.println("Processing file " + fileName);

            for (int j = 0; j < lines.size(); j++) {
                String word = lines.get(j).trim();
                if (word.equals("")) {
                    sentences.add(words);
                    words = new ArrayList<>();
                } else {
                    words.add(word);
                }
            }

            // process the last sentence of words
            if (!words.isEmpty()) {
                sentences.add(words);
            }

            fileContents.put(fileName, sentences);
        }

        extractFundsRate(fileContents, "federalFundsRate.txt");
        extractVotingNames(fileContents, "votingNames.txt");
        extractInflationStatus(fileContents, "inflationStatus.txt");
    }

    private static void extractInflationStatus(Map<String, List<List<String>>> files, String outputFile) throws IOException {
        List<String> results = new ArrayList<>();

        File deflationFile = new File("deflationWords.txt");
        File inflationFile = new File("inflationWords.txt");
        List<String> deflationWords = Files.readAllLines(deflationFile.toPath(), StandardCharsets.UTF_8);
        List<String> inflationWords = Files.readAllLines(inflationFile.toPath(), StandardCharsets.UTF_8);

        Stemmer stemmer = new Stemmer();

        for (int i = 0; i < deflationWords.size(); i++)
            deflationWords.set(i, stemmer.stem(deflationWords.get(i)));

        for (int i = 0; i < inflationWords.size(); i++)
            inflationWords.set(i, stemmer.stem(inflationWords.get(i)));

        Set<String> uniqueDeflationWords = new HashSet<>(deflationWords);
        Set<String> uniqueInflationWords = new HashSet<>(inflationWords);

        for (String fileName : files.keySet()) {
            List<List<String>> sentences = files.get(fileName);
            results.add(fileName);
            int numInflation = 0, numDeflation = 0;
            for (List<String> words : sentences) {
                boolean isTargetSentence = false;
                StringBuffer result = new StringBuffer();
                StringBuffer sentence = new StringBuffer();

                int feedback = 0;
                for (int i = 0; i < words.size(); i++) {
                    if (!words.get(i).matches("[^A-Za-z0-9]"))
                        sentence.append(" ");
                    sentence.append(words.get(i));

                    String stemmedWord = stemmer.stem(words.get(i));
                    if (uniqueDeflationWords.contains(stemmedWord)) {
                        sentence.append("(deflation)");
                        feedback -= 1;
                    }
                    if (uniqueInflationWords.contains(stemmedWord)) {
                        sentence.append("(inflation)");
                        feedback += 1;
                    }

                    if (!isTargetSentence && i + 1 < words.size()) {
                        if (words.get(i).toLowerCase().equals("inflation")) {
                            isTargetSentence = true;
                        }
                    }
                }

                if (isTargetSentence) {
                    if (feedback > 0) {
                        result.append("Inflation");
                        numInflation++;
                    }
                    else if (feedback < 0) {
                        result.append("Deflation");
                        numDeflation++;
                    }
                    else
                        result.append("None     ");
                    result.append(" -").append(sentence);

                    results.add(result.toString());
                }
            }
            if (numDeflation > numInflation)
                results.add("----------- deflation more overall");
            else if (numDeflation < numInflation)
                results.add("----------- inflation more overall");
            else
                results.add("----------- remained stable");

            // add an empty line between results from each file
            results.add("");
        }

        writeTofiles(results, outputFile);
    }

    private static void extractVotingNames(Map<String, List<List<String>>> files, String outputFile) {
        Map<String, HashSet<String>> votingNamesMap = new TreeMap<>();
        Set<String> uniqueVotingNames = new HashSet<>();

        for (String fileName : files.keySet()) {
            votingNamesMap.put(fileName, new HashSet<>());
            List<List<String>> sentences = files.get(fileName);

            for (List<String> words : sentences) {
                boolean isTargetSentence = false;
                boolean hasNumbers = false;
                StringBuffer result = new StringBuffer();
                StringBuffer sentence = new StringBuffer();
                List<String> votingNames = new ArrayList<>();

                for (int i = 0; i < words.size(); i++) {
                    if (!words.get(i).matches("[^A-Za-z0-9]"))
                        sentence.append(" ");
                    sentence.append(words.get(i));

                    if (!isTargetSentence && i + 1 < words.size()) {
                        if (words.get(i).equals("Voting") && words.get(i+1).equals("for")) {
                            isTargetSentence = true;
                        }
                    }
                }

                if (isTargetSentence) {
                    String nameSentence = sentence.substring(sentence.indexOf(":") + 1).trim();
                    String[] names = nameSentence.split(";");
                    for (String name : names) {
                        name = name.split(",")[0].trim();
                        votingNames.add(name);
                    }
                    // the last name pattern of the sentence is "and [name].", should remove it
                    String name = votingNames.get(votingNames.size()-1);
                    name = name.substring(name.indexOf(' ') + 1, name.length() - 1);
                    votingNames.set(votingNames.size() - 1, name);
                }
                votingNamesMap.get(fileName).addAll(votingNames);
            }
            uniqueVotingNames.addAll(votingNamesMap.get(fileName));
        }

        List<String> results = formatVotingNameData(uniqueVotingNames, votingNamesMap);
        writeTofiles(results, outputFile);
    }

    private static List<String> formatVotingNameData(Set<String> uniqueVotingNames, Map<String, HashSet<String>> votingNamesMap) {
        List<String> results = new ArrayList<>();
        List<String> votingNames = new ArrayList<>();
        votingNames.addAll(uniqueVotingNames);

        StringBuffer result = new StringBuffer();
        result.append("[");
        for (String name : votingNames) {
            result.append("'").append(name).append("'").append(", ");
        }
        result.append("{ role: 'annotation' } ],");
        results.add(result.toString());

        for (String fileName : votingNamesMap.keySet()) {
            result = new StringBuffer();
            result.append("['").append(fileName).append("', ");
            HashSet<String> namesInEachFile = votingNamesMap.get(fileName);

            for (String name : votingNames) {
                if (namesInEachFile.contains(name))
                    result.append("1, ");
                else
                    result.append("0, ");
            }
            result.append("''],");

            results.add(result.toString());
        }

        return results;
    }
    private static void extractFundsRate(Map<String, List<List<String>>> files, String outputFile) {
        List<String> results = new ArrayList<>();

        for (String fileName : files.keySet()) {
            List<List<String>> sentences = files.get(fileName);

            for (List<String> words : sentences) {
                boolean isTargetSentence = false;
                boolean hasNumbers = false;
                StringBuffer result = new StringBuffer();
                StringBuffer sentence = new StringBuffer();
                String keyword = "";
                String rangeBegin = "";
                String rangeEnd = "";

                for (int i = 0; i < words.size(); i++) {
                    sentence.append(" " + words.get(i));

                    if (!isTargetSentence && i + 2 < words.size()) {
                        if (words.get(i).equals("federal") && words.get(i + 1).equals("funds") && words.get(i + 2).equals("rate")) {
                            keyword = words.get(i) + " " + words.get(i + 1) + " " + words.get(i + 2);
                            isTargetSentence = true;
                        }
                    }

                    if (!hasNumbers && i + 2 < words.size()) {
                        if (isNumber(words.get(i)) && words.get(i + 1).equals("to") && isNumber(words.get(i + 2))) {
                            rangeBegin = words.get(i);
                            rangeEnd = words.get(i + 2);
                            hasNumbers = true;
                        }
                    }
                }

                Set<String> uniqueWords = new HashSet<>();
                uniqueWords.addAll(words);
                // ensure Committee perform this action
                if (isTargetSentence && hasNumbers && uniqueWords.contains("Committee")) {
                    result.append(formatFederalFundsRateOutput(fileName, rangeBegin, rangeEnd));
                }

                if (!result.toString().isEmpty()) {
                    results.add(result.toString());
                }
            }
            results.add("");
        }

        writeTofiles(results, outputFile);
    }

    // form output to show graph in Google Chart API
    private static String formatFederalFundsRateOutput(String fileName, String rangeBegin, String rangeEnd) {
        StringBuffer sb = new StringBuffer();
        sb.append("['").append(fileName).append("', ")
                .append(rangeBegin).append(", ")
                .append(rangeBegin).append(", ")
                .append(rangeEnd).append(", ")
                .append(rangeEnd).append("],");
        return sb.toString();
    }

    private static boolean isNumber(String value) {
        return value.matches("(\\d+[/]\\d+)|(\\d+)");
    }

    private static void writeTofiles (List<String> results, String fileName) {
        // print results to file
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(fileName, false));

        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (String result : results)
            sb.append(result + "\n");

        pw.append(sb.toString());
        pw.close();
    }
}
