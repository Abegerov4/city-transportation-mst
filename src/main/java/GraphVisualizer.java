import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Utility class for generating visual representations of graphs
 */
public class GraphVisualizer {

    public static void generateGraphImage(Graph graph, String filename) {
        int width = 800;
        int height = 600;
        int margin = 50;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Setup rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Draw graph info
        drawGraphInfo(g2d, graph, width);

        // Calculate vertex positions
        Point[] positions = calculateVertexPositions(graph, width, height, margin);

        // Draw edges
        drawEdges(g2d, graph, positions);

        // Draw vertices
        drawVertices(g2d, positions);

        // Save image
        try {
            File output = new File("src/main/resources/" + filename);
            ImageIO.write(image, "PNG", output);
            System.out.println("📊 Graph visualization saved: " + filename);
        } catch (IOException e) {
            System.err.println("Failed to save graph image: " + e.getMessage());
        }

        g2d.dispose();
    }

    private static Point[] calculateVertexPositions(Graph graph, int width, int height, int margin) {
        int vertexCount = graph.getVerticesCount(); // Используем существующий метод
        Point[] positions = new Point[vertexCount];

        if (vertexCount == 1) {
            positions[0] = new Point(width / 2, height / 2);
            return positions;
        }

        // Arrange vertices in a circle
        double angleStep = 2 * Math.PI / vertexCount;
        int radius = Math.min(width, height) / 2 - margin;
        int centerX = width / 2;
        int centerY = height / 2;

        for (int i = 0; i < vertexCount; i++) {
            double angle = i * angleStep;
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            positions[i] = new Point(x, y);
        }

        return positions;
    }

    private static void drawEdges(Graphics2D g2d, Graph graph, Point[] positions) {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(2));

        // Получаем все ребра графа
        List<Edge> edges = getAllEdges(graph);

        for (Edge edge : edges) {
            Point p1 = positions[edge.getSource()];
            Point p2 = positions[edge.getDestination()];
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

            // Draw weight
            int midX = (p1.x + p2.x) / 2;
            int midY = (p1.y + p2.y) / 2;
            g2d.setColor(Color.BLUE);
            g2d.drawString(String.valueOf(edge.getWeight()), midX, midY);
            g2d.setColor(Color.LIGHT_GRAY);
        }
    }

    private static void drawVertices(Graphics2D g2d, Point[] positions) {
        g2d.setColor(Color.RED);
        for (int i = 0; i < positions.length; i++) {
            Point p = positions[i];
            g2d.fillOval(p.x - 10, p.y - 10, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.valueOf(i), p.x - 5, p.y + 5);
            g2d.setColor(Color.RED);
        }
    }

    private static void drawGraphInfo(Graphics2D g2d, Graph graph, int width) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));

        // Используем только существующие методы
        String info = String.format("Graph | Vertices: %d | Edges: %d | Connected: %s",
                graph.getVerticesCount(), graph.getEdgesCount(),
                graph.isConnected());
        g2d.drawString(info, 10, 20);

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        String stats = String.format("Total Weight: %d", calculateTotalWeight(graph));
        g2d.drawString(stats, 10, 40);
    }

    // Вспомогательный метод для получения всех ребер
    private static List<Edge> getAllEdges(Graph graph) {
        List<Edge> allEdges = new ArrayList<>();
        int vertices = graph.getVerticesCount();

        // Собираем все уникальные ребра
        for (int i = 0; i < vertices; i++) {
            List<Edge> adjacentEdges = graph.getAdjacentEdges(i);
            for (Edge edge : adjacentEdges) {
                // Добавляем только если source < destination чтобы избежать дубликатов
                if (edge.getSource() <= edge.getDestination()) {
                    allEdges.add(edge);
                }
            }
        }
        return allEdges;
    }

    // Вспомогательный метод для вычисления общего веса
    private static int calculateTotalWeight(Graph graph) {
        int totalWeight = 0;
        List<Edge> edges = getAllEdges(graph);
        for (Edge edge : edges) {
            totalWeight += edge.getWeight();
        }
        return totalWeight;
    }
}