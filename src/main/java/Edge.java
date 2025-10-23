import java.util.Objects;

public class Edge implements Comparable<Edge> {
    private final int source;
    private final int destination;
    private final int weight;

    public Edge(int source, int destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public int getSource() { return source; }
    public int getDestination() { return destination; }
    public int getWeight() { return weight; }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return source == edge.source &&
                destination == edge.destination &&
                weight == edge.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination, weight);
    }

    @Override
    public String toString() {
        return String.format("%d-%d:%d", source, destination, weight);
    }
}