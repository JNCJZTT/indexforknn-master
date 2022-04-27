package graph;

import java.util.HashMap;
import java.util.HashSet;

public class ClusterLink {
    public int vertexName;
    private HashMap<Integer, int[]> clusterDisMap;         //邻接表形式的ClusterDis
    private Vnode LNAV;

    public ClusterLink(int vertexName) {
        this.vertexName = vertexName;
        clusterDisMap = new HashMap<>();
        LNAV = new Vnode(-1, -1);
    }

    // 设置邻接距离
    public void setClusterDis(Integer to, int dis) {
        if (clusterDisMap.containsKey(to)) {
            clusterDisMap.get(to)[0] = Math.min(clusterDisMap.get(to)[0], dis);
        } else {
            clusterDisMap.put(to, new int[]{dis});
        }
    }

    public int getClusterDis(Integer to) {
        if (!clusterDisMap.containsKey(to)) return -1;
        return clusterDisMap.get(to)[0];
    }

    public HashMap<Integer, int[]> getClusterDisMap() {
        return clusterDisMap;
    }

    public void setSelf() {
        LNAV.setVnode(vertexName, 0);
    }

    public boolean updateLNAV(int activeName, int dis) {
        if (dis == -1) System.out.println("error!");
        if (LNAV.name != -1 && LNAV.dis <= dis) return false;
        LNAV.setVnode(activeName, dis);
        return true;
    }

    public boolean equalLNAV(int activeName) {
        return LNAV.name == activeName;
    }

    public Vnode getLNAV() {
        return LNAV;
    }

    public void resetLNAV() {
        LNAV.setVnode(-1, -1);
    }

    public void setLNAV(Vnode childLNAV) {
        this.LNAV.setVnode(childLNAV.name, childLNAV.dis);
    }
}
