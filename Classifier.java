import java.io.*;
import java.util.*;

// This object represents an text classifier. it takes in a group of text and predicts a lablel.
public class Classifier {

    private ClassifierNode overallRoot;

    // Behavior:
    // - This method creates a classifier decsion tree with already trained data.
    // Parameters:
    // - input: the previous trained data thats being used to create this classifier.
    // Exceptions:
    // - If input is null throw illegalArgument.
    // - If after processing the input the tree is empty throw illegal state exception.
    public Classifier(Scanner input) {
        if (input == null) {
            throw new IllegalArgumentException("input cant be null");
        }

        overallRoot = createTree(input);

        if (overallRoot == null) {
            throw new IllegalStateException("Root cant be null after processing");
        }
    }

    // Behavior:
    // - A helper method for the scanner Consturctior method. this method creates a
    //   decision tree with the input scanner data.
    // Parameters:
    // - input: the previous trained data thats being used to create the decsion tree.
    // Returns:
    // - ClassifierNode: The root of the constructed tree. if input is empty returns null.
    private ClassifierNode createTree(Scanner input) {
        if (!input.hasNextLine()) {
            return null;
        }

        String line = input.nextLine();

        // checks to see if we are at a label or not, if so return as new label node.
        if (!line.startsWith("Feature: ")) {
            return new ClassifierNode(line, null);
        }

        String feature = line.substring("Feature: ".length());
        double threshold = Double.parseDouble(input.nextLine().substring("Threshold: ".length()));

        ClassifierNode leftNode = createTree(input);
        ClassifierNode rightNode = createTree(input);

        return new ClassifierNode(feature, threshold, leftNode, rightNode);
    }

    // Behavior:
    // - Creates and trains a classifier from the input data and corresponding labels.
    // Parameters:
    // - data: the text data.
    // - labels: the assosiated label for each text data.
    // Exceptions:
    // - if data or labels is null throw an illegal argument exception.
    // - if data and labels are not the same size also throw an illegal argument exception.
    // - if datas or labels is empty throw an illegal argument exception.
    public Classifier(List<TextBlock> data, List<String> labels) {
        if (data == null || labels == null) {
            throw new IllegalArgumentException("data or labels cant be null");
        } else if (data.size() != labels.size()) {
            throw new IllegalArgumentException("data and labels must be same size");
        } else if (data.isEmpty()) {
            throw new IllegalArgumentException("data or labels cant be empty");
        }

        overallRoot = new ClassifierNode(labels.get(0), data.get(0));

        for (int i = 1; i < data.size(); i++) {
            overallRoot = train(overallRoot, data.get(i), labels.get(i));
        }
    }

    // Behavior:
    // a helper method for the constructor method to "train" the descison tree.
    // Parameters:
    // - data: the text data.
    // - labels: the assosiated label for each text data.
    // - root: the current node for the decision tree.
    // Returns:
    // - ClassifierNode: returns the root of the desison tree after training.
    private ClassifierNode train(ClassifierNode root, TextBlock block, String labels) {
        if (root.label != null) {
            if (root.label.equals(labels)) {
                return root;
            }

            String feature = block.findBiggestDifference(root.block);
            double val1 = root.block.get(feature);
            double val2 = block.get(feature);
            double midpoint = midpoint(val1, val2);

            if (val2 < midpoint) {
                root.left = new ClassifierNode(labels, block);
                root.right = new ClassifierNode(root.label, root.block);
            } else {
                root.left = new ClassifierNode(root.label, root.block);
                root.right = new ClassifierNode(labels, block);
            }

            return new ClassifierNode(feature, midpoint, root.left, root.right);
        }

        double val = block.get(root.feature);
        if(val < root.threshold) {
            return new ClassifierNode(root.feature, root.threshold,
                                      train(root.left, block, labels), root.right);
        } else {
            return new ClassifierNode(root.feature, root.threshold,
                                      root.left, train(root.right, block, labels));
        }
    }

    // Behavior:
    // - Takes a text block and returns a label that this classifier predicts for it.
    // Parameters:
    // - input: the text being classified
    // Exceptions:
    // - if input is null return a illegal argument exception
    // Return:
    // - String: the label predicted by the classifier to match the input.
    public String classify(TextBlock input) {
        if(input == null) {
            throw new IllegalArgumentException("input cant be null");
        }
        return classify(input, overallRoot);
    }

