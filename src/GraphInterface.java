public interface GraphInterface<T> {
    public boolean addVertex(T vertex);
    public boolean addEdge(T begin, T end, double weight);
    public boolean hasVertex(T vertex);
    public boolean hasEdge(T begin, T end);
}
