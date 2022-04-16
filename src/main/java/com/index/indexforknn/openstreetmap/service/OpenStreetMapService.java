package com.index.indexforknn.openstreetmap.service;

import com.index.indexforknn.base.common.constants.Constants;
import com.index.indexforknn.openstreetmap.domain.OpenStreetMapArc;
import com.index.indexforknn.openstreetmap.domain.OpenStreetMapNode;
import com.index.indexforknn.openstreetmap.domain.OpenStreetMapVariable;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * 处理openstreetmap上的数据
 * 2022/3/6 zhoutao
 */
@Service
public class OpenStreetMapService {


    private final static String MAP = "BJ";

    // 原始OpenStreetMap文件路径
    private final static String ORIGINAL_MAP_URL = Constants.BASE_URL + MAP + "/" + MAP + "-openstreetmap.txt";



    /**
     * 读取OpenStreetMap文件路径，生成结点文件和边文件
     */
    public void GenerateVertexAndEdgeFile() throws IOException, DocumentException {
        // 生成的路径
        File pointFile = new File(Constants.BASE_URL + MAP + "/USA-road-d." + MAP + ".co");
        File arcFile = new File(Constants.BASE_URL + MAP + "/USA-road-d." + MAP + ".gr");

        FileOutputStream fosPoint = new FileOutputStream(pointFile);
        FileOutputStream fosArc = new FileOutputStream(arcFile);
        OutputStreamWriter oswPoint = new OutputStreamWriter(fosPoint);
        OutputStreamWriter oswArc = new OutputStreamWriter(fosArc);
        BufferedWriter bwPoint = new BufferedWriter(oswPoint);
        BufferedWriter bwArc = new BufferedWriter(oswArc);

        // 读取文档
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(ORIGINAL_MAP_URL));
        Element root = document.getRootElement();

        OpenStreetMapVariable.NODE_SIZE = 0;
        OpenStreetMapVariable.EDGE_SIZE = 0;

        Iterator<Element> iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();

            //输出点信息
            if (e.getName().equals("node")) {
                OpenStreetMapNode node = new OpenStreetMapNode();
                //首先获取当前节点的所有属性节点
                List<Attribute> list = e.attributes();
                // 从1开始
                node.setName(++OpenStreetMapVariable.NODE_SIZE);
                //遍历属性节点
                for (Attribute attribute : list) {
                    if (attribute.getName().equals("id"))
                        node.setId(attribute.getValue());
                    if (attribute.getName().equals("lat"))
                        node.setLat(attribute.getValue());
                    if (attribute.getName().equals("lon"))
                        node.setLon(attribute.getValue());
                }
                OpenStreetMapVariable.NODES.put(node.getId(), node);

                bwPoint.write(node.toString());
                bwPoint.flush();
            } else if (e.getName().equals("way")) {  //输出弧信息

                OpenStreetMapArc arc = new OpenStreetMapArc();
                //首先获取当前节点的所有属性节点
                List<Attribute> list = e.attributes();
                //遍历属性节点
                for (Attribute attribute : list) {
                    if (attribute.getName().equals("id"))
                        arc.setWayId(attribute.getValue());
                }
                //遍历子节点
                Iterator<Element> iter = e.elementIterator();
                while (iter.hasNext()) {
                    Element element = iter.next();
                    //首先获取当前节点的所有属性节点
                    List<Attribute> list1 = element.attributes();
                    //遍历属性节点
                    for (Attribute attribute : list1) {
                        if (attribute.getName().equals("ref"))
                            arc.addNodeId(attribute.getValue());
                    }
                }
                bwArc.write(arc.toString());
                bwArc.flush();
            }
        }
        System.out.println("nodeSize=" + OpenStreetMapVariable.NODE_SIZE);
        System.out.println("edgeSize=" + OpenStreetMapVariable.EDGE_SIZE);
    }


}
