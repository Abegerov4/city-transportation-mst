import java.util.*;

public class MSTAnalyzer {

    public static void analyzeGraph(Graph graph, String graphName) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ANALYZING: " + graphName);
        System.out.println("=".repeat(60));

        System.out.println("Graph Properties:");
        System.out.println("  Vertices: " + graph.getVerticesCount());
        System.out.println("  Edges: " + graph.getEdgesCount());
        System.out.println("  Connected: " + graph.isConnected());

        if (!graph.isConnected()) {
            System.out.println(" Cannot compute MST - Graph is disconnected!");
            return;
        }

        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        System.out.println("\nComputing MSTs...");

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        System.out.println("\nRESULTS:");
        System.out.println(primResult);
        System.out.println(kruskalResult);

        // Validation
        System.out.println("\nVALIDATION:");
        boolean costsMatch = primResult.getTotalCost() == kruskalResult.getTotalCost();
        boolean primValid = primResult.isValidMST();
        boolean kruskalValid = kruskalResult.isValidMST();

        System.out.println("  ✓ MST costs match: " + costsMatch);
        System.out.println("  ✓ Prim's MST valid: " + primValid);
        System.out.println("  ✓ Kruskal's MST valid: " + kruskalValid);
        System.out.println("  ✓ Correct edge count: " + primResult.hasCorrectEdgeCount());

        if (costsMatch && primValid && kruskalValid) {
            System.out.println("\n ALL VALIDATIONS PASSED!");
        } else {
            System.out.println("\n SOME VALIDATIONS FAILED!");
        }

        // Performance comparison
        System.out.println("\nPERFORMANCE COMPARISON:");
        System.out.printf("  Time Ratio (Prim/Kruskal): %.2f\n",
                (double) primResult.getExecutionTime() / kruskalResult.getExecutionTime());
        System.out.printf("  Operations Ratio (Prim/Kruskal): %.2f\n",
                (double) primResult.getOperationsCount() / kruskalResult.getOperationsCount());
    }

    public static void runPerformanceAnalysis() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PERFORMANCE ANALYSIS");
        System.out.println("=".repeat(60));

        List<Graph> testGraphs = Arrays.asList(
                createSmallGraph(),
                createMediumGraph(),
                createLargeGraph(),
                createDenseGraph()
        );

        List<String> graphNames = Arrays.asList(
                "Small Graph (6 vertices)",
                "Medium Graph (12 vertices)",
                "Large Graph (20 vertices)",
                "Dense Graph (15 vertices)"
        );

        for (int i = 0; i < testGraphs.size(); i++) {
            analyzeGraph(testGraphs.get(i), graphNames.get(i));
        }
    }

    // Test graph generators
    private static Graph createSmallGraph() {
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

    private static Graph createMediumGraph() {
        Graph graph = new Graph(12);
        // Create a connected graph
        for (int i = 0; i < 11; i++) {
            graph.addEdge(i, i + 1, (i * 2 + 1) % 10 + 1);
        }
        // Add cross edges
        graph.addEdge(0, 5, 4);
        graph.addEdge(3, 8, 3);
        graph.addEdge(6, 11, 5);
        graph.addEdge(2, 9, 2);
        graph.addEdge(4, 10, 6);
        graph.addEdge(1, 7, 7);
        return graph;
    }

    private static Graph createLargeGraph() {
        Graph graph = new Graph(20);
        // Create a grid-like structure
        for (int i = 0; i < 20; i++) {
            for (int j = i + 1; j < Math.min(i + 5, 20); j++) {
                int weight = (i + j) % 10 + 1;
                graph.addEdge(i, j, weight);
            }
        }
        return graph;
    }

    private static Graph createDenseGraph() {
        Graph graph = new Graph(15);
        // Very dense graph
        for (int i = 0; i < 15; i++) {
            for (int j = i + 1; j < 15; j++) {
                if (Math.random() > 0.2) { // 80% density
                    int weight = (int) (Math.random() * 20) + 1;
                    graph.addEdge(i, j, weight);
                }
            }
        }
        return graph;
    }
}