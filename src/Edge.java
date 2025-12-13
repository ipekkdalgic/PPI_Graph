public class Edge<T> {
    private  T begin;
    private  T end;
    private  double weight ;


    public Edge(T begin) {
        if(begin == null || end == null)
            throw new IllegalArgumentException("Edge endpoints cannot be null.");
        this.begin = begin;
        this.end = end;
        this.weight= weight;

    }

    public Edge(String begin, String end, double weight) {
        if(begin == null || end == null)
            throw new IllegalArgumentException("Edge endpoints cannot be null.");
        this.begin = (T) begin;
        this.end = (T) end;
        this.weight= weight;
    }

    public T getBegin() {
        return begin;
    }

    public T getEnd() {
        return end;
    }

    public double getWeight() {
        return weight;
    }
    @Override
    public String toString() {
        return begin + " -> " + end + " (" + weight + ")";
    }

}
