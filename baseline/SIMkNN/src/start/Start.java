package start;

import buildindex.BuildIndex;

import java.io.IOException;

public class Start {

    public void TestSearch() throws IOException {
        int times=1;
        int Nc = 100, m = 3, objectnum = 20000, k = 10;
        BuildIndex BI = new BuildIndex(Nc, m, k, objectnum);
        BI.SIMkNNPrcocess();
    }

    public static void main(String[] args) throws IOException {
        int times=10;
        int Nc = 100, m = 3, objectnum = 20000, k = 10;
        boolean l = true;
        float CreatGridTime = 0, CreatRoadnetworkTime = 0, kNNTime = 0, ObjectUpdateTime = 0;
        for (int i = 0; i < times; i++) {
//            System.gc();
//            long memoryBefore = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

            BuildIndex BI = new BuildIndex(Nc, m, k, objectnum);
            BI.SIMkNNPrcocess();
//            long memoryAfter = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
//            String memory = String.valueOf((double)(memoryAfter-memoryBefore)/1024/1024) ;
//            System.out.println("消耗内存：" + memory.substring(0,memory.equals("0.0") ? 1 : (memory.indexOf(".")+3)) + " M");
            CreatGridTime += BI.CreatGridTime;
            CreatRoadnetworkTime += BI.CreatRoadnetworkTime;
            kNNTime += BI.kNNTime;
            ObjectUpdateTime += BI.ObjectUpdateTime;
        }
        System.out.println("平均创建格子时间：" + CreatGridTime / times + " ms ");
        System.out.println("平均创建路网时间：" + CreatRoadnetworkTime / times + " ms ");
        System.out.println("平均knn时间：" + kNNTime / times + " ms ");
        System.out.println("平均更新移动对象时间：" + ObjectUpdateTime / times + " ms ");
    }
}
