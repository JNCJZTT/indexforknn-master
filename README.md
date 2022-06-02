-Datasets

Using two files to build a AMT-Index.

1. City_Edge.txt : each row represents the adjacenct list for each vertex with the following format：

  ```
  neighborName1,dis1,neighborName2,dis2....
  ```
2. USA-road-d.City.branch-x.avg-y : x represents the branch number m  and y refers to the maximum threshold w.r.t. the number of vertices in each leaf subgraph. Each row represents the IDs of subgraphs each vertex belongs to, where the subgrahs are organized with a M-ary tree. The following row is the first row that contains the IDs of subgraphs containing vertex 1 (ID of the vertex being 1), which implies that the vertex 1 is assigned to the subgraph 1 (ID of the subgraph being 1) in the first division. In the second division, vertex 1 is the assigned to the second child subgraph of subgraph 1. The division iteratively proceeds until vertex 1 is contained by a leaf subgraph.
  ```
  1,2,1,2
  ```
The data of NY has been prepared in indexforknn/data/NY

- Operating environment: Java Jdk17

- Baseline codes: codes of baselines are in the package "indexforknn-master/baseline"

- AMT-Index: Package "indexforknn-master/src/man/java/com/index/indexforknn" contains the code of our solution. This package includes two packages "amt" and "base", where "amt" contains the code of the AMT-index while "base" stores the onfiguration messages and some initialization operations about AMT-Index.

- Start:

  Start the springboot by running the driver class: "src/main/java/com/index/indexforknn/IndexforknnApplication.java"

  Use the interface in base/controller/BaseController through postman to start index building, index maintenance and processing kNN queries, where the urls are http://localhost:8888/build , http://localhost:8888/knn, and http://localhost:8888/update respectively. 
  

  

  
