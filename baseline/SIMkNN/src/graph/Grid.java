package graph;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Grid {
    String GridName;
    public int GridNo;
    public int capacity;               //容量
    public int Nc;                     //目标容量 超过Nc则分裂
    public int m;                      //分裂系数
    public boolean valid;
    public int ObjectNum;               //目标数量
    public ArrayList<Objects> ObjectList = new ArrayList<Objects>();

    public double X1, X2;
    public double Y1, Y2;

    //  public ArrayList<Grid> SubGrid = new ArrayList<Grid>();
    public ArrayList<Grid> up = new ArrayList<Grid>();
    public ArrayList<Grid> down = new ArrayList<Grid>();
    public ArrayList<Grid> left = new ArrayList<Grid>();
    public ArrayList<Grid> right = new ArrayList<Grid>();
    public Grid top_left;
    public Grid top_right;
    public Grid bottom_left;
    public Grid bottom_right;

    public Grid[][] splitarray;
    public boolean Leaf;                   //是否叶子
    public Grid parent;

    public Grid(int nc, int mm, double x1, double y1, double x2, double y2, boolean leaf) throws FileNotFoundException, IOException {
        //    GridNo = gridno;
        Nc = nc;
        m = mm;
        valid = true;
        X1 = x1;
        Y1 = y1;
        X2 = x2;
        Y2 = y2;
        Leaf = leaf;
        ObjectNum = 0;
        top_left = null;
        top_right = null;
        bottom_left = null;
        bottom_right = null;
    }

    public int CountObject() {
//        if (Leaf == true) {
//            if(ObjectList!=null){
//                ObjectNum = ObjectList.size();
//                return ObjectNum;
//            }else
//                return 0;
//
//        } else {
//            int sum = 0;
//            for (int i = 0; i < m; i++) {
//                for (int j = 0; j < m; j++) {
//                    sum += splitarray[i][j].CountObject();
//                }
//            }
//            ObjectNum = sum;
//            return ObjectNum;
//        }
        ObjectNum = ObjectList.size();
        return ObjectNum;
    }

    public int CountParentObject() {
        int sum = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
//                if(p==null)
//                    System.out.println("aa");
//                if(p.Leaf!=false)
//                    System.out.println("bb");
//                if(p.splitarray[i][j].ObjectList==null)
//                    System.out.println("b");
                sum += parent.splitarray[i][j].ObjectList.size();
            }
        }
        return sum;
    }
