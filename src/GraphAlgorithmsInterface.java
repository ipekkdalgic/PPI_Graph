public interface GraphAlgorithmsInterface<T> {
    //topological order ve shortest cheapest  suan kullanılmıyo kullanilmazsa devamında sileriz
    public QueueInterface<T>  getBreadthFirstTravel(T origin);
    public QueueInterface<T> getDepthFirstTravel(T origin);
    public StackInterface<T>  getTopologicalOrder();
    public int shortestPath (T begin,T end,StackInterface<T> path);
    public double cheapestPath (T begin,T end,StackInterface<T> path);
}
