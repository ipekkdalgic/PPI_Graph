import java.util.ArrayList;
import java.util.HashSet;

public class EdgeListGraph implements GraphInterface<String> {

    //we keep all protein ids here (vertex list)
    private ArrayList<String> vertices = new ArrayList<>();

    //we keep all edges here (begin -> end) and weight is confidence score
    private ArrayList<Edge<String>> edges = new ArrayList<>();

    //add new vertex (protein id)
    @Override
    public boolean addVertex(String vertex) {
        //if vertex is null, we cannot add
        if (vertex == null) return false;

        //if already exist, do not add again
        if (hasVertex(vertex)) return false;

        //add to vertex list
        vertices.add(vertex);
        return true;
    }

    //add new directed edge begin -> end with weight
    @Override
    public boolean addEdge(String begin, String end, double weight) {
        //if any id is null, edge cannot be added
        if (begin == null | end == null) return false;

        //if begin not in graph, add it
        if (!hasVertex(begin)) addVertex(begin);

        //if end not in graph, add it
        if (!hasVertex(end)) addVertex(end);

        //if edge already exist, do not add again
        if (hasEdge(begin, end)) return false;

        //add new edge
        edges.add(new Edge<>(begin, end, weight));
        return true;
    }

    //check if vertex exists
    @Override
    public boolean hasVertex(String vertex) {
        //we search in vertex list one by one
        for (int i = 0; i < vertices.size(); i++) {
            String v = vertices.get(i);
            if (v.equals(vertex)) return true;
        }
        return false;
    }

    //check if edge exists (directed: begin -> end)
    @Override
    public boolean hasEdge(String begin, String end) {
        //we search in edge list one by one
        for (int i = 0; i < edges.size(); i++) {
            Edge<String> e = edges.get(i);
            if (e.getBegin().equals(begin) && e.getEnd().equals(end)) {
                return true;
            }
        }
        return false;
    }

    //graph empty check
    @Override
    public boolean isEmpty() {
        return vertices.isEmpty();
    }

    //how many vertices in graph
    @Override
    public int getNumberOfVertices() {
        return vertices.size();
    }

    //how many edges in graph
    @Override
    public int getNumberOfEdges() {
        return edges.size();
    }

    //BFS traversal from origin
    @Override
    public QueueInterface<String> getBreadthFirstTravel(String origin) {
        //this queue will store BFS visit order
        QueueInterface<String> result = new LinkedQueue<>();

        //if origin is not valid, return empty result
        if (origin == null || !hasVertex(origin)) return result;

        //visited array for vertices (by index)
        boolean[] visited = new boolean[vertices.size()];

        //queue for BFS process
        QueueInterface<String> queue = new LinkedQueue<>();

        //start from origin
        int originIndex = vertices.indexOf(origin);
        visited[originIndex] = true;

        queue.enqueue(origin);
        result.enqueue(origin);

        //BFS loop
        while (!queue.isEmpty()) {
            String current = queue.dequeue();

            //get neighbors of current (outgoing)
            ArrayList<String> neighbors = getNeighbors(current);

            for (int i = 0; i < neighbors.size(); i++) {
                String neighbor = neighbors.get(i);
                int index = vertices.indexOf(neighbor);

                //if not visited, visit it
                if (!visited[index]) {
                    visited[index] = true;
                    queue.enqueue(neighbor);
                    result.enqueue(neighbor);
                }
            }
        }

        return result;
    }

    //DFS traversal from origin
    @Override
    public QueueInterface<String> getDepthFirstTravel(String origin) {
        //this queue will store DFS visit order
        QueueInterface<String> result = new LinkedQueue<>();

        //if origin is not valid, return empty result
        if (origin == null || !hasVertex(origin)) return result;

        //visited array for vertices (by index)
        boolean[] visited = new boolean[vertices.size()];

        //stack for DFS process
        StackInterface<String> stack = new LinkedStack<>();

        //start from origin
        int originIndex = vertices.indexOf(origin);
        visited[originIndex] = true;

        stack.push(origin);
        result.enqueue(origin);

        //DFS loop
        while (!stack.isEmpty()) {
            String top = stack.peek();

            //get neighbors of top (outgoing)
            ArrayList<String> neighbors = getNeighbors(top);

            boolean foundUnvisited = false;

            //find first unvisited neighbor and go deeper
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

            //if no unvisited neighbor, backtrack
            if (!foundUnvisited) {
                stack.pop();
            }
        }

        return result;
    }
    //finds the shortest path from begin to end based on the minimum number of edges
    //uses BFS and stores predecessors to reconstruct the first found shortest path
    public int shortestPath(String begin, String end, StackInterface<String> path) {
        path.clear();
        if (begin == null || end == null) return -1;
        if (!hasVertex(begin) || !hasVertex(end)) return -1;

        int n = vertices.size();
        int start = vertices.indexOf(begin);
        int target = vertices.indexOf(end);

        int[] dist = new int[n];
        int[] pred = new int[n];
        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            dist[i] = -1;
            pred[i] = -1;
            visited[i] = false;
        }