//
//    public void NeighborNum() {//每一次更新必不可少执行，影响ObjectNum
//        int sum = 0;
//        if (top_left != null) {
//            sum++;
//        }
//        if (top_right != null) {
//            sum++;
//        }
//        if (bottom_left != null) {
//            sum++;
//        }
//        if (bottom_right != null) {
//            sum++;
//        }
//        sum = up.size() + down.size() + left.size() + right.size();
//        ObjectNum = sum;
//    }

    public boolean JudgeSplit() {
        if (CountObject() >= Nc) {
            return true;
        } else {
            return false;
        }
    }

    public void Inheritance(Grid[][] splitarray) {
        splitarray[0][0].bottom_left = bottom_left;
        splitarray[0][m - 1].bottom_right = bottom_right;
        splitarray[m - 1][0].top_left = top_left;
        splitarray[m - 1][m - 1].top_right = top_right;
    }

    public void SelectiveInheritance(Grid[][] splitarray) {
        ArrayList<Grid> side = new ArrayList<Grid>();
        ArrayList<Grid> angle = new ArrayList<Grid>();
        if (up != null) {
            for (int i = 0; i < up.size(); i++) {
                side.add(up.get(i));
            }
        }
        if (left != null) {
            for (int i = 0; i < left.size(); i++) {
                side.add(left.get(i));
            }
        }
        if (down != null) {
            for (int i = 0; i < down.size(); i++) {
                side.add(down.get(i));
            }
        }
        if (right != null) {
            for (int i = 0; i < right.size(); i++) {
                side.add(right.get(i));
            }
        }
        if (top_left != null) {
            angle.add(top_left);
        }
        if (top_right != null) {
            angle.add(top_right);
        }
        if (bottom_left != null) {
            angle.add(bottom_left);
        }
        if (bottom_right != null) {
            angle.add(bottom_right);
        }

        //case 1
        for (int i1 = 0; i1 < side.size(); i1++) {
            for (int j = 0; j < m; j++) {
                for (int i = 0; i < m; i++) {
                    Grid n = side.get(i1);
                    if ((i == 0) || (j == 0) || (i == m - 1) || (j == m - 1)) { //表示side/angle cells
                        if ((splitarray[j][i].X1 == n.X2) && ((n.Y1 >= splitarray[j][i].Y1 && n.Y2 <= splitarray[j][i].Y2) || (n.Y1 <= splitarray[j][i].Y1 && n.Y2 >= splitarray[j][i].Y2))) {
                            splitarray[j][i].left.add(n);
                        }
                        if ((splitarray[j][i].X2 == n.X1) && ((n.Y1 >= splitarray[j][i].Y1 && n.Y2 <= splitarray[j][i].Y2) || (n.Y1 <= splitarray[j][i].Y1 && n.Y2 >= splitarray[j][i].Y2))) {
                            splitarray[j][i].right.add(n);
                        }
                        if ((splitarray[j][i].Y2 == n.Y1) && ((n.X1 >= splitarray[j][i].X1 && n.X2 <= splitarray[j][i].X2) || (n.X1 <= splitarray[j][i].X1 && n.X2 >= splitarray[j][i].X2))) {
                            splitarray[j][i].up.add(n);
                        }
                        if ((splitarray[j][i].Y1 == n.Y2) && ((n.X1 >= splitarray[j][i].X1 && n.X2 <= splitarray[j][i].X2) || (n.X1 <= splitarray[j][i].X1 && n.X2 >= splitarray[j][i].X2))) {
                            splitarray[j][i].down.add(n);
                        }
                    }

                }

            }
        }//time 2020/1/16 paper page6 case1

        //case 2
        Grid tl = splitarray[m - 1][0];
        Grid tr = splitarray[m - 1][m - 1];
        Grid bl = splitarray[0][0];
        Grid br = splitarray[0][m - 1];
        for (int i1 = 0; i1 < angle.size(); i1++) {
            Grid n = angle.get(i1);
            //top_left cell of C    Cij
            if ((tl.X2 <= n.X2 && tl.X2 >= n.X1) && (tl.Y2 == n.Y1)) {
                tl.top_right = n;
            }
            if ((tl.Y1 <= n.Y2 && tl.Y1 >= n.Y1) && (tl.X1 == n.X2)) {
                tl.bottom_left = n;
            }

            //top_right cell of C Cij
            if ((tr.X1 <= n.X2 && tr.X1 >= n.X1) && (tr.Y2 == n.Y1)) {
                tr.top_left = n;
            }
            if ((tr.Y1 <= n.Y2 && tr.Y1 >= n.Y1) && (tr.X2 == n.X1)) {
                tr.bottom_right = n;
            }

            //bottom_left of cell C Cij
            if ((bl.Y2 <= n.Y2 && bl.Y2 >= n.Y1) && (bl.X1 == n.X2)) {
                bl.top_left = n;
            }
            if ((bl.X2 <= n.X2 && bl.X2 >= n.X1) && (bl.Y1 == n.Y2)) {
                bl.bottom_right = n;
            }

            //bottom_right cell of C Cij
            if ((br.Y2 <= n.Y2 && br.Y2 >= n.Y1) && (br.X2 == n.X1)) {
                br.top_right = n;
            }
            if ((br.X1 <= n.X2 && br.X1 >= n.X1) && (br.Y1 == n.Y2)) {
                br.bottom_left = n;
            }
        }

        //case 3
        for (int i = 1; i < m - 1; i++) {
            for (int i1 = 0; i1 < angle.size(); i1++) {
                Grid n = angle.get(i1);
                if (splitarray[i][0].Y2 <= n.Y2 && splitarray[i][0].Y2 >= n.Y1 && splitarray[i][0].X1 == n.X2) {
                    splitarray[i][0].top_left = n;
                }
                if (splitarray[i][0].Y1 <= n.Y2 && splitarray[i][0].Y1 >= n.Y1 && splitarray[i][0].X1 == n.X2) {
                    splitarray[i][0].bottom_left = n;
                }
                if (splitarray[i][m - 1].Y2 <= n.Y2 && splitarray[i][m - 1].Y2 >= n.Y1 && splitarray[i][m - 1].X2 == n.X1) {
                    splitarray[i][0].top_right = n;
                }
                if (splitarray[i][m - 1].Y1 <= n.Y2 && splitarray[i][m - 1].Y1 >= n.Y1 && splitarray[i][m - 1].X2 == n.X1) {
                    splitarray[i][0].bottom_right = n;
                }
                if (splitarray[m - 1][i].X1 <= n.X2 && splitarray[m - 1][i].X1 >= n.X1 && splitarray[m - 1][i].Y2 == n.Y1) {
                    splitarray[m - 1][i].top_left = n;
                }
                if (splitarray[m - 1][i].X2 <= n.X2 && splitarray[m - 1][i].X2 >= n.X1 && splitarray[m - 1][i].Y2 == n.Y1) {
                    splitarray[m - 1][i].top_right = n;
                }
                if (splitarray[0][i].X1 <= n.X2 && splitarray[0][i].X1 >= n.X1 && splitarray[0][i].Y1 == n.Y2) {
                    splitarray[0][i].bottom_left = n;
                }
                if (splitarray[0][i].X2 <= n.X2 && splitarray[0][i].X2 >= n.X1 && splitarray[0][i].Y1 == n.Y2) {
                    splitarray[0][i].bottom_right = n;
                }
            }

        }
    }

    public void Non_Inheritance(Grid[][] splitarray) {
        //case1 angle cell
        //top_left angle cell
        splitarray[m - 1][0].down.add(splitarray[m - 2][0]);
        splitarray[m - 1][0].right.add(splitarray[m - 1][1]);
        splitarray[m - 1][0].bottom_right = splitarray[m - 2][1];

        //top_right angle cell
        splitarray[m - 1][m - 1].down.add(splitarray[m - 2][m - 1]);
        splitarray[m - 1][m - 1].left.add(splitarray[m - 1][m - 2]);
        splitarray[m - 1][m - 1].bottom_left = splitarray[m - 2][m - 2];

        //bottom_left angle cell
        splitarray[0][0].up.add(splitarray[1][0]);
        splitarray[0][0].right.add(splitarray[0][1]);
        splitarray[0][0].top_right = splitarray[1][1];

        //bottom_right angle cell
        splitarray[0][m - 1].up.add(splitarray[1][m - 1]);
        splitarray[0][m - 1].left.add(splitarray[0][m - 2]);
        splitarray[0][m - 1].top_left = splitarray[1][m - 2];

        //case2 side cells
        //left side cell
        for (int i = 1; i < m - 1; i++) {
            splitarray[i][0].up.add(splitarray[i + 1][0]);
            splitarray[i][0].down.add(splitarray[i - 1][0]);
            splitarray[i][0].right.add(splitarray[i][1]);
            splitarray[i][0].top_right = splitarray[i + 1][1];
            splitarray[i][0].bottom_right = splitarray[i - 1][1];
        }
        //right side cell
        for (int i = 1; i < m - 1; i++) {
            splitarray[i][m - 1].up.add(splitarray[i + 1][m - 1]);
            splitarray[i][m - 1].down.add(splitarray[i - 1][m - 1]);
            splitarray[i][m - 1].left.add(splitarray[i][m - 2]);
            splitarray[i][m - 1].top_left = splitarray[i + 1][m - 2];
            splitarray[i][m - 1].bottom_left = splitarray[i - 1][m - 2];
        }
        //up side cell
        for (int i = 1; i < m - 1; i++) {
            splitarray[m - 1][i].left.add(splitarray[m - 1][i - 1]);
            splitarray[m - 1][i].right.add(splitarray[m - 1][i + 1]);
            splitarray[m - 1][i].down.add(splitarray[m - 2][i]);
            splitarray[m - 1][i].bottom_left = splitarray[m - 2][i - 1];
            splitarray[m - 1][i].bottom_right = splitarray[m - 2][i + 1];
        }
        //down side cell
        for (int i = 1; i < m - 1; i++) {
            splitarray[0][i].left.add(splitarray[0][i - 1]);
            splitarray[0][i].right.add(splitarray[0][i + 1]);
            splitarray[0][i].up.add(splitarray[1][i]);
            splitarray[0][i].top_left = splitarray[1][i - 1];
            splitarray[0][i].top_right = splitarray[1][i + 1];
        }

        //case3 inner-cells
        for (int i = 1; i < m - 1; i++) {
            for (int j = 1; j < m - 1; j++) {
                splitarray[i][j].up.add(splitarray[i + 1][j]);
                splitarray[i][j].down.add(splitarray[i - 1][j]);
                splitarray[i][j].left.add(splitarray[i][j - 1]);
                splitarray[i][j].right.add(splitarray[i][j + 1]);
                splitarray[i][j].top_left = splitarray[i + 1][j - 1];
                splitarray[i][j].top_right = splitarray[i + 1][j + 1];
                splitarray[i][j].bottom_left = splitarray[i - 1][j - 1];
                splitarray[i][j].bottom_right = splitarray[i - 1][j + 1];
            }
        }
    }

    public void Split(ArrayList<Grid> allgrids) throws IOException {

        splitarray = new Grid[m][m];
        boolean l = true;//means leaf cell
        double deltx = (X2 - X1) / m, delty = (Y2 - Y1) / m;
        double x1, x2, y1, y2;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                x1 = X1 + deltx * i;
                x2 = X1 + deltx * (i + 1);
                y1 = Y1 + delty * j;
                y2 = Y1 + delty * (j + 1);
                Grid g = new Grid(Nc, m, x1, y1, x2, y2, l);
                g.parent = this;
                splitarray[i][j] = g;
                allgrids.add(g);
            }
        }

        for (int i = 0; i < ObjectList.size(); i++) {
            for (int xx = 0; xx < m; xx++) {
                for (int yy = 0; yy < m; yy++) {
                    if (BelongTo(ObjectList.get(i), splitarray[xx][yy])) {
                        splitarray[xx][yy].ObjectList.add(ObjectList.get(i));
                        splitarray[xx][yy].Leaf = true;
                    }
                }
            }

        }
        valid = false;
        ObjectList.clear();
        top_left = null;
        top_right = null;
        bottom_left = null;
        bottom_right = null;
        up.clear();
        down.clear();
        left.clear();
        right.clear();
        ObjectNum = 0;
        Leaf = false;
        Inheritance(splitarray);
        SelectiveInheritance(splitarray);
        Non_Inheritance(splitarray);

    }

    public boolean BelongTo(Objects o, Grid g) {
        return o.X >= g.X1 && o.X <= g.X2 && o.Y >= g.Y1 && o.Y <= g.Y2;
    }

    public void Remove(Objects o) {
        ObjectList.remove(o);
        ObjectNum--;
    }

}
