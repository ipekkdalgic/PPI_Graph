public interface GraphAlgorithmsInterface<T> {
    public QueueInterface<T>  getBreadthFirstTravel(T origin);
    public QueueInterface<T> getDepthFirstTravel(T origin);
    public double cheapestPath (T begin,T end,StackInterface<T> path);
}
