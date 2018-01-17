package network.overlay.dijkstra;

import java.util.*;

/**
 * Created by toddw on 2/13/17.
 */
public class Graph {
    private Map<String, Vertex> vertices;
    private Set<Edge> edges;

    public Graph(String weightList) {
        vertices = new HashMap<>();
        edges = new HashSet<>();
        setEdges(weightList);
        setVertices(this.edges);
    }

    private void setEdges(String weightList) {
        ArrayList<String> edges = new ArrayList<>(Arrays.asList(weightList.split("\n")));
        for (int i = 0; i < edges.size(); i++) {
            this.edges.add(new Edge(edges.get(i)));
        }
    }

    private void setVertices(Set<Edge> edges) {
        for (Edge e : this.edges) {
            for (Vertex v : e.getVerts()) {
                if (!vertices.containsKey(v.getName()))
                    this.vertices.put(v.getName(), v);
            }
        }

    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public Map<String,Vertex> getVertices() {
        return vertices;
    }
}
