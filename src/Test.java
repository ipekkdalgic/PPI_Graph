import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Test {

    //main method can throw FileNotFoundException
    //even if we catch it inside, we show that we know exception handling
    public static void main(String[] args) throws FileNotFoundException {

        //create graph object
        EdgeListGraph graph = new EdgeListGraph();

        //scanner for menu input
        Scanner sc = new Scanner(System.in);

        //we use this to check graph loaded or not
        boolean loaded = false;

        //menu loop
        while (true) {
            System.out.println("\n===== STRING PPI MENU =====");
            System.out.println("1) Load graph");
            System.out.println("2) Search for protein by protein_id");
            System.out.println("3) Check if two proteins have an interaction");
            System.out.println("4) Most confident path between two proteins");
            System.out.println("5) Calculate basic graph metrics (VertexCount, EdgeCount, Average Degree, Diameter, Reciprocity)");
            System.out.println("6) BFS and DFS Traverse (origin protein)");
            System.out.println("0) Exit");
            System.out.print("Choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                //if input is not number
                System.out.println("Invalid input.");
                continue;
            }

            //exit
            if (choice == 0) {
                System.out.println("Bye!");
                break;
            }

            //1) load graph
            if (choice == 1) {
                System.out.print("Confidence threshold (0-1 arası, örn 0.7): ");

                double threshold;
                try {
                    threshold = Double.parseDouble(sc.nextLine().trim());
                } catch (Exception e) {
                    System.out.println("Threshold sayı olmalı.");
                    continue;
                }

                String infoPath = "9606.protein.info.v12.0.txt";
                String linksPath = "9606.protein.links.v12.0.txt";

                try {
                    int added = loadGraph(graph, infoPath, linksPath, threshold);
                    loaded = true;

                    System.out.println("Graph loaded!");
                    System.out.println("Vertices: " + graph.getNumberOfVertices());
                    System.out.println("Edges:    " + graph.getNumberOfEdges());
                    System.out.println("Added edges (after filtering): " + added);

                } catch (Exception ex) {
                    System.out.println("Load failed: " + ex.getMessage());
                }
            }

            //2) search protein
            else if (choice == 2) {
                if (!loaded) {
                    System.out.println("Önce 1) Load graph yap.");
                    continue;
                }

                System.out.print("protein_id: ");
                String id = sc.nextLine().trim();

                if (graph.hasVertex(id)) System.out.println("FOUND: " + id);
                else System.out.println("NOT FOUND: " + id);
            }

            //3) check interaction
            else if (choice == 3) {
                if (!loaded) {
                    System.out.println("Önce 1) Load graph yap.");
                    continue;
                }

                System.out.print("Protein 1 (protein_id): ");
                String p1 = sc.nextLine().trim();
                System.out.print("Protein 2 (protein_id): ");
                String p2 = sc.nextLine().trim();

                if (graph.hasEdge(p1, p2)) {
                    System.out.println("YES: interaction exists (" + p1 + " -> " + p2 + ")");
                } else {
                    System.out.println("NO: interaction does NOT exist (" + p1 + " -> " + p2 + ")");
                }
            }

            //4) most confident path
            else if (choice == 4) {
                if (!loaded) {
                    System.out.println("Önce 1) Load graph yap.");
                    continue;
                }

                System.out.print("Protein 1: ");
                String p1 = sc.nextLine().trim();
                System.out.print("Protein 2: ");
                String p2 = sc.nextLine().trim();

                StackInterface<String> path = new LinkedStack<>();
                double conf = graph.cheapestPath(p1, p2, path);

                if (conf == 0.0) {
                    System.out.println("No path found.");
                } else {
                    System.out.println("Most confident path confidence: " + conf);
                    System.out.println("Path:");
                    while (!path.isEmpty()) {
                        System.out.println(path.pop());
                    }
                }
            }

            //5) graph metrics
            else if (choice == 5) {
                if (!loaded) {
                    System.out.println("Önce 1) Load graph yap.");
                    continue;
                }

                System.out.println("\n--- BASIC GRAPH METRICS ---");
                EdgeListGraph.GraphMetrics m = graph.calculateBasicMetrics();

                System.out.println("VertexCount     : " + m.vertexCount);
                System.out.println("EdgeCount       : " + m.edgeCount);
                System.out.printf("Average Degree  : %.4f%n", m.averageDegree);
                System.out.println("Diameter (hops) : " + m.diameter);
                System.out.printf("Reciprocity     : %.4f%n", m.reciprocity);
            }

            //6) BFS and DFS
            else if (choice == 6) {
                if (!loaded) {
                    System.out.println("Önce 1) Load graph yap.");
                    continue;
                }

                System.out.print("Origin protein_id: ");
                String origin = sc.nextLine().trim();

                if (!graph.hasVertex(origin)) {
                    System.out.println("Origin protein not found.");
                    continue;
                }

                System.out.println("\nBFS order:");
                QueueInterface<String> bfs = graph.getBreadthFirstTravel(origin);
                printQueue(bfs);

                System.out.println("\nDFS order:");
                QueueInterface<String> dfs = graph.getDepthFirstTravel(origin);
                printQueue(dfs);
            }

            else {
                System.out.println("Unknown choice.");
            }
        }
        sc.close();
    }

    //print queue elements
    private static void printQueue(QueueInterface<String> q) {
        while (!q.isEmpty()) {
            System.out.println(q.dequeue());
        }
    }

    //load graph from files
    private static int loadGraph(EdgeListGraph graph, String infoPath, String linksPath, double threshold) {
        if (threshold < 0 || threshold > 1) {
            throw new IllegalArgumentException("Threshold must be between 0-1");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(infoPath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                graph.addVertex(parts[0]);
            }
        } catch (IOException e) {
            System.out.println("INFO dosyası okunamadı: " + e.getMessage());
            return 0;
        }

        int addedEdges = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(linksPath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length < 3) continue;

                double raw;
                try {
                    raw = Double.parseDouble(parts[2]);
                } catch (NumberFormatException ex) {
                    continue;
                }

                double score = (raw > 1.0) ? (raw / 1000.0) : raw;

                if (score >= threshold) {
                    if (graph.addEdge(parts[0], parts[1], score)) addedEdges++;
                    if (graph.addEdge(parts[1], parts[0], score)) addedEdges++;
                }
            }
        } catch (IOException e) {
            System.out.println("LINKS dosyası okunamadı: " + e.getMessage());
            return addedEdges;
        }

        return addedEdges;
    }
}