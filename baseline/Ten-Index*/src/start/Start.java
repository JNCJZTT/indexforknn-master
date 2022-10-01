package start;

import buildindex.BuildIndex;
import graph.Car;
import search.DijkstraSearch;
import search.Search;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static buildindex.BuildIndex.K;

public class Start {
    public static void testBuild() throws IOException {
        System.gc();
        long memoryBefore = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        // long startMem = Runtime.getRuntime().freeMemory(); // 开始Memory
        BuildIndex b=new BuildIndex(20000,"COL");
        b.Run();
        long memoryAfter = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        String memory = String.valueOf((double)(memoryAfter-memoryBefore)/1024/1024) ;
        System.out.println("消耗内存：" + memory.substring(0,memory.equals("0.0") ? 1 : (memory.indexOf(".")+3)) + " M");

    }

    private static void TestkNN(int CarSize,String map) throws IOException {
        BuildIndex b=new BuildIndex(CarSize,map);
        b.Run();
        System.out.println("此时 CarSize="+CarSize);
        int[] KArray={10,20,30,40,50};
        int times=1000;
        long AveTime=0;
        for(int i=0;i<KArray.length;i++){
            b.K=KArray[i];

            for(int j=0;j<times;j++){

                try{
                    int QueryName=b.GetRandomQueryName();
                    Search sc=new Search(QueryName);
                    b.ResetValue();
                    AveTime +=sc.getTime();
                }catch (Exception e){

                }

            }
            System.out.println(" 当K="+KArray[i]+" 时，平均查询时间="+ (AveTime/times)+"微秒 ");
        }
    }

    private static void TestUpdate(int CarSize, String map) throws IOException {
        BuildIndex b = new BuildIndex(CarSize, map);
        b.Run();
        System.out.println("此时 CarSize=" + CarSize);
        int[] UpdateNums = {5000, 10000, 15000, 20000};
        int times = 10;
        long AveTime = 0;
        for (int i = 0; i < UpdateNums.length; i++) {
            int num = UpdateNums[i];

            for (int j = 0; j < times; j++) {

                b.UpdateCar(num);

                AveTime += b.GetUpdateTime();


            }
            System.out.println("更新"+num+"个移动对象耗费的时间="+AveTime/times+"毫秒");
        }
    }

    public static void main(String[] args) throws IOException {

        TestUpdate(20000,"NY");


//        while(true)
//        {
//            BuildIndex b=new BuildIndex();
//            Scanner ss = new Scanner(System.in);
//            System.out.println("请输入K值：");
//            int x = ss.nextInt();
//            b.K=x;
//            b.Run();

//            int QueryName=13;

//            int QueryName=b.GetRandomQueryName();
//            System.out.println("查询点为："+QueryName);
//            Search s=new Search(QueryName);
//            List<Car> sk=s.KCars;
//            DijkstraSearch ds=new DijkstraSearch(b.getVertexSize(),QueryName);
//            List<Car> dk=ds.KCars;
//            for(int i=0;i<K;i++){
//                System.out.println(" name = "+sk.get(i).CarName+" ,  "+ dk.get(i).CarName+"     dis="+sk.get(i).QueryDis+"   , "+dk.get(i).QueryDis);
//            }
//            b.ResetValue();
//        }

    }
}
