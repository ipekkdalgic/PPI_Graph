import java.util.ArrayList;
public class EdgeListGraph implements GraphInterface <String>{
    private ArrayList<String> vertices = new ArrayList<>();
    private ArrayList<Edge<String>> edges = new ArrayList<>();
   // classı generic yapmadım cünkü string sistemiyle çakişiyordu en temis yol buydu o yüzden generic değil
    @Override
    public boolean addVertex(String vertex) {
        if(vertex == null) return false;
        if(hasVertex(vertex)) return false;
        vertices.add(vertex);
        return true;
    }

    @Override
    public boolean addEdge(String begin, String end, double weight) {
        if(begin == null| end == null)return false;
        if(!hasVertex(begin)) addVertex(begin);
        if(!hasVertex(end)) addVertex(end);
        if(hasEdge(begin,end))return false;
        edges.add(new Edge<>(begin,end,weight));
        return true;
    }
   // o protein var mı
    @Override
    public boolean hasVertex(String vertex) {
        for(int i =0; i< vertices.size();i++) {
            String v = vertices.get(i);
            if (v.equals(vertex)) return true;
        }
        return false;
    }
  // iki protein arasında bağ var mı
    @Override
    public boolean hasEdge(String begin, String end) {
        for(int i =0; i<edges.size();i++){
            Edge<String> e =  edges.get(i);
            if(e.getBegin().equals(begin)&& e.getEnd().equals(end)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return vertices.isEmpty();
    }

    @Override
    public int getNumberOfVertices() {
        return vertices.size();
    }

    @Override
    public int getNumberOfEdges() {
        return edges.size();
    }
  // bfs
    @Override
    public QueueInterface<String> getBreadthFirstTravel(String origin) {
        QueueInterface<String> result = new LinkedQueue<>();
        if(origin == null|| !hasVertex(origin)) return result;

        boolean[] visited = new boolean [vertices.size()];
        QueueInterface<String> queue = new LinkedQueue<>();
        int originIndex = vertices.indexOf(origin);
        visited[originIndex] = true;
        queue.enqueue(origin);
        result.enqueue(origin);

        while (!queue.isEmpty()) {
            String current = queue.dequeue();

            ArrayList<String> neighbors = getNeighbors(current);
            for (int i = 0; i < neighbors.size(); i++) {
                String neighbor = neighbors.get(i);
                int index = vertices.indexOf(neighbor);

                if (!visited[index]) {
                    visited[index] = true;
                    queue.enqueue(neighbor);
                    result.enqueue(neighbor);
                }
            }
        }
        return result;
    }
   // dfs
    @Override
    public QueueInterface<String> getDepthFirstTravel(String origin) {

        QueueInterface<String> result = new LinkedQueue<>();
        if (origin == null || !hasVertex(origin)) return result;

        boolean[] visited = new boolean[vertices.size()];
        StackInterface<String> stack = new LinkedStack<>();

        int originIndex = vertices.indexOf(origin);
        visited[originIndex] = true;

        stack.push(origin);
        result.enqueue(origin);

        while (!stack.isEmpty()) {
            String top = stack.peek();
            ArrayList<String> neighbors = getNeighbors(top);

            boolean foundUnvisited = false;

            for (int i = 0; i < neighbors.size(); i++) {
                String neighbor = neighbors.get(i);
                int idx = vertices.indexOf(neighbor);

                if (!visited[idx]) {
                    visited[idx] = true;
                    stack.push(neighbor);
                    result.enqueue(neighbor);
                    foundUnvisited = true;
                    break;
                }
            }

            if (!foundUnvisited) {
                stack.pop();
            }
        }
        return result;
    }

  // kullanılmadıkları için boslar suan 2 si
    @Override
    public StackInterface<String> getTopologicalOrder() {
        return null;
    }

    @Override
    public int shortestPath(String begin, String end, StackInterface<String> path) {
        return 0;
    }
    // yani mostconfidentpath 4.fonksiyon için kullanıyoruz
    // Dijkstra (en ucuz yolu bulma algorisması) scoreları costa cevirip bunu kullanıyoruz
    // yolun güvenilirliği için kenar skorlarının çarpımı düşünülür
    // çarpımı max yapmak için cost = -log(score) kullaniyoruz
    @Override
    public double cheapestPath(String begin, String end, StackInterface<String> path) {

        path.clear();
        if (begin == null || end == null) return 0.0;
        if (!hasVertex(begin) || !hasVertex(end)) return 0.0;
        int n = vertices.size();
        int start = vertices.indexOf(begin);
        int target = vertices.indexOf(end);

        double[] distance = new double[n];
        int[] pred = new int[n];
        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            distance[i] = Double.POSITIVE_INFINITY;
            pred[i] = -1;
            visited[i] = false;
        }
        distance[start] = 0.0;
        for (int iterator = 0; iterator < n; iterator++) {
            int u = -1;
            double best = Double.POSITIVE_INFINITY;

            for (int i = 0; i < n; i++) {
                if (!visited[i] && distance[i] < best) {
                    best = distance[i];
                    u = i;
                }
            }

            if (u == -1) break;
            if (u == target) break;

            visited[u] = true;

            String uLabel = vertices.get(u);
            ArrayList<Edge<String>> outEdges = getOutgoingEdges(uLabel);

            for (int k = 0; k < outEdges.size(); k++) {
                Edge<String> e = outEdges.get(k);

                int v = vertices.indexOf(e.getEnd());
                if (v == -1) continue;

                double score = e.getWeight(); // 0..1
                if (score <= 0) continue;

                double edgeCost = -Math.log(Math.max(score, 1e-12));
                double newDist = distance[u] + edgeCost;

                if (newDist < distance[v]) {
                    distance[v] = newDist;
                    pred[v] = u;
                }
            }
        }
        if (Double.isInfinite(distance[target])) return 0.0;
        StackInterface<String> reverse = new LinkedStack<>();
        int cur = target;
        while (cur != -1) {
            reverse.push(vertices.get(cur));
            if (cur == start) break;
            cur = pred[cur];
        }
        while (!reverse.isEmpty()) {
            path.push(reverse.pop());
        }

        return Math.exp(-distance[target]);
    }

    private int indexOfVertex(String v) {
        return vertices.indexOf(v);
    }
    // komsuları çekerken score u da beraber  döndürür
    private ArrayList<Edge<String>> getOutgoingEdges(String vertex) {
        ArrayList<Edge<String>> out = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            Edge<String> e = edges.get(i);
            if (e.getBegin().equals(vertex)) out.add(e);
        }
        return out;
    }
    //neigh adında yeni bi array list oluşturup istenilenn vertexin kaç komsusu var ona bakar
    private ArrayList<String> getNeighbors(String vertex) {
        ArrayList<String> neigh = new ArrayList<>();
        for (int i =0;i<edges.size();i++) {
            Edge<String> e = edges.get(i);
            if (e.getBegin().equals(vertex)) {
                neigh.add(e.getEnd());
            }
        }
        return neigh;
    }
}
