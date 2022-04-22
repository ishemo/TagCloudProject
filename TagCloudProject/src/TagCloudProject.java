import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Takes in the path to a TXT file and outputs an HTML page to another user
 * provided path. The HTML file displays a tag cloud consisting of words in the
 * provided TXT file.
 *
 * @author Ian Shemo
 */
public final class TagCloudProject {

    /**
     * Default constructor--private to prevent instantiation.
     */
    private TagCloudProject() {
    }

    /**
     * Variable for font size calculation.
     */
    private static final int SIZEMAX = 48;

    /**
     * Variable for font size calculation.
     */
    private static final int SIZEMIN = 11;

    /**
     * Compare {@code String}s in alphabetical order ignoring case.
     */
    private static class AlphabeticalOrder
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> p1,
                Map.Entry<String, Integer> p2) {
            //words are all lower case so do not have to consider case
            int check = p1.getKey().compareTo(p2.getKey());
            //prevents comparator returning 0 if .equals would not return 0
            if (check == 0) {
                check = p1.getValue().compareTo(p2.getValue());
            }
            return check;
        }
    }

    /**
     * Compare {@code Integer}s in descending order.
     */
    private static class DescendingOrder
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> p1,
                Map.Entry<String, Integer> p2) {
            int check = p2.getValue().compareTo(p1.getValue());
            //prevents comparator returning 0 if .equals would not return 0
            if (check == 0) {
                check = p2.getKey().compareTo(p1.getKey());
            }
            return check;
        }
    }

    /**
     * Returns a map of (word, word count) entries from the TXT file.
     *
     * @param fromFile
     *            BufferedReader linked to input file
     * @return Map(word, word count)
     * @requires fromFile is open and is linked to a valid input file
     * @ensures mapOfWordCounts = Map(words in input file, amount of times each
     *          word appears in the file)
     */
    private static Map<String, Integer> mapOfWordCounts(
            BufferedReader fromFile) {
        //common word separators
        String separators = " \t\n\r\",-.!?'[];:/()*`";

        //map of containing words and corresponding word counts
        Map<String, Integer> wordCount = new HashMap<>();

        //add words and word counts to wordCount
        try {
            //loop through every line of input file
            String line = fromFile.readLine();
            while (line != null) {
                int startInd = 0;

                //loop variables
                int len = line.length();
                int i = 0;
                while (i < len) {
                    //if char is not a separator
                    if (separators.indexOf(line.charAt(i)) < 0) {
                        //first letter of word
                        startInd = i;
                        //find last letter of word
                        while (i < len
                                && separators.indexOf(line.charAt(i)) < 0) {
                            i++;
                        }
                        String wCouldBeUpper = line.substring(startInd, i);
                        String word = wCouldBeUpper.toLowerCase();

                        //increase corresponding word count for the word in the map
                        if (wordCount.containsKey(word)) {
                            int count = wordCount.remove(word);
                            wordCount.put(word, count + 1);
                        } else { //add to map if first occurrence of word
                            wordCount.put(word, 1);
                        }
                    } else {
                        i++;
                    }
                }
                line = fromFile.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error reading input file");
        }
        return wordCount;
    }

    /**
     * Returns a List consisting of the user defined number of entries in
     * descending numerical and alphabetical order.
     *
     * @param m
     *            Map containing words and their word counts
     * @param desiredCount
     *            user defined value for number of words to be in the cloud
     * @return List with words and counts in alphabetical order that has the
     *         correct length according to user input
     * @requires m is not null and desired count is between 0 and the number of
     *           words in m.
     * @ensures getSortedList = List with correct number of words in
     *          alphabetical order and their corresponding word counts.
     */
    private static List<Map.Entry<String, Integer>> getSortedList(
            Map<String, Integer> m, int desiredCount) {
        //list to hold pairs of words and counts
        List<Map.Entry<String, Integer>> tempList = new ArrayList<>();

        //add entries to list
        Set<Map.Entry<String, Integer>> entrySet = m.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet) {
            tempList.add(entry);
        }

        //sort entries in descending order
        Comparator<Map.Entry<String, Integer>> descend = new DescendingOrder();
        tempList.sort(descend);

        //sort a list of only the desired amount of entries in alphabetical order
        List<Map.Entry<String, Integer>> finalList = tempList.subList(0,
                desiredCount);
        Comparator<Map.Entry<String, Integer>> alpha = new AlphabeticalOrder();
        finalList.sort(alpha);
        return finalList;
    }

    /**
     * Produces an HTML file which contains the tag cloud.
     *
     *
     * @param entries
     *            List containing entries of (word, word counts)
     * @param toPage
     *            BufferedWriter linked to output file
     * @param fileName
     *            path to user defined output file
     * @param num
     *            user defined number of words to be in tag cloud
     * @requires Entries contains correct amount of words and corresponding word
     *           count. toPage is open and is linked to a valid output file.
     *           Filename is the file name provided by the user that ends with
     *           ".html". num is between 0 and the number of words in m.
     *
     * @ensures output file is a properly formatted HTML document in the desired
     *          output location displaying a tagCloud
     */
    private static void createHTMLPage(List<Map.Entry<String, Integer>> entries,
            BufferedWriter toPage, String fileName, int num) {
        try {
            //beginning tags
            toPage.write("<html>\n");
            toPage.write("\t<head>\n");
            toPage.write("\t\t<title>" + num + " words in " + fileName
                    + "</title>\n");
            toPage.write(
                    "\t\t<link href=\"http://web.cse.ohio-state.edu/software/"
                            + "2231/web-sw2/assignments/projects/tag-cloud-generator"
                            + "/data/tagcloud."
                            + "css\" rel=\"stylesheet\" type=\"text/css\">\n");
            toPage.write("\t\t<link href=\"data/tagcloud.css\" rel="
                    + "\"stylesheet\" type=\"text/css\">\n");
            toPage.write("\t</head>\n");
            toPage.write("\t<body>\n");
            toPage.write(
                    "\t\t<h2>Top " + num + " words in " + fileName + "</h2>\n");
            toPage.write("\t\t<hr>\n");
            toPage.write("\t\t<div class=\"cdiv\">\n");
            toPage.write("\t\t\t<p class=\"cbox\">\n");

            //get minimum and max number of occurrences
            double max = Integer.MIN_VALUE;
            double min = Integer.MAX_VALUE;
            for (Map.Entry<String, Integer> entry : entries) {
                int count = entry.getValue();
                if (count > max) {
                    max = count;
                }
                if (count < min) {
                    min = count;
                }
            }

            //add words with their corresponding font size to HTML file
            ListIterator<Map.Entry<String, Integer>> it = entries
                    .listIterator();
            while (it.hasNext()) {
                Map.Entry<String, Integer> entry = it.next();
                int occ = entry.getValue();

                //gives value between 11 and 48 inclusive
                double fSize = SIZEMIN;
                if (max != min) {
                    fSize = (occ - min) / (max - min);
                    fSize = fSize * (SIZEMAX - SIZEMIN) + SIZEMIN;
                }
                String word = entry.getKey();

                //print element to file
                toPage.write("\t\t\t\t<span style=\"cursor:default\" class=\"f"
                        + (int) fSize + "\" title=\"count: " + occ + "\">"
                        + word + "</span>\n");
            }

            //ending tags
            toPage.write("\t\t\t</p>\n");
            toPage.write("\t\t</div>\n");
            toPage.write("\t</body>\n");
            toPage.write("</html>\n");
        } catch (IOException e) {
            System.err.println("Error writing to output file");
        }
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        //Scanner to get input from console
        Scanner fromConsole = new Scanner(System.in);

        //get input file path
        System.out.println("Enter the name of the input file (include .txt): ");
        String pathToInput = fromConsole.nextLine();

        //linked to input TXT file
        BufferedReader fromInput;
        try {
            fromInput = new BufferedReader(new FileReader(pathToInput));
        } catch (IOException e) {
            System.err.println("Error opening input file");

            //close to prevent resource leak
            fromConsole.close();
            return;
        }

        //get output file path
        System.out
                .println("Enter the name of the output file (inlcude .html): ");
        String pathToOutput = fromConsole.nextLine();

        //linked to output HTML file
        BufferedWriter toFile;
        try {
            toFile = new BufferedWriter(new FileWriter(pathToOutput));
        } catch (IOException e) {
            System.err.println("Error opening the output file");

            //close other I/O to prevent resource leak
            fromConsole.close();
            try {
                fromInput.close();
            } catch (IOException ex) {
                System.err.println("Error closing input file");
            }
            return;
        }

        //get map of words and word counts
        Map<String, Integer> wordsMap = mapOfWordCounts(fromInput);
        int mapSize = wordsMap.size();

        //get cloud size input until non-negative and less than wordsMap size
        int numOfWords = -1;
        while (numOfWords < 0 || numOfWords > mapSize) {
            System.out.println(
                    "Enter a positive integer for the number of words to be "
                            + "included in the tag cloud");
            numOfWords = fromConsole.nextInt();
            if (numOfWords > mapSize) {
                System.out.println("Your input size is greater than the"
                        + " number of unique words in the input file.");
            }
        }
        List<Map.Entry<String, Integer>> wordList = getSortedList(wordsMap,
                numOfWords);

        //createHTMLPage
        createHTMLPage(wordList, toFile, pathToInput, numOfWords);

        //close input and output streams
        fromConsole.close();
        try {
            fromInput.close();
        } catch (IOException e) {
            System.err.println("Error closing input file");
        }
        try {
            toFile.close();
        } catch (IOException e) {
            System.err.println("Error closing output file");
        }
    }
}
