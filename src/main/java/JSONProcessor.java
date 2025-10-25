import java.util.*;
import java.io.*;
import java.nio.file.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class JSONProcessor {
    private static final ObjectMapper mapper = new ObjectMapper();

    // Structures for parsing JSON
    public static class JsonGraph {
        public int id;
        public List<String> nodes;
        public List<JsonEdge> edges;
    }

    public static class JsonEdge {
        public String from;
        public String to;
        public int weight;
    }

    public static class InputData {
        public List<JsonGraph> graphs;
    }

    public static class OutputResult {
        public int graph_id;
        public InputStats input_stats;
        public AlgorithmResult prim;
        public AlgorithmResult kruskal;
    }

    public static class InputStats {
        public int vertices;
        public int edges;
    }

    public static class AlgorithmResult {
        public List<OutputEdge> mst_edges;
        public int total_cost;
        public int operations_count;
        public double execution_time_ms;
    }

    public static class OutputEdge {
        public String from;
        public String to;
        public int weight;

        public OutputEdge(String from, String to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    public static class OutputData {
        public List<OutputResult> results;
    }

    // Read input.json
    public static List<GraphData> readInputFile(String filename) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        InputData inputData = mapper.readValue(content, InputData.class);

        List<GraphData> graphDataList = new ArrayList<>();

        for (JsonGraph jsonGraph : inputData.graphs) {
            Graph graph = Graph.fromJsonData(jsonGraph.nodes, convertEdges(jsonGraph.edges));
            graphDataList.add(new GraphData(jsonGraph.id, jsonGraph.nodes, graph));
        }

        return graphDataList;
    }

    private static List<Map<String, Object>> convertEdges(List<JsonEdge> jsonEdges) {
        List<Map<String, Object>> edges = new ArrayList<>();
        for (JsonEdge jsonEdge : jsonEdges) {
            Map<String, Object> edge = new HashMap<>();
            edge.put("from", jsonEdge.from);
            edge.put("to", jsonEdge.to);
            edge.put("weight", jsonEdge.weight);
            edges.add(edge);
        }
        return edges;
    }

    // Write output.json
    public static void writeOutputFile(String filename, List<OutputResult> results) throws IOException {
        OutputData outputData = new OutputData();
        outputData.results = results;

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), outputData);
    }

    // Convert MST result to output format
    public static OutputResult convertToOutputResult(int graphId, List<String> nodeNames,
                                                     MSTResult primResult, MSTResult kruskalResult) {
        OutputResult result = new OutputResult();
        result.graph_id = graphId;

        // Input statistics
        result.input_stats = new InputStats();
        result.input_stats.vertices = primResult.getVertices();
        result.input_stats.edges = -1; // Will be set later

        // Prim algorithm results
        result.prim = convertAlgorithmResult(primResult, nodeNames);

        // Kruskal algorithm results
        result.kruskal = convertAlgorithmResult(kruskalResult, nodeNames);

        return result;
    }

    private static AlgorithmResult convertAlgorithmResult(MSTResult mstResult, List<String> nodeNames) {
        AlgorithmResult result = new AlgorithmResult();
        result.total_cost = mstResult.getTotalCost();
        result.operations_count = mstResult.getOperationsCount();
        result.execution_time_ms = Math.round(mstResult.getExecutionTime() * 1000.0) / 1000.0; // Rounded to 3 decimals

        // Convert MST edges to output format
        result.mst_edges = new ArrayList<>();
        for (Edge edge : mstResult.getMstEdges()) {
            String fromName = Graph.getNodeName(edge.getSource(), nodeNames);
            String toName = Graph.getNodeName(edge.getDestination(), nodeNames);
            result.mst_edges.add(new OutputEdge(fromName, toName, edge.getWeight()));
        }

        return result;
    }

    // Helper class to store graph-related data
    public static class GraphData {
        public final int id;
        public final List<String> nodeNames;
        public final Graph graph;

        public GraphData(int id, List<String> nodeNames, Graph graph) {
            this.id = id;
            this.nodeNames = nodeNames;
            this.graph = graph;
        }
    }

    // Create a demo graph for OOP demonstration
    public static Graph createDemoGraph() {
        // Create a simple demo graph manually
        Graph graph = new Graph(6);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 2, 1);
        graph.addEdge(1, 3, 5);
        graph.addEdge(2, 3, 8);
        graph.addEdge(2, 4, 10);
        graph.addEdge(3, 4, 2);
        graph.addEdge(3, 5, 6);
        graph.addEdge(4, 5, 3);
        return graph;
    }
}