    // Behavior:
    // - a helper method for classify which recurively goes through the classifier decsion tree
    //   to find a valid label for the input text.
    // Parameters:
    // - input: the text being classified.
    // - root: the current node of the tree.
    // Returns:
    // - String: the label predicted by the classifier to match the input.
    private String classify(TextBlock input, ClassifierNode root) {
        if (root.label != null) {
            return root.label;
        }

        if (input.get(root.feature) < root.threshold) {
            return classify(input, root.left);
        } else {
            return classify(input, root.right);
        }
    }

    // Behavior:
    // - Saves the current classifier to the inputed PrintStream
    // Parameters:
    // - output: the prinstream being saved to.
    // Exceptions:
    // - if output is null throw illegal argument exception.
    public void save(PrintStream output) {
        if (output == null) {
            throw new IllegalArgumentException("output cant be null");
        }
        save(overallRoot, output);
    }

    // Behavior:
    // - A helper method for the save method which recursively goes through the decsion tree
    //   and saves it to a new file in the form of "Feature: featureName" next line "Threshold:
    //   thresholdValue" for each node, and just the label for leaf nodes.

    // Parameters:
    // - output: the prinstream being saved to.
    private void save(ClassifierNode root, PrintStream output) {
        if(root != null) {
            if(root.feature == null) {
                output.println(root.label);
            } else {
                output.println("Feature: " + root.feature);
                output.println("Threshold: " + root.threshold);
                save(root.left, output);
                save(root.right, output);
            }
        }
    }

    //This represents the nodes for the desicion tree in classifier
    private static class ClassifierNode {
        public final String label;
        public final String feature;
        public final double threshold;
        public final TextBlock block;
        public ClassifierNode left;
        public ClassifierNode right;

        // creates a node with a label and textblock
        public ClassifierNode(String label, TextBlock block) {
            this.label = label;
            this.block = block;
            this.threshold = 0.0;
            this.feature = null;
            this.left = null;
            this.right = null;
        }

        // creates a node with a featurem threshold, and a left and right node.
        public ClassifierNode(String feature, double threshold, ClassifierNode
                                left, ClassifierNode right) {
            this.label = null;
            this.block = null;
            this.feature = feature;
            this.threshold = threshold;
            this.left = left;
            this.right = right;
        }
    }


    ////////////////////////////////////////////////////////////////////
    // PROVIDED METHODS - **DO NOT MODIFY ANYTHING BELOW THIS LINE!** //
    ////////////////////////////////////////////////////////////////////

    // Helper method to calcualte the midpoint of two provided doubles.
    private static double midpoint(double one, double two) {
        return Math.min(one, two) + (Math.abs(one - two) / 2.0);
    }

    // Behavior: Calculates the accuracy of this model on provided Lists of
    //           testing 'data' and corresponding 'labels'. The label for a
    //           datapoint at an index within 'data' should be found at the
    //           same index within 'labels'.
    // Exceptions: IllegalArgumentException if the number of datapoints doesn't match the number
    //             of provided labels
    // Returns: a map storing the classification accuracy for each of the encountered labels when
    //          classifying
    // Parameters: data - the list of TextBlock objects to classify. Should be non-null.
    //             labels - the list of expected labels for each TextBlock object.
    //             Should be non-null.
    public Map<String, Double> calculateAccuracy(List<TextBlock> data, List<String> labels) {
        // Check to make sure the lists have the same size (each datapoint has an expected label)
        if (data.size() != labels.size()) {
            throw new IllegalArgumentException(
                    String.format("Length of provided data [%d] doesn't match provided labels [%d]",
                                  data.size(), labels.size()));
        }

        // Create our total and correct maps for average calculation
        Map<String, Integer> labelToTotal = new HashMap<>();
        Map<String, Double> labelToCorrect = new HashMap<>();
        labelToTotal.put("Overall", 0);
        labelToCorrect.put("Overall", 0.0);

        for (int i = 0; i < data.size(); i++) {
            String result = classify(data.get(i));
            String label = labels.get(i);

            // Increment totals depending on resultant label
            labelToTotal.put(label, labelToTotal.getOrDefault(label, 0) + 1);
            labelToTotal.put("Overall", labelToTotal.get("Overall") + 1);
            if (result.equals(label)) {
                labelToCorrect.put(result, labelToCorrect.getOrDefault(result, 0.0) + 1);
                labelToCorrect.put("Overall", labelToCorrect.get("Overall") + 1);
            }
        }

        // Turn totals into accuracy percentage
        for (String label : labelToCorrect.keySet()) {
            labelToCorrect.put(label, labelToCorrect.get(label) / labelToTotal.get(label));
        }
        return labelToCorrect;
    }
}
