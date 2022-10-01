import os
import queue
import metis

base_url = "/Users/zhoutao/Documents/kNNIndexData/{}/"
root_dir = ""
global_adj = {}  # 全局的adj
global_part = []
graph_name = 0


class Graph:
    def __init__(self, num, rank, parent, url="error"):
        global graph_name
        if graph_name != 0:
            url = root_dir + str(graph_name) + ".txt"
        self.name = graph_name
        graph_name += 1
        self.rank = rank
        self.url = url
        self.num = num
        self.parent = parent
        # print("graph name= %s , url= %s,  num= %d" % (self.name, url, num))

        self.index2name = []
        self.name2index = {}
        self.adj_list = []

    def init_root(self):
        """
        初始化根文件
        """
        global root_dir
        root_dir = base_url + "metis/metisProcess.{}.part-{}.z-{}/"
        root_dir = root_dir.format(map, npart, z)
        if (not os.path.exists(root_dir)):
            os.mkdir(root_dir)

        graph_file = open(self.url)
        for ver in range(num):
            global_adj[ver] = []
            line = graph_file.readline()
            str = line.split()
            i = 0
            while i < len(str):
                global_adj[ver].append((
                    int(str[i]), int(str[i + 1])
                ))
                i += 2
        graph_file.close()

    def process(self):
        self.__readfile()
        self.__metis()
        self.__partitionChildren()

    def getChildren(self):
        return self.children

    def __readfile(self):
        """
        读取文件
        """
        graph_file = open(self.url)
        self.adj_list = []
        for i in range(self.num):
            line = graph_file.readline()
            str = line.split()
            ver = i
            if len(self.adj_list) <= ver:
                self.adj_list.append([])
            j = 0
            while j < len(str):
                self.adj_list[ver].append((
                    self.__name2index(int(str[j])), int(str[j + 1])
                ))
                j = j + 2
        # print(self.adj_list)
        graph_file.close()

    def __metis(self):
        # if self.name == 363:
        #     print(self.adj_list)
        cuts, self.parts = metis.part_graph(self.adj_list, nparts=npart, recursive=True)
        self.part_num = [0 for part in range(npart)]

        for i in range(self.num):
            # try:
            self.part_num[self.parts[i]] += 1
            # except Exception:
            #     print("eee")

            # i 为下标
            name = self.__index2name(i)
            global_part[name] += ("," + str(self.parts[i]))
        # print(part_num)

    def __partitionChildren(self):
        """
        生成子图
        """
        self.children = []
        if self.num <= z:
            return

        filelist = []
        for i in range(npart):
            self.children.append(Graph(self.part_num[i], i, parent=self.name))
            filelist.append(open(self.children[i].url, 'w'))

        # 名字和下标之间的相互转换
        for i in range(self.num):
            ver = i
            part = self.parts[ver]
            # 将当前图的下标转换成名字再添加
            ver_name = self.__index2name(ver)
            index = len(self.children[part].index2name)
            child = self.children[part]
            file = filelist[part]

            child.index2name.append(ver_name)
            child.name2index[ver_name] = index
            # child.adj_list.append([])
            adj = global_adj[ver_name]

            first = True
            for (name, dis) in adj:
                if self.name == 0 or self.name2index.__contains__(name):
                    adj_index = self.__name2index(name)
                    if part == self.parts[adj_index]:
                        # 如果划分到一个子图中
                        # child.adj_list[index].append((name, dis))
                        if first:
                            file.write(str(name) + " " + str(dis))
                            first = False
                        else:
                            file.write(" " + str(name) + " " + str(dis))
            file.write("\n")
        for file in filelist:
            file.close()
        # for child in self.children:
        #     child.__readfile()
        #     child.__metis()

    def __name2index(self, name):
        """
        名字和下标之间的转换
        :return: 下标
        """
        if self.name == 0:
            return name
        return self.name2index[name]

    def __index2name(self, index):
        """
        下标和名字之间的转换
        :param index:
        :return: name
        """
        if self.name == 0:
            return index
        return self.index2name[index]


def partition():
    init_global_part()

    root = Graph(num, 0, -1, base_url + map + "_Edge_test.txt")
    root.init_root()
    q = queue.Queue()
    q.put(root)
    x = 0
    y = 1
    z = 1
    layer = 0
    while not q.empty():
        graph = q.get()
        # print("graph_name= %d ,graph_num=%d  ,parent= %d" % (graph.name, graph.num, graph.parent))

        graph.process()
        x += 1
        for subgraph in graph.getChildren():
            q.put(subgraph)
        z += len(graph.getChildren())
        if x == y:
            print("Layer %d is processed " % layer)
            layer += 1
            y = z


def init_global_part():
    global global_part
    global_part = ["0" for n in range(num)]


def outputFile():
    """
    写入结果文件
    :return:
    """
    output_url = base_url + "USA-road-d.{}.branch-{}.avg-{}.txt"
    output_url = output_url.format(map, npart, z)
    print("output path = %s" % output_url)
    # print("Resule:")
    # print(global_part)
    file = open(output_url, 'w')
    for line in global_part:
        file.write(line + "\r\n")


if __name__ == '__main__':
    npart = 4
    z = 200
    map = "COL"
    num = 435_666  # graph的点数量
    base_url = base_url.format(map)
    partition()
    print("Graph is spilit in %d sub-graphs" % (graph_name))
    outputFile()
    print("Done!")
