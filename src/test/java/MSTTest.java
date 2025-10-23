import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MSTTest {

    @Test
    void testEdgeComparison() {
        Edge edge1 = new Edge(0, 1, 5);
        Edge edge2 = new Edge(1, 2, 3);
        Edge edge3 = new Edge(2, 3, 5);

        assertTrue(edge2.compareTo(edge1) < 0); // edge2 should be less than edge1
        assertEquals(0, edge1.compareTo(edge3)); // equal weights
    }

    @Test
    void testGraphConnectivity() {
        Graph connectedGraph = new Graph(4);
        connectedGraph.addEdge(0, 1, 1);
        connectedGraph.addEdge(1, 2, 1);
        connectedGraph.addEdge(2, 3, 1);

        Graph disconnectedGraph = new Graph(4);
        disconnectedGraph.addEdge(0, 1, 1);
        disconnectedGraph.addEdge(2, 3, 1);

        assertTrue(connectedGraph.isConnected());
        assertFalse(disconnectedGraph.isConnected());
    }

    @Test
    void testMSTAlgorithms() {
        Graph graph = createTestGraph();

        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        // Both algorithms should find MST with same cost
        assertEquals(primResult.getTotalCost(), kruskalResult.getTotalCost());

        // MST should have V-1 edges
        assertEquals(graph.getVerticesCount() - 1, primResult.getMstEdgesCount());
        assertEquals(graph.getVerticesCount() - 1, kruskalResult.getMstEdgesCount());

        // Results should be valid
        assertTrue(primResult.isValidMST());
        assertTrue(kruskalResult.isValidMST());
    }

    @Test
    void testMSTResultValidation() {
        Graph graph = createTestGraph();
        PrimMST prim = new PrimMST();
        MSTResult result = prim.findMST(graph);

        assertTrue(result.hasCorrectEdgeCount());
        assertTrue(result.getTotalCost() > 0);
        assertTrue(result.getExecutionTime() >= 0);
        assertTrue(result.getOperationsCount() > 0);
    }

    @Test
    void testGraphEdgeOperations() {
        Graph graph = new Graph(3);
        assertEquals(0, graph.getEdgesCount());

        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);

        assertEquals(2, graph.getEdgesCount());
        assertEquals(3, graph.getVerticesCount());
        assertEquals(1, graph.getAdjacentEdges(0).size());
        assertEquals(2, graph.getAdjacentEdges(1).size());
    }

    @Test
    void testEdgeEquality() {
        Edge edge1 = new Edge(0, 1, 5);
        Edge edge2 = new Edge(0, 1, 5);
        Edge edge3 = new Edge(1, 0, 5); // Different direction
        Edge edge4 = new Edge(0, 1, 3); // Different weight

        assertEquals(edge1, edge2);
        assertNotEquals(edge1, edge3);
        assertNotEquals(edge1, edge4);
    }

    private Graph createTestGraph() {
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