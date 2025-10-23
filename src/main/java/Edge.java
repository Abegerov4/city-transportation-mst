import java.util.Objects;

/**
 * Represents a weighted edge in a graph with proper encapsulation
 * and object-oriented design principles.
 */
public class Edge implements Comparable<Edge> {
    private final int source;
    private final int destination;
    private final int weight;

    public Edge(int source, int destination, int weight) {
        validateVertex(source);
        validateVertex(destination);
        validateWeight(weight);

        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    // Factory method for creating edges
    public static Edge of(int source, int destination, int weight) {
        return new Edge(source, destination, weight);
    }

    // Getters with proper encapsulation
    public int getSource() { return source; }
    public int getDestination() { return destination; }
    public int getWeight() { return weight; }

    // Business logic methods
    public int getOtherVertex(int vertex) {
        if (vertex == source) return destination;
        if (vertex == destination) return source;
        throw new IllegalArgumentException("Vertex " + vertex + " is not incident to this edge");
    }

    public boolean isIncidentTo(int vertex) {
        return vertex == source || vertex == destination;
    }

    public Edge reversed() {
        return new Edge(destination, source, weight);
    }

    // Validation methods
    private void validateVertex(int vertex) {
        if (vertex < 0) {
            throw new IllegalArgumentException("Vertex index must be non-negative");
        }
    }

    private void validateWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Edge weight must be non-negative");
        }
    }

    // Object contract methods
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
        return String.format("Edge{%d-%d, weight=%d}", source, destination, weight);
    }
}