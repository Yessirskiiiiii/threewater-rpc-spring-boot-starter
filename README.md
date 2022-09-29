# threewater-rpc-spring-boot-starter
一个基于 Spring Boot Starter 的小型 RPC 框架，麻雀虽小，五脏俱全。
* 基于 Spring Boot Starter 的小型 RPC 框架
* 使用 Zookeeper 作为注册中心
* 使用 Netty 作为通信框架
* 提供多种序列化协议
* 提供多种负载均衡算法
* 客户端提供本地服务列表缓存，提高性能
* 服务端利用线程池提高消息处理能力
* 客户端支持 TCP 长连接，多次复用
## 快速使用
1. 下载源码，生成本地 Maven 依赖

   ```shell
   mvn clean install
   ```

2. 服务提供者、消费者引入 Maven 依赖

   ```xml
   <dependency>
   	<groupId>com.threewater</groupId>
   	<artifactId>threewater-rpc-spring-boot-starter</artifactId>
   	<version>1.0-SNAPSHOT</version>
   </dependency>
   ```

3. 服务提供者、消费者进行简单配置

   ```yaml
   threewater:
     rpc:
       load-balance: weightRandom
       server-port: 9999
       register-address: localhost:2181
       weight: 1
       protocol: hessian
   ```

   | 属性                            | 含义             | 可选项                                       |
   | ------------------------------- | ---------------- | -------------------------------------------- |
   | threewater.rpc.protocol         | 消息序列化协议   | Java，Kryo，Protobuf，Hessian                |
   | threewater.rpc.register-address | 注册中心地址     | 默认 localhost:2181                          |
   | threewater.rpc.server-port      | 服务端通信端口号 | 默认 9999                                    |
   | threewater.rpc.weight           | 权重             | 默认 1                                       |
   | threewater.rpc.load-balance     | 负载均衡算法     | 随机，加权随机。轮询，加权轮询，平滑加权轮询 |

4. 服务提供者注解服务

   ```java
   @RestController
   @RequestMapping("rpc")
   public class UseController {
   
       @InjectService
       private UserService userService;
   
       @GetMapping("/user/{id}")
       public Result<User> getUserById(@PathVariable Long id) {
           return userService.selectUserById(id);
       }
   
   }
   ```

5. 服务消费者注入服务

   ```java
   @Service
   public class UserServiceImpl implements UserService {
   
       @Autowired
       private UserMapper userMapper;
   
       @Override
       public Result<User> selectUserById(Long id) {
           User user = userMapper.selectById(id);
           return user == null ? Result.error(ResultCode.NOT_FOUND) : Result.success(user);
       }
   
   }
   ```

   注意：此处`@Service`注解为 threewater-rpc-spring-boot-starter 的注解
## 附
demo：https://github.com/Yessirskiiiiii/threewater-rpc-example
