package network.overlay.dijkstra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by toddw on 2/12/17.
 */
public class Edge {
    private HashSet<Vertex> verts;
    private int weight;

    public Edge(Vertex v1, Vertex v2, int weight) {
        verts = new HashSet<Vertex>();
        verts.add(v1);
        verts.add(v2);
        this.weight = weight;
    }

    public Edge(String edge) {
        ArrayList<String> e = new ArrayList<>(Arrays.asList(edge.split(" ")));
        verts = new HashSet<Vertex>();
        verts.add(new Vertex(e.get(0)));
        verts.add(new Vertex(e.get(1)));
        this.weight = Integer.parseInt(e.get(2));
    }

    public int getWeight() {
        return this.weight;
    }

    public boolean contains(Vertex v) {
        for (Vertex other: verts) {
            if (other.getName().equals(v.getName()))
                return true;
        }
        return false;
    }

    public boolean contains(String v) {
        for (Vertex other: verts) {
            if (other.getName().equals(v))
                return true;
        }
        return false;
    }

    public Vertex getOther(Vertex v) {
        for (Vertex other : verts) {
            if (!(other.getName().equals(v.getName())))
                return other;
        }
        return null;
    }

    public Set<Vertex> getVerts() {
        return this.verts;
    }
}
