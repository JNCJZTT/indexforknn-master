We write amt-index code based on Spring and use the MVC architecture.

Its advantage is that it can make our code more modular, loosely coupled and easier to maintain and read.

Spring provides many annotations and useful interfaces, which allow us to avoid writing repetitive and meaningless code. 

We layer the entire code project, it can be well maintained, reused and future expansion (such as adding databases, etc.)

The project code is divided into two packages, amt and base. The code in base is a set of general build and update index and knn code framework. By inheriting base, we can implement an index algorithm with very little code. amt is the amt-index we implement by inheriting base.

IndexforknnApplication.java is the startup class for our code. After starting Sping, we use the interface of the Controller layer to test by postman. The controller java file's path is src/main/java/com/index/indexforknn/base/controller/BaseController.java.

The following is the interface document if it is started locally. If it is started in the server, just convert localhost to the server address.

request url：http://localhost:8888/build

request body:

| parameter      | Type   | DESCRIBE                                       |
| -------------- | ------ | :--------------------------------------------- |
| branch         | Int    | the branch of tree                             |
| subGraphSize   | Int    | Maximum number of nodes per subgraph           |
| indexType      | String | the type of index(AMT)                         |
| mapInfo        | String | the city name                                  |
| Distribution   | String | the distribution of moving objects             |
| carName        | Int    | the number of moving objects                   |
| leastActiveNum | Int    | Thresholds for maintaining tree nodes          |
| timeType       | String | The unit of time spent to build the index      |
| Memory         | Bool   | Whether to calculate the index consumes memory |
| memoryType     | String | The unit of memory cost to build the index     |

request url : http://localhost:8888/update

request body:

| parameter | Type | DESCRIBE                                             |
| --------- | ---- | :--------------------------------------------------- |
| updateNum | Int  | The number of moving objects that need to be updated |

request url : http://localhost:8888/knn



| parameter | Type | DESCRIBE                                                     |
| --------- | ---- | :----------------------------------------------------------- |
| k         | Int  | the number of nearest moving objects                         |
| queryName | Int  | the name of query node(If it is -1, generate according to the distribution) |
|           |      |                                                                                 |
