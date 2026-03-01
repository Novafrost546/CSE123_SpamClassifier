# Spam Classifier

A machine learning classifier that uses decision trees to classify emails and text documents as spam or ham (legitimate). Built in Java as a comprehensive implementation of binary tree data structures and recursive algorithms.

## Overview

This project implements a binary decision tree classifier that learns to distinguish between spam and legitimate emails by analyzing word probabilities. The classifier is trained on labeled examples and builds a tree-based model that recursively evaluates text features to make classification predictions.

## Features

- **Decision Tree Classification**: Builds and traverses binary decision trees for text classification
- **Recursive Training Algorithm**: Implements a novel tree-building approach that adaptively splits nodes based on feature differences
- **Model Persistence**: Save trained classifiers to files and load them for later use
- **Accuracy Metrics**: Calculate per-label and overall classification accuracy on test datasets
- **Pre-order Serialization**: Trees are serialized in pre-order format for efficient I/O

## Architecture

### Core Components

- **Classifier.java**: Main classification engine with tree training and prediction logic
- **ClassifierNode.java**: Inner class representing decision and leaf nodes in the tree
- **TextBlock.java**: Represents text data with word probability features
- **Client.java**: Interactive command-line interface for training, loading, and testing models

### Tree Design

The classifier uses a binary decision tree where:
- **Leaf nodes** store classification labels and the original training example
- **Internal nodes** store a feature name and threshold value
- **Traversal** follows the path: if feature value < threshold, go left; otherwise go right

## Usage

### Training a New Model

```bash
# Run the interactive client
java Client

# Select option 1 (Train classification model)
# Provide path to training CSV file (e.g., data/emails/train.csv)
# Optional: shuffle the data for better generalization
```

### Loading a Pre-trained Model

```bash
# Run the client
java Client

# Select option 2 (Load model from file)
# Provide path to saved model file (e.g., trees/simple.txt)
```

### Classifying New Data

After loading or training a model:
- Select option 1 to classify an input CSV file
- View predictions for each document

### Saving Models

- Select option 3 to save your trained classifier
- Save to a file for later use or output to console to view the tree structure

## File Format

Models are saved in a pre-order traversal format:
```
Feature: word_name
Threshold: 0.125
Feature: another_word
Threshold: 0.25
label_value
label_value
```

This format allows models to be perfectly reconstructed by the Scanner constructor.

## Algorithm

The training algorithm follows these steps:

1. **Initialize**: Create a leaf node with the first training example
2. **Classify**: For each new example, classify it using the current tree
3. **Adapt**: If prediction is incorrect, split the misclassified leaf node:
   - Find the feature with biggest probability difference between examples
   - Calculate midpoint as the decision threshold
   - Place original and new examples as children
4. **Repeat**: Process all training examples in order

## Project Structure

```
P3_SpamClassifier/
├── Classifier.java          # Main classifier implementation
├── ClassifierNode.java      # Tree node inner class
├── TextBlock.java           # Text feature representation
├── Client.java              # Interactive CLI
├── CsvReader.java           # CSV parsing utility
├── DataLoader.java          # Training data loader
├── data/
│   ├── emails/
│   │   ├── train.csv        # Email training dataset
│   │   ├── test.csv         # Email test dataset
│   │   ├── example_hams.csv # Sample legitimate emails
│   │   └── example_spams.csv# Sample spam emails
│   └── federalist_papers/   # Alternative dataset
├── trees/
│   ├── simple.txt           # Pre-trained simple model
│   ├── medium.txt           # Pre-trained medium model
│   └── large.txt            # Pre-trained large model
└── README.md
```

## Technical Details

### Language & Tools
- **Language**: Java 21
- **Build System**: Maven/Gradle
- **Data Format**: CSV with header row

### Key Implementation Patterns

- **x = change(x)**: Tree modifications create new nodes rather than mutating existing ones
- **Public-Private Pair**: Recursive algorithms use public wrapper methods calling private helpers
- **Functional Tree Operations**: No in-place node modifications; all changes return new tree references

### Performance

- **Training**: O(n*m*log n) where n = training examples, m = features
- **Classification**: O(log n) average case for balanced trees
- **Space**: O(n) for storing the complete tree

## Running Tests

```bash
# Compile
javac *.java

# Run client with training dataset
java Client
# Choose option 1, enter data/emails/train.csv

# Test accuracy
# Choose option 2 to see model performance

# Save the model
# Choose option 3 to export for later use
```

## Example Results

On the email dataset:
- Overall accuracy: ~93-96% (depending on training set)
- Ham classification: ~99% accurate
- Spam classification: ~70-80% accurate

## Learning Objectives

This implementation demonstrates:
- Binary tree construction and traversal
- Recursive algorithm design
- Data encapsulation and object-oriented principles
- Generic data structure implementation
- Functional programming patterns in tree manipulation

## License

This is an educational project for CSE 123 (Data Structures).
ReadMe created with Ai
## Author

Eric Nguyen
