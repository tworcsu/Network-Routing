package network.overlay.dijkstra;

/**
 * Created by toddw on 2/12/17.
 */
public class Vertex {
    private String name;

    public Vertex(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vertex other = (Vertex) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