        QueueInterface<Integer> q = new LinkedQueue<>();
        visited[start] = true;
        dist[start] = 0;
        q.enqueue(start);

        while (!q.isEmpty()) {
            int u = q.dequeue();
            if (u == target) break;

            String uLabel = vertices.get(u);
            ArrayList<Edge<String>> out = getOutgoingEdges(uLabel);

            for (int k = 0; k < out.size(); k++) {
                int v = vertices.indexOf(out.get(k).getEnd());
                if (v == -1) continue;

                if (!visited[v]) {
                    visited[v] = true;
                    dist[v] = dist[u] + 1;
                    pred[v] = u;
                    q.enqueue(v);
                }
            }
        }

        if (dist[target] == -1) return -1;
        StackInterface<String> rev = new LinkedStack<>();
        int current = target;
        while (current != -1) {
            rev.push(vertices.get(current));
            if (current == start) break;
            current = pred[current];
        }
        while (!rev.isEmpty()) path.push(rev.pop());

        return dist[target];
    }

    //most confident path (we use Dijkstra with -log(score))
    //edge weights represent confidence scores, and the total path cost is the sum of edge weights
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

        for (int it = 0; it < n; it++) {
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
            ArrayList<Edge<String>> out = getOutgoingEdges(uLabel);

            for (int k = 0; k < out.size(); k++) {
                Edge<String> e = out.get(k);
                int v = vertices.indexOf(e.getEnd());
                if (v == -1) continue;

                double w = e.getWeight();
                double cand = distance[u] + w;

                if (cand < distance[v]) {
                    distance[v] = cand;
                    pred[v] = u;
                }
            }
        }

        if (Double.isInfinite(distance[target])) return 0.0;

        StackInterface<String> rev = new LinkedStack<>();
        int cur = target;
        while (cur != -1) {
            rev.push(vertices.get(cur));
            if (cur == start) break;
            cur = pred[cur];
        }
        while (!rev.isEmpty()) path.push(rev.pop());

        return distance[target];
    }
    // computes the Most Confident Path between two vertices
    // it first finds the shortest path distances using BFS
    // then among all shortest paths, selects the one with the maximum total edge weight
    // sum of confidence scores using dynamic programming over BFS layers
    public double mostConfidentPath(String begin, String end, StackInterface<String> path) {
        path.clear();
        if (begin == null || end == null) return 0.0;
        if (!hasVertex(begin) || !hasVertex(end)) return 0.0;

        int n = vertices.size();
        int start = vertices.indexOf(begin);
        int target = vertices.indexOf(end);

        int[] distance = new int[n];
        for (int i = 0; i < n; i++) distance[i] = -1;

        QueueInterface<Integer> q = new LinkedQueue<>();
        distance[start] = 0;
        q.enqueue(start);

        while (!q.isEmpty()) {
            int u = q.dequeue();

            String uLabel = vertices.get(u);
            ArrayList<Edge<String>> out = getOutgoingEdges(uLabel);

            for (int k = 0; k < out.size(); k++) {
                int v = vertices.indexOf(out.get(k).getEnd());
                if (v == -1) continue;

                if (distance[v] == -1) {
                    distance[v] = distance[u] + 1;
                    q.enqueue(v);
                }
            }
        }

        if (distance[target] == -1) return 0.0;
        double[] best = new double[n];
        int[] pre = new int[n];
        for (int i = 0; i < n; i++) {
            best[i] = Double.NEGATIVE_INFINITY;
            pre[i] = -1;
        }
        best[start] = 0.0;

        int maxD = distance[target];
        for (int d = 0; d < maxD; d++) {
            for (int u = 0; u < n; u++) {
                if (distance[u] != d) continue;
                if (best[u] == Double.NEGATIVE_INFINITY) continue;

                String uLabel = vertices.get(u);
                ArrayList<Edge<String>> out = getOutgoingEdges(uLabel);

                for (int k = 0; k < out.size(); k++) {
                    Edge<String> e = out.get(k);
                    int v = vertices.indexOf(e.getEnd());
                    if (v == -1) continue;

                    if (distance[v] == distance[u] + 1) {
                        double w = e.getWeight();
                        double cand = best[u] + w;

                        if (cand > best[v]) {
                            best[v] = cand;
                            pre[v] = u;
                        }
                    }
                }
            }
        }

        if (best[target] == Double.NEGATIVE_INFINITY) return 0.0;

        StackInterface<String> rev = new LinkedStack<>();
        int current = target;
        while (current != -1) {
            rev.push(vertices.get(current));
            if (current == start) break;
            current = pre[current];
        }

        if (rev.isEmpty() || !rev.peek().equals(begin)) return 0.0;
        while (!rev.isEmpty()) path.push(rev.pop());
        return best[target];
    }


    //get all outgoing edges of a vertex
    private ArrayList<Edge<String>> getOutgoingEdges(String vertex) {
        ArrayList<Edge<String>> out = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            Edge<String> e = edges.get(i);
            if (e.getBegin().equals(vertex)) out.add(e);
        }
        return out;
    }

    //get neighbor list (outgoing neighbors) of a vertex
    private ArrayList<String> getNeighbors(String vertex) {
        ArrayList<String> neigh = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            Edge<String> e = edges.get(i);
            if (e.getBegin().equals(vertex)) {
                neigh.add(e.getEnd());
            }
        }
        return neigh;
    }

    //baisc graph metrics (VertexCount, EdgeCount, AvgDegree, Diameter, Reciprocity)
    public GraphMetrics calculateBasicMetrics() {
        int V = getNumberOfVertices();
        int E = getNumberOfEdges();

        //average degree (in+out) for directed graph is 2E / V
        double avgDegree = (V == 0) ? 0.0 : (2.0 * E) / V;

        //reciprocity is how many edges have reverse edge too
        double reciprocity = (E == 0) ? 0.0 : (countReciprocalEdges() / (double) E);

        //diameter is max shortest hop distance between reachable pairs
        int diameter = computeDiameterUnweightedDirected();

        return new GraphMetrics(V, E, avgDegree, diameter, reciprocity);
    }

    //count reciprocated edges - u->v and v->u
    private int countReciprocalEdges() {
        HashSet<String> edgeSet = new HashSet<>(edges.size() * 2);

        //store every edge as u->v
        for (int i = 0; i < edges.size(); i++) {
            Edge<String> e = edges.get(i);
            edgeSet.add(key(e.getBegin(), e.getEnd()));
        }

        int reciprocalEdgeCount = 0;

        //if reverse exists, this edge is reciprocated
        for (int i = 0; i < edges.size(); i++) {
            Edge<String> e = edges.get(i);
            String rev = key(e.getEnd(), e.getBegin());
            if (edgeSet.contains(rev)) {
                reciprocalEdgeCount++;
            }
        }

        return reciprocalEdgeCount;
    }

    //make unique key for edge
    private String key(String u, String v) {
        return u + "->" + v;
    }

    //compute diameter with BFS from every vertex
    private int computeDiameterUnweightedDirected() {
        int n = vertices.size();
        if (n == 0) return 0;

        //build adjacency list one time
        ArrayList<ArrayList<Integer>> adj = buildAdjIndex();

        int diameter = 0;

        int[] dist = new int[n];
        int[] q = new int[n];

        //run BFS for each start vertex
        for (int s = 0; s < n; s++) {
            //set all dist to -1 first
            for (int i = 0; i < n; i++) dist[i] = -1;

            int head = 0, tail = 0;
            dist[s] = 0;
            q[tail++] = s;

            //BFS loop
            while (head < tail) {
                int u = q[head++];
                int du = dist[u];

                ArrayList<Integer> neigh = adj.get(u);
                for (int k = 0; k < neigh.size(); k++) {
                    int v = neigh.get(k);
                    if (dist[v] == -1) {
                        dist[v] = du + 1;
                        q[tail++] = v;
                    }
                }
            }

            //update diameter with max dist in this BFS
            for (int i = 0; i < n; i++) {
                if (dist[i] > diameter) diameter = dist[i];
            }
        }

        return diameter;
    }

    //build adjacency list by using vertex indexes
    private ArrayList<ArrayList<Integer>> buildAdjIndex() {
        int n = vertices.size();
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

        //add directed edge u->v into adjacency list
        for (int i = 0; i < edges.size(); i++) {
            Edge<String> e = edges.get(i);
            int u = vertices.indexOf(e.getBegin());
            int v = vertices.indexOf(e.getEnd());

            if (u != -1 && v != -1) {
                adj.get(u).add(v);
            }
        }

        return adj;
    }

    //this class only stores metric results
    public static class GraphMetrics {
        public final int vertexCount;
        public final int edgeCount;
        public final double averageDegree;
        public final int diameter;
        public final double reciprocity;

        public GraphMetrics(int vertexCount, int edgeCount, double averageDegree, int diameter, double reciprocity) {
            this.vertexCount = vertexCount;
            this.edgeCount = edgeCount;
            this.averageDegree = averageDegree;
            this.diameter = diameter;
            this.reciprocity = reciprocity;
        }
    }
}