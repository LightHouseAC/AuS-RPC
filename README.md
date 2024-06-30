# AuS - RPC

#### 基于 Vert.x 和 Etcd 的RPC框架，服务于 [AuS OJ](https://github.com/LightHouseAC/Aus-OJ) 和 [Simpletodo](https://github.com/LightHouseAC/SimpleTodo/settings)

——————

### 项目笔记

1. 安装 Etcd： https://github.com/etcd-io/etcd/releases/tag/v3.5.14

2. 使用动态代理工厂给调用服务生成一个代理对象，使用代理对象调用帮消费者调用服务

   **设计模式：工厂模式，代理模式**

   > 自用学习笔记：[LINK](https://lighthouseac.github.io/2024/06/30/%E9%9D%A2%E8%AF%95%E5%9F%BA%E7%A1%80%E7%9F%A5%E8%AF%86%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/)

3. 代理对象用**建造者模式**创建不同的Rpc请求类并**序列化**，向服务提供者发送请求获取到并将**反序列化**后的相应类中的数据返回给消费者

4. 序列化通过一个序列化工厂类 + 自定义的SPI机制（扫描resources/META-INFO/rpc下的配置类）加载不同的序列化类（Jdk、Hessian、Hessian2、Json、Kryo），支持自定义序列化器

5. 由于每次请求和响应都需要从配置类中读取配置（一个本地的注册器LocalRegistry），因此用DCL实现一个**单例模式**来完成配置类

6. 到这里实现了基础的RPC功能，接下来需要实现 注册中心✔，尝试实现负载均衡、自定义的协议（优化网络传输效率）以及一些复杂机制 —— **TODO**

7. 用 Etcd 实现注册中心

   Etcd是一个基于 go 的高效的分布式 K/V存储系统，使用Raft算法保证数据的一致性

   对于同一个服务，可能有多个服务节点，可以像文件系统一样设计注册中心的存储方式，可以用前缀查询某个服务的所有节点（类似父目录下的所有子目录）

   Etcd有很多种客户端，实现注册中心需要这3种：

   1. KVClient —— 用于K/V操作
   2. LeaseClient —— 管理Etcd的租约（也就是过期时间TTL）
   3. WatchClient —— 监听Etcd中key的变化

   通过这3种客户端，就可以实现服务的注册，注销，发现和注册中心的初始化/销毁操作了

8. 可以通过类似序列化器的设计模式，也用工厂模式 + SPI 来实现通过配置使用不同的注册中心和支持自定义的注册中心

9. 将serviceNodeKey设置为 `/rpc/serviceName:serviceVersion/serviceHost:servicePort`，serviceKey设置为 “前缀查找” 要匹配的字串 `/rpc/serviceName:serviceVersion/` 即可查找到所有提供相同服务的节点的 地址 + 节点信息（metainfo存在value里）

10. Provider在调用register方法注册后，服务的信息就存到了Etcd注册中心上，消费者调用服务前，就会先在注册中心中获取一个可用的服务节点（暂用随机获取进行简单的负载均衡✔），然后调用该地址即可实现服务

11. 接下来还要做一些注册中心的优化（心跳检测续期，停机节点下线，服务缓存）—— **TODO**，以及扩展注册中心（使用Zookeeper）

12. Etcd的Key每30s自动过期，Provider只要在过期前给自己的服务续期，这样就一口气自动地完成了 `心跳检测 + 续期`，以及不可用节点的下线（停机的节点无法给自己续期，就被Etcd清掉了）

    框架提供维护一个节点列表，用定时任务实现定期向Etcd发送续期请求（重新注册），续期自己列表中的节点，续不上期的就会自动下线了

    这样Provider就不需要自己写一个用定时任务来进行心跳检测的方法了，只需要在注册中心初始化时启动心跳检测方法就可以了

13. 