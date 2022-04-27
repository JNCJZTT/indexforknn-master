package graph;


import static common.Constants.CAR_SPEED_LIMIT;
import static common.Constants.RANDOM;
import static common.GlobalVariable.allVertices;

public class Car {
    public int carName;
    public int curDis;     //到活跃点的距离
    public int queryDis;   //到查询点的距离
    public int activeName;  //活跃点名字

    public Car(int name, int activeName, int edgeDis) {
        this.carName = name;
        this.activeName = activeName;
        curDis = RANDOM.nextInt(edgeDis);
    }

    /*
    * 更新移动对象的位置
    * */
    public int updateLocation() {

        curDis -= RANDOM.nextInt(CAR_SPEED_LIMIT);
        if (curDis < 0) {
            int originalActive = activeName;
            updateActive();
            return originalActive;
        }
        return -1;
    }

    /*
     * 更新兴趣点
     * */
    private void updateActive() {
        Vnode vn = allVertices.get(activeName).getRandomEdge();
        activeName = vn.name;
        curDis = vn.dis;
    }
}
