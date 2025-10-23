import java.util.*;

public class PrimMST {
    private int comparisonCount;
    private int assignmentCount;
    private int queueOperations;

    public MSTResult findMST(Graph graph) {
        long startTime = System.nanoTime();
        resetCounters();

        int vertices = graph.getVerticesCount();
        boolean[] inMST = new boolean[vertices];
        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;

        // Priority queue to always get the minimum weight edge
        PriorityQueue<Edge> pq = new PriorityQueue<>((e1, e2) -> {
            comparisonCount++;
            return Integer.compare(e1.getWeight(), e2.getWeight());
        });

        // Start from vertex 0
        inMST[0] = true;
        assignmentCount++;

        // Add all edges from vertex 0 to the priority queue
        for (Edge edge : graph.getAdjacentEdges(0)) {
            pq.offer(edge);
            queueOperations++;
        }

        while (!pq.isEmpty() && mstEdges.size() < vertices - 1) {
            Edge edge = pq.poll();
            queueOperations++;

            int u = edge.getSource();
            int v = edge.getDestination();

            // Skip if both vertices are already in MST (would create cycle)
            comparisonCount++;
            if (inMST[u] && inMST[v]) {
                continue;
            }

            // Add edge to MST
            mstEdges.add(edge);
            totalCost += edge.getWeight();
            assignmentCount += 2;

            // Find the vertex not yet in MST and add its edges
            int newVertex = inMST[u] ? v : u;
            inMST[newVertex] = true;
            assignmentCount += 2;

            // Add all edges from the new vertex to priority queue
            for (Edge adjEdge : graph.getAdjacentEdges(newVertex)) {
                comparisonCount++;
                if (!inMST[adjEdge.getDestination()]) {
                    pq.offer(adjEdge);
                    queueOperations++;
                }
            }
        }

        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0; // Дробные миллисекунды
        int totalOperations = comparisonCount + assignmentCount + queueOperations;

        return new MSTResult("Prim's Algorithm", mstEdges, totalCost,
                executionTimeMs, totalOperations, vertices);
    }

    private void resetCounters() {
        comparisonCount = 0;
        assignmentCount = 0;
        queueOperations = 0;
    }
}