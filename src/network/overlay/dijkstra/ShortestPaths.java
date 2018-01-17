package network.overlay.dijkstra;

import java.util.*;

/**
 * Created by toddw on 2/12/17.
 */
public class ShortestPaths {
    private List<Vertex> vertices;
    private List<Edge> edges;
    private Set<Vertex> settled;
    private Set<Vertex> unsettled;
    private Map<String,String> predecessors;
    private Map<String,Integer> weights;

    private Vertex source;

    public ShortestPaths(Graph graph) {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        this.vertices.addAll(graph.getVertices().values());
        this.edges.addAll(graph.getEdges());
    }

    public void computePaths(Vertex source) {
        this.source = source;
        settled = new HashSet<Vertex>();
        unsettled = new HashSet<Vertex>();
        weights = new HashMap<String, Integer>();
        predecessors = new HashMap<String, String>();
        weights.put(source.getName(), 0);
        unsettled.add(source);
        while (unsettled.size() > 0) {
            Vertex vertex = getMinVertex(unsettled);
            settled.add(vertex);
            unsettled.remove(vertex);
            findMinWeights(vertex);
        }
    }

    public void findMinWeights(Vertex vertex) {
        List<Vertex> adjacentNodes = getNeighbors(vertex);
        for (Vertex target : adjacentNodes) {
            if (getMinWeight(target) > (getMinWeight(vertex) + getEdgeWeight(vertex, target))) {
                weights.put(target.getName(), getMinWeight(vertex) + getEdgeWeight(vertex, target));
                predecessors.put(target.getName(), vertex.getName());
                unsettled.add(target);
            }
        }

    }

    private boolean isSettled(Vertex vertex) {
        for (Vertex v : settled) {
            if (v.getName().equals(vertex.getName()))
                return true;
        }
        return false;
    }

    private int getMinWeight(Vertex destination) {
        Integer d = null;
        for (String v : weights.keySet()) {
            if (v.equals(destination.getName()))
                d = weights.get(v);
        }
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    private int getEdgeWeight(Vertex vertex, Vertex target) {
        for (Edge edge : edges) {
            if (edge.contains(vertex) && edge.contains(target)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Something went wrong");
    }

    private int getEdgeWeightByName(String vertex, String target) {
        for (Edge edge : edges) {
            if (edge.contains(vertex) && edge.contains(target)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Something went wrong");
    }

    private List<Vertex> getNeighbors(Vertex vertex) {
        List<Vertex> neighbors = new ArrayList<Vertex>();
        for (Edge edge : edges) {
            if ((edge.contains(vertex)) && !isSettled(edge.getOther(vertex))) {
                neighbors.add(edge.getOther(vertex));
            }
        }
        return neighbors;
    }
    private Vertex getMinVertex(Set<Vertex> vertices) {
        Vertex minimum = null;
        for (Vertex vertex : vertices) {
            if (minimum == null)
                minimum = vertex;
            else if (getMinWeight(vertex) < getMinWeight(minimum))
                    minimum = vertex;
            }
        return minimum;
    }

    public String getPath(String target) {
        String path = "";
        String pred = target;
        if (predecessors.get(pred) == null)
            return null;
        path = pred + path;
        while (predecessors.get(pred) != null) {
            pred = predecessors.get(pred);
            path = pred + " " + path;
        }
        // Knock off source node
        int index = path.indexOf(' ');
        path = path.substring(index + 1);
        return path.trim();
    }

    public ArrayList<String> getPaths() {
        ArrayList<String> paths = new ArrayList<>();
        for (Vertex vertex : vertices) {
            if (!vertex.equals(this.source)) {
                paths.add(getPath(vertex.getName()));
            }
        }
        return paths;
    }

    public String pathWithWeights(String target) {
       String path = "";
       String pred = target;
        if (predecessors.get(pred) == null)
            return null;
        path = pred + path;
        while (predecessors.get(pred) != null) {
            path = "--" + Integer.toString(getEdgeWeightByName(predecessors.get(pred), pred)) + "--" + path;
            pred = predecessors.get(pred);
            path = pred + path;
        }
        return path;
    }

    public void printPaths() {
        for (Vertex vertex : vertices) {
            if (pathWithWeights(vertex.getName()) !=null) {
                System.out.println(pathWithWeights(vertex.getName()));
            }
        }
    }

    public static void main (String[] args) {
        String weightlist =
                "10.0.0.203:55590 10.0.0.203:55573 7\n" +
                "10.0.0.203:55590 10.0.0.203:55584 4\n" +
                "10.0.0.203:55573 10.0.0.203:55584 3\n" +
                "10.0.0.203:55573 10.0.0.203:55579 4\n" +
                "10.0.0.203:55584 10.0.0.203:55579 10\n" +
                "10.0.0.203:55584 10.0.0.203:55576 8\n" +
                "10.0.0.203:55579 10.0.0.203:55576 4\n" +
                "10.0.0.203:55579 10.0.0.203:55587 1\n" +
                "10.0.0.203:55576 10.0.0.203:55587 10\n" +
                "10.0.0.203:55576 10.0.0.203:55590 1\n" +
                "10.0.0.203:55587 10.0.0.203:55590 10\n" +
                "10.0.0.203:55587 10.0.0.203:55573 1";
        ShortestPaths sh = new ShortestPaths(new Graph(weightlist));
        sh.computePaths(new Vertex("10.0.0.203:55590"));
        sh.printPaths();
        System.out.println(sh.getPaths());
    }

}

