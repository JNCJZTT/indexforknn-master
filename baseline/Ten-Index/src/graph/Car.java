/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.Objects;

import static buildindex.BuildIndex.AllVertices;
import static buildindex.BuildIndex.random;

/**
 * @author 江南才俊周涛涛
 */
public class Car {

    public int CarName;
    public int Dis;                                                             //到活跃点距离
    public int QueryDis;                                                        //到查询点距离
    public Edge CarE;                                                           //移动对象所在的边
    public int TrueDis = -1;

    //构建汽车
    public Car(int CarName, Edge e) {
        this.CarE = e;
        this.CarName = CarName;
        this.Dis = random.nextInt(e.distence);
    }

    public Car(int CarName){
        this.CarName=CarName;
        this.Dis=0;
    }

    public Car(int CarName,int ToName){


        this.CarName=CarName;
        this.CarE=new Edge(ToName,AllVertices.get(ToName).GetRandomNode());
        this.Dis=random.nextInt(CarE.distence);
    }

    public boolean isUpdate(){
        Dis -= random.nextInt(Math.max(CarE.distence /4,100));
        if (Dis < 0) {
            return true;
        }
        return false;
    }

    //更新汽车位置
    public Vertex UpdateLocation(){

        this.CarE=AllVertices.get(this.CarE.to).GetRandomEdge();
        this.Dis=random.nextInt(this.CarE.distence);
        return AllVertices.get(this.CarE.to);
    }

    //设置到查询点到距离
    public void SetQueryDis(int ToDis){
        this.QueryDis=this.Dis+ToDis;
    }

    //返回移动对象驶向的节点
    public int GetToName(){
        return CarE.to;
    }

    public Vertex GetToVertex(){
        return AllVertices.get(CarE.to);
    }

    public boolean equals(Object temp) {
        if (temp == this) {
            return true;
        }
        if (!(temp instanceof Car)) {
            return false;
        }
        Car c = (Car) temp;
        return CarName == c.CarName;
    }

    public int hashCode() { return Objects.hash(CarName); }
}
