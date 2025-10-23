import java.util.*;

public class KruskalMST {
    private int comparisonCount;
    private int assignmentCount;
    private int unionFindOperations;

    public MSTResult findMST(Graph graph) {
        long startTime = System.nanoTime();
        resetCounters();

        List<Edge> edges = new ArrayList<>(graph.getEdges());
        int vertices = graph.getVerticesCount();
        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;

        // Sort edges by weight with operation counting
        Collections.sort(edges, (e1, e2) -> {
            comparisonCount++;
            return Integer.compare(e1.getWeight(), e2.getWeight());
        });

        // Approximate sort operations (log(n) comparisons per element)
        comparisonCount += (int) (edges.size() * Math.log(edges.size()));

        UnionFind uf = new UnionFind(vertices);
        assignmentCount += vertices; // Initialize Union-Find

        for (Edge edge : edges) {
            comparisonCount++;

            if (mstEdges.size() == vertices - 1) break;

            int root1 = uf.find(edge.getSource());
            int root2 = uf.find(edge.getDestination());
            unionFindOperations += 2;

            // If including this edge doesn't cause cycle, include it in MST
            comparisonCount++;
            if (root1 != root2) {
                mstEdges.add(edge);
                totalCost += edge.getWeight();
                uf.union(root1, root2);
                assignmentCount += 3;
                unionFindOperations++;
            }
        }

        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;
        int totalOperations = comparisonCount + assignmentCount + unionFindOperations;

        return new MSTResult("Kruskal's Algorithm", mstEdges, totalCost,
                executionTimeMs, totalOperations, vertices);
    }

    private void resetCounters() {
        comparisonCount = 0;
        assignmentCount = 0;
        unionFindOperations = 0;
    }

    // Union-Find (Disjoint Set Union) data structure
    private class UnionFind {
        private final int[] parent;
        private final int[] rank;

        public UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        public int find(int x) {
            comparisonCount++;
            if (parent[x] != x) {
                assignmentCount++;
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            comparisonCount++;
            if (rootX != rootY) {
                // Union by rank
                comparisonCount++;
                if (rank[rootX] < rank[rootY]) {
                    assignmentCount++;
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]) {
                    comparisonCount++;
                    assignmentCount++;
                    parent[rootY] = rootX;
                } else {
                    comparisonCount++;
                    assignmentCount += 2;
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }
    }
}