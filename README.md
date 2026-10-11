# Protein–Protein Interaction Network Analysis

A Java graph analysis project using protein–protein interaction data from the STRING database.

Developed as a team project at Dokuz Eylül University to apply graph data structures and algorithms to biological interaction networks.

## Features

- Load protein identifiers and interaction confidence scores
- Filter interactions using a user-defined confidence threshold
- Search for proteins and check interactions
- Explore the network using Breadth-First Search (BFS) and Depth-First Search (DFS)
- Find the most confident path between two proteins
- Calculate network metrics, including vertex and edge counts, average degree, diameter, and reciprocity
- Interact with the application through a console menu

## Technologies and Concepts

- Java
- Object-Oriented Programming
- Custom Edge List Graph
- BFS and DFS
- Dijkstra's Algorithm
- Graph Metrics
- File Processing

## Graph Representation

Proteins are represented as vertices, and interactions are weighted by their confidence scores.

Each interaction is stored as two directed edges, allowing traversal in both directions.

During dataset loading, interactions below the selected confidence threshold are excluded.

## Most Confident Path

The confidence of a path is calculated as the product of its edge confidence scores.

The application transforms each confidence score into a cost using its negative logarithm. Dijkstra's algorithm then finds the minimum-cost path, corresponding to the maximum product of confidence scores.

## Dataset

The application uses STRING-derived files containing:

- Protein identifiers
- Interaction pairs and associated confidence scores

Ensure the required input files are available at the paths expected by the application.

## Running the Project

1. Download or clone the repository.
2. Open the project in a Java IDE.
3. Configure a Java Development Kit (JDK).
4. Prepare the required dataset files.
5. Run the class containing the `main` method.
6. Use the console menu to load the network and perform analyses.

## Authors

- İpek Dalgıç
- Kadriye Beren Sümer

## Scope

This academic project demonstrates graph algorithms on biological data. Path confidence describes the interaction scores used by the algorithm; it does not establish a biological or clinical conclusion.
