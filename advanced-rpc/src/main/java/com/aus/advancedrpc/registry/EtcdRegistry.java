package com.aus.advancedrpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.aus.advancedrpc.config.RegistryConfig;
import com.aus.advancedrpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry{

    private Client client;

    private KV kvClient;

    public static final String ETCD_ROOT_PATH = "/rpc/";

    private final Set<String> localRegisteredNodeKeySet = new HashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        Lease leaseClient = client.getLeaseClient();
        // 设置一个合理时间的租期，后面通过心跳监测给服务续约
        long leaseId = leaseClient.grant(30L).get().getID();

        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        // key => 服务节点key
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        // value => 服务节点的信息
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 关联键值对和租约，来实现服务节点的自动过期
        PutOption putOption = PutOption.builder()
                .withLeaseId(leaseId).
                build();
        kvClient.put(key, value, putOption).get();

        //  维护本地已注册的节点（每个实例存自己的节点）
        localRegisteredNodeKeySet.add(registerKey);
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        kvClient.delete(key);
        localRegisteredNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 通过前缀搜索的方式来找服务节点：类似文件系统找到一个服务的父目录（相同前缀）
        String servicePrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            GetOption getOption = GetOption.builder()
                    .isPrefix(true)
                    .build();
            List<KeyValue> keyValues = kvClient.get(
                    ByteSequence.from(servicePrefix, StandardCharsets.UTF_8),
                    getOption
            ).get().getKvs();
            return keyValues.stream().map(keyValue -> {
               String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
               return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());
        } catch (Exception e){
            throw new RuntimeException("获取服务节点列表失败", e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前注册中心已下线");
        if (kvClient != null) kvClient.close();
        if (client != null)client.close();
    }

    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                for (String key : localRegisteredNodeKeySet){
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        if (CollUtil.isEmpty(keyValues)) continue; //   已过期，无法续签，需要重新注册
                        KeyValue keyValue = keyValues.get(0);   //  只能获取列表getKvs()，但是只有1个节点只应该有一个服务来续期 (用的是Set)
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);  //  用重新注册的方式来续签
                    } catch (Exception e){
                        throw new RuntimeException(String.format("服务 %s 续签失败", key), e);
                    }
                }
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }
}
