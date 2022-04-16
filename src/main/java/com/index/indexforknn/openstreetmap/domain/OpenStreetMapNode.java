package com.index.indexforknn.openstreetmap.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 * 2022/3/6 zhoutao
 */
@Getter
@Setter
public class OpenStreetMapNode {
    private int name;

    private String id;

    private String lat;

    private String lon;

    @Override
    public String toString() {
        return "v " + name + " " + lon + " " + lat + "\r\n";
    }

    public double getLat() {
        return Double.parseDouble(lat);
    }

    public double getLon() {
        return Double.parseDouble(lon);
    }

}
