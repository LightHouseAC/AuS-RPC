package com.aus.advancedrpc.serializer;

import com.aus.advancedrpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {

//    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>(){
//        {
//            put(SerializerKeys.JDK, new JdkSerializer());
//            put(SerializerKeys.HESSIAN, new HessianSerializer());
//            put(SerializerKeys.HESSIAN2, new Hessian2Serializer());
//            put(SerializerKeys.KRYO, new KryoSerializer());
//            put(SerializerKeys.JSON, new JsonSerializer());
//        }
//    };
//
//    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get("jdk");

    static {
        SpiLoader.load(Serializer.class);
    }

    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class, key);
    }

}
