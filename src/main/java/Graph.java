import java.util.*;
import java.io.*;
import java.nio.file.*;

public class Graph {
    private final int vertices;
    private final List<Edge> edges;
    private final List<List<Edge>> adjacencyList;

    public Graph(int vertices) {
        this.vertices = vertices;
        this.edges = new ArrayList<>();
        this.adjacencyList = new ArrayList<>();

        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int source, int destination, int weight) {
        if (source < 0 || source >= vertices || destination < 0 || destination >= vertices) {
            throw new IllegalArgumentException("Invalid vertex index");
        }

        Edge edge = new Edge(source, destination, weight);
        edges.add(edge);
        adjacencyList.get(source).add(edge);
        adjacencyList.get(destination).add(new Edge(destination, source, weight));
    }

    // Добавьте эти методы - они заменят getVerticesCount() и getEdgesCount()
    public int getVertices() { return vertices; }
    public int getEdges() { return edges.size(); }

    // Для обратной совместимости тоже добавим
    public int getVerticesCount() { return vertices; }
    public int getEdgesCount() { return edges.size(); }

    public List<Edge> getEdgesList() { return new ArrayList<>(edges); }
    public List<Edge> getAdjacentEdges(int vertex) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException("Invalid vertex index");
        }
        return new ArrayList<>(adjacencyList.get(vertex));
    }

    public boolean isConnected() {
        if (vertices == 0) return true;

        boolean[] visited = new boolean[vertices];
        int visitedCount = dfs(0, visited);
        return visitedCount == vertices;
    }

    private int dfs(int vertex, boolean[] visited) {
        visited[vertex] = true;
        int count = 1;

        for (Edge edge : adjacencyList.get(vertex)) {
            int neighbor = edge.getDestination();
            if (!visited[neighbor]) {
                count += dfs(neighbor, visited);
            }
        }
        return count;
    }

    // Новый метод: создание графа из JSON данных
    public static Graph fromJsonData(List<String> nodes, List<Map<String, Object>> edgesData) {
        // Создаем маппинг имен узлов на индексы
        Map<String, Integer> nodeToIndex = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeToIndex.put(nodes.get(i), i);
        }

        Graph graph = new Graph(nodes.size());

        // Добавляем все ребра
        for (Map<String, Object> edgeData : edgesData) {
            String from = (String) edgeData.get("from");
            String to = (String) edgeData.get("to");
            int weight = ((Number) edgeData.get("weight")).intValue();

            int fromIndex = nodeToIndex.get(from);
            int toIndex = nodeToIndex.get(to);

            graph.addEdge(fromIndex, toIndex, weight);
        }

        return graph;
    }

    // Метод для получения имени узла по индексу (для output)
    public static String getNodeName(int index, List<String> nodeNames) {
        if (index >= 0 && index < nodeNames.size()) {
            return nodeNames.get(index);
        }
        return "Unknown";
    }

    @Override
    public String toString() {
        return String.format("Graph(V=%d, E=%d)", vertices, edges.size());
    }
}