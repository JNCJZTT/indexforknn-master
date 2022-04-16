package com.index.indexforknn.openstreetmap.domain;

import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 * 2022/3/6 zhoutao
 */
@Setter
public class OpenStreetMapArc {
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371000;

    private String wayId;

    private List<String> nodeIds;

    public OpenStreetMapArc() {
        nodeIds = new ArrayList<>();
    }

    public void addNodeId(String nodeId) {
        nodeIds.add(nodeId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        OpenStreetMapNode fromNode = OpenStreetMapVariable.NODES.get(nodeIds.get(0));

        for (int i = 1; i < nodeIds.size(); i++) {
            OpenStreetMapNode toNode = OpenStreetMapVariable.NODES.get(nodeIds.get(i));
            int dis = calculateDistanceInKilometer(fromNode.getLat(), fromNode.getLon(), toNode.getLat(), toNode.getLon());
            String edge1 = "a " + fromNode.getName() + " " + toNode.getName() + " " + dis;
            String edge2 = "a " + toNode.getName() + " " + fromNode.getName() + " " + dis;
            sb.append(edge1 + "\r\n").append(edge2 + "\r\n");
            OpenStreetMapVariable.EDGE_SIZE++;

            fromNode = toNode;
        }
        return sb.toString();
    }

    private int calculateDistanceInKilometer(double userLat, double userLng,
                                             double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }
}
