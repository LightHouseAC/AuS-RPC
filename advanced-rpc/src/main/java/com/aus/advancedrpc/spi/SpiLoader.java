package com.aus.advancedrpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.aus.advancedrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {

    /*
    存储已经加载的类：接口名 => (key => 实现类)
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /*
    对象实例缓存，类路径 => 对象实例，单例模式避免多个实例消耗内存
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /*
    自带SPI目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /*
    自定义SPI目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /*
    SPI扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    public static void loadAll(){
        log.info("加载所有SPI");
        for (Class<?> clazz : LOAD_CLASS_LIST){
            load(clazz);
        }
    }

    public static <T> T getInstance(Class<?> tClass, String key){
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap == null){
            throw new RuntimeException(String.format("SpiLoader未加载 %s 类型", tClassName));
        }
        if (!keyClassMap.containsKey(key)){
            throw new RuntimeException(String.format("SpiLoader的 %s 不存在 key=%s 的类型", tClassName, key));
        }
        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();
        if(!instanceCache.containsKey(implClassName)){
            try{
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e){
                String errMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }

    public static Map<String, Class<?>> load(Class<?> loadClass){
        log.info("加载类型为 {} 的SPI", loadClass.getName());

        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for(String scanDir : SCAN_DIRS){
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            for (URL resource : resources){
                try{
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line=bufferedReader.readLine()) != null){
                        String[] strArray = line.split("=");
                        if (strArray.length > 1){
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e){
                    log.error("SPI 资源加载失败", e);
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

}
