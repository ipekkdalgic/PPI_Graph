import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
public class Test {
    public static void main(String[] args) throws FileNotFoundException {
        EdgeListGraph graph = new EdgeListGraph();
        Scanner sc = new Scanner(System.in);
        boolean loaded = false;

        while (true) {
            System.out.println("\n===== STRING PPI MENU =====");
            System.out.println("1) Load graph");
            System.out.println("2) Search for protein by protein_id");
            System.out.println("3) Check if two proteins have an interaction");
            System.out.println("4) Most confident path between two proteins");
            System.out.println("5) Most confident path between two proteins");
            System.out.println("6) BFS and DFS Traverse (origin protein)");
            System.out.println("0) Exit");
            System.out.print("Choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid input.");
                continue;
            }

            if (choice == 0) {
                System.out.println("Bye!");
                break;
            }
            // secenek 1 dosyayı loadlar
            if (choice == 1) {
                System.out.print("Confidence threshold (0-1 arası, örn 0.7): ");
                double threshold;
                try {
                    threshold = Double.parseDouble(sc.nextLine().trim());
                } catch (Exception e) {
                    System.out.println("Threshold sayı olmalı.");
                    continue;
                }

                String infoPath  = "9606.protein.info.v12.0.txt";
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
             // girilen protein hasvertex mi diye bakar
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

            // iki proten arasındaki etkilesime bakar eğer girdiğimiz treshold link dosyasındaki sayıdan büyükse etkilesim verir yoksa eskilesim vermez
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

            // bfs ve dfs ye göre arar direkt edgelistgraphtan fonksiyonu çağirir
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


    // kuyruğu yazar
    private static void printQueue(QueueInterface<String> q) {
        while (!q.isEmpty()) {
            System.out.println(q.dequeue());
        }
    }
    // graphı yüklemek için
    // değer aralığı 0-1 arası olmazsa hata
    // \\s+ bu regexe göre dosyayı ayırıp okuyor zaten ilk okunan protein idsi ve sting
    // link dosyasında 2 tane id 1 tane combined score olduğu için ilk ikiyi proteinlere atar 3. raw olur onu double a çevir
    // directed dediği için iki yönlü kontrol ederiz
    private static int loadGraph(EdgeListGraph graph, String infoPath, String linksPath, double threshold) throws FileNotFoundException {
        if (threshold < 0 || threshold > 1) {
            throw new IllegalArgumentException("Threshold must be between 0-1");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(infoPath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                String proteinId = parts[0];
                graph.addVertex(proteinId);
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

                String p1 = parts[0];
                String p2 = parts[1];

                double raw;
                try {
                    raw = Double.parseDouble(parts[2]);
                } catch (NumberFormatException ex) {
                    continue;
                }

                double score = (raw > 1.0) ? (raw / 1000.0) : raw;
                if (score >= threshold) {
                    if (graph.addEdge(p1, p2, score)) addedEdges++;
                    if (graph.addEdge(p2, p1, score)) addedEdges++;
                }
            }
        } catch (IOException e) {
            System.out.println("LINKS dosyası okunamadı: " + e.getMessage());
            return addedEdges;
        }
        return addedEdges;
    }

}
