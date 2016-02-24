package com.adavieslyons.util.map.pathfinding;

import java.util.*;

/**
 * @author Ashley
 *         <p/>
 *         <p/>
 *         "Path" method mostly credits to WhiteFang on StackOverflow; see
 *         http://stackoverflow.com/questions/5601889/
 */
public class AStar {
    private static List<Node> getNeighbors(Node[][] matrix, int x, int y) {
        List<Node> neighbors = new ArrayList<>();

        if (y > 0 && matrix[y - 1][x] != null)
            neighbors.add(matrix[y - 1][x]);
        if (x > 0 && matrix[y][x - 1] != null)
            neighbors.add(matrix[y][x - 1]);
        if (y < matrix.length - 1 && matrix[y + 1][x] != null)
            neighbors.add(matrix[y + 1][x]);
        if (x < matrix[0].length - 1 && matrix[y][x + 1] != null)
            neighbors.add(matrix[y][x + 1]);

        return neighbors;
    }

    public static Node[][] getNodeMatrix(CollisionMap map) {
        Node[][] matrix;

        matrix = new Node[map.getRows()][map.getColumns()];

        for (int y = 0; y < map.getRows(); y++)
            for (int x = 0; x < map.getColumns(); x++)
                if (!map.getCollision(x, y)) {
                    matrix[y][x] = new Node();
                    matrix[y][x].mapReference = map;
                    matrix[y][x].x = x;
                    matrix[y][x].y = y;
                }

        // Form neighbourly relations. More convenient [Only possible way with
        // this system?] to do this in a second loop after all nodes made.
        for (int y = 0; y < map.getRows(); y++)
            for (int x = 0; x < map.getColumns(); x++)
                if (matrix[y][x] != null) {
                    matrix[y][x].neighbors = getNeighbors(matrix, x, y);
                }

        return matrix;
    }

    public static List<Node> path(Node beginning, Node end) {
        beginning.g = 0;
        beginning.h = heuristic(beginning, end);
        beginning.f = beginning.h;
        beginning.parent = null;

        Set<Node> open = new HashSet<>();
        Set<Node> closed = new HashSet<>();

        open.add(beginning);

        while (true) {
            Node current = null;

            if (open.size() == 0)
                return null;

            for (Node node : open)
                if (current == null || node.f < current.f)
                    current = node;

            if (current == end)
                break;

            open.remove(current);
            closed.add(current);

            for (Node neighbor : current.neighbors) {
                if (neighbor == null)
                    continue;

                // Ignore neighbor if occupied
                if (beginning.mapReference.getOccupied(neighbor.x, neighbor.y))
                    continue;

                int g = current.g + neighbor.cost;

                if (g < neighbor.g) {
                    open.remove(neighbor);
                    closed.remove(neighbor);
                }

                if (!open.contains(neighbor) && !closed.contains(neighbor)) {
                    neighbor.g = g;
                    neighbor.h = heuristic(neighbor, end);
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;
                    open.add(neighbor);
                }
            }
        }

        List<Node> returnNodes = new ArrayList<>();

        Node current = end;

        while (current.parent != null) {
            returnNodes.add(current);
            current = current.parent;
        }
        // returnNodes.add(beginning);
        Collections.reverse(returnNodes);

        return returnNodes;
    }

    private static int heuristic(Node beginning, Node end) {
        return (Math.abs(beginning.x - end.x) + Math.abs(beginning.y - end.y));
    }

    public static class Node {
        Node parent;
        CollisionMap mapReference;
        List<Node> neighbors = new ArrayList<>();
        int f;
        int g;
        int h;
        int x;
        int y;
        int cost;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
