import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class PerformanceTest {

    @Test
    void testPerformanceConsistency() {
        Graph graph = createPerformanceTestGraph();

        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        List<Integer> primCosts = new ArrayList<>();
        List<Integer> kruskalCosts = new ArrayList<>();

        // Run multiple times to check consistency
        for (int i = 0; i < 3; i++) {
            MSTResult primResult = prim.findMST(graph);
            MSTResult kruskalResult = kruskal.findMST(graph);

            primCosts.add(primResult.getTotalCost());
            kruskalCosts.add(kruskalResult.getTotalCost());

            // Costs should always be the same within each algorithm
            assertEquals(primCosts.get(0), primResult.getTotalCost(),
                    "Prim's algorithm should return consistent costs");
            assertEquals(kruskalCosts.get(0), kruskalResult.getTotalCost(),
                    "Kruskal's algorithm should return consistent costs");
        }

        // Both algorithms should consistently find the same cost
        assertEquals(primCosts.get(0), kruskalCosts.get(0),
                "Both algorithms should find the same MST cost");
    }

    @Test
    void testOperationCounts() {
        Graph smallGraph = createSmallGraph();
        Graph largeGraph = createLargeGraph();

        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        MSTResult smallPrim = prim.findMST(smallGraph);
        MSTResult largePrim = prim.findMST(largeGraph);

        MSTResult smallKruskal = kruskal.findMST(smallGraph);
        MSTResult largeKruskal = kruskal.findMST(largeGraph);

        // Larger graphs should generally require more operations
        assertTrue(largePrim.getOperationsCount() > smallPrim.getOperationsCount(),
                "Prim's algorithm should use more operations for larger graphs");
        assertTrue(largeKruskal.getOperationsCount() > smallKruskal.getOperationsCount(),
                "Kruskal's algorithm should use more operations for larger graphs");
    }

    @Test
    void testExecutionTime() {
        Graph graph = createPerformanceTestGraph();

        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        // Execution time should be non-negative
        assertTrue(primResult.getExecutionTime() >= 0,
                "Prim's execution time should be non-negative");
        assertTrue(kruskalResult.getExecutionTime() >= 0,
                "Kruskal's execution time should be non-negative");

        // Operations count should be positive
        assertTrue(primResult.getOperationsCount() > 0,
                "Prim's operations count should be positive");
        assertTrue(kruskalResult.getOperationsCount() > 0,
                "Kruskal's operations count should be positive");
    }

    private Graph createPerformanceTestGraph() {
        Graph graph = new Graph(10);
        // Create a consistent test graph
        for (int i = 0; i < 9; i++) {
            graph.addEdge(i, i + 1, (i * 3 + 1) % 7 + 1);
        }
        graph.addEdge(0, 5, 4);
        graph.addEdge(2, 7, 3);
        graph.addEdge(4, 9, 2);
        return graph;
    }

    private Graph createSmallGraph() {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 2, 1);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 4, 5);
        graph.addEdge(3, 4, 6);
        return graph;
    }

    private Graph createLargeGraph() {
        Graph graph = new Graph(15);
        for (int i = 0; i < 14; i++) {
            for (int j = i + 1; j < Math.min(i + 4, 15); j++) {
                graph.addEdge(i, j, (i + j) % 10 + 1);
            }
        }
        return graph;
    }
}