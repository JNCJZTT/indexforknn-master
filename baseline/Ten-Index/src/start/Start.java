package start;

import buildindex.BuildIndex;
import graph.Car;
import graph.Vertex;
import graph.Vnode;
import search.DijkstraSearch;
import search.Search;

import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

import static buildindex.BuildIndex.AllVertices;
import static buildindex.BuildIndex.K;

public class Start {
    public static void TestBuild(int times) throws IOException {
//        System.gc();
//        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        // long startMem = Runtime.getRuntime().freeMemory(); // 开始Memory
        String maps[] = {"NY", "COL", "CAL", "E"};
        for (String map : maps) {
            long BuildTimeSum = 0;
            for (int i = 0; i < times; i++) {
                System.gc();
                BuildIndex b = new BuildIndex(600000, "COL");
                BuildTimeSum += b.Run();
            }
            BuildTimeSum /= times;
            System.out.println("构建平均时间为：" + String.format("%.2f 秒", (float) BuildTimeSum));
        }


//        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//        String memory = String.valueOf((double) (memoryAfter - memoryBefore) / 1024 / 1024);
//        System.out.println("消耗内存：" + memory.substring(0, memory.equals("0.0") ? 1 : (memory.indexOf(".") + 3)) + " M");
    }

    private static void TestkNN() throws IOException {
        String maps[] = {"NY", "COL", "CAL", "E"};
        for (String map : maps) {
            BuildIndex b = new BuildIndex(600000, map);
            b.Run();
            System.out.println("地图为：" + map);
            int[] KArray = {10, 20, 30, 40, 50};
            long AveTime = 0;
            for (int i = 0; i < KArray.length; i++) {
                b.K = KArray[i];
                int times = 500;

                for (int j = 0; j < times; j++) {

                    try {
                        int QueryName = b.GetRandomQueryName();
                        Search sc = new Search(QueryName, "t");
                        b.ResetValue();
                        AveTime += sc.getTime();

                    } catch (Exception e) {
                        times--;
                    }

                }
                System.out.println(" 当K=" + KArray[i] + " 时，平均查询时间=" + (AveTime / times) + "微秒 ");
            }
        }


    }

    private static void TestUpdate() throws IOException {
        String maps[] = {"NY", "COL", "CAL", "E"};
        for (int k = 0; k < maps.length; k++) {
            String map = maps[k];
            System.out.println("map=" + map);
            BuildIndex b = new BuildIndex(600000, map);
            b.Run();

            System.out.println("构建完成");
            int times = 50;
            long AveTime = 0;

            int num = (int) (600000 * 0.25);

            for (int j = 0; j < times; j++) {
                b.UpdateCar(num);
                AveTime += b.GetUpdateTime();
            }
            System.out.println("更新" + num + "个移动对象耗费的时间=" + AveTime / times + "毫秒");

        }
    }

    private static final int SAVE_DECIMAL_PLACES = 2;

    public static void main(String[] args) throws IOException {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(SAVE_DECIMAL_PLACES);
        Scanner sc = new Scanner(System.in);
        String maps[] = {"NY"};
        for (String map : maps) {
            BuildIndex b = new BuildIndex(20000, map);
            b.Run();

            int maxName = 1, minName = 1;
            for (Vertex vertex : AllVertices.values()) {
                if (vertex.TreeLevel == 600) {
                    maxName = vertex.VertexName;
                }
                if (vertex.TreeLevel == 10) {
                    minName = 10;
                }
            }
            System.out.println("*******************MAX_TIME******************");
            for (int i = 0; i < 50; i++) {
                Search search = new Search(maxName, "s");
                System.out.println(nf.format(search.time / 1000) + "毫秒");

            }
            System.out.println("*******************MIN_TIME******************");
            for (int i = 0; i < 50; i++) {
                Search search = new Search(minName, "s");
                System.out.println(nf.format(search.time / 1000) + "毫秒");

            }
//            while (true) {
//                int QueryName = b.GetRandomQueryName();
//                System.out.println(AllVertices.get(QueryName).TreeLevel);
//                Search search = new Search(QueryName, "t");
//                System.out.println(nf.format(search.time / 1000) + "毫秒");
//                System.out.println("请输入：");
//                int x = sc.nextInt();
//                if (x == -1) {
//                    break;
//                }
//            }
        }


//        TestBuild(1);
//            TestUpdate();
//        TestkNN();

//        TestUpdate(600000,"COL");
//        while(true)
//        {
        //System.out.println("输入指令：");
//            Scanner ss = new Scanner(System.in);
        //int x = ss.nextInt();
//            if(x==-1){
//                System.out.println("输入K值：");
//                int y=ss.nextInt();
//                b.K=y;
        //}


//
//            if(x>0){
//                b.UpdateCar(x);
//            }
//
//
//            int QueryName=b.GetRandomQueryName();
//            //QueryName=257266;
//            System.out.println("查询点为："+QueryName);
//            DijkstraSearch ds=new DijkstraSearch(b.getVertexSize(),QueryName);
//            List<Car> dk=ds.KCars;
//
//            Search s=new Search(QueryName);
//            List<Car> sk=s.KCars;
//
//            for(int i=0;i<K;i++){
//                System.out.println(" name = "+sk.get(i).CarName+" ,  "+ dk.get(i).CarName+"     dis="+sk.get(i).QueryDis+"   , "+dk.get(i).QueryDis);
//            }
//            b.ResetValue();
//        }

    }
}
