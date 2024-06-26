package com.aus.advancedrpc.serializer;

import java.io.*;

public class JdkSerializer implements Serializer{

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(os);
        objectOS.writeObject(object);
        objectOS.close();
        return os.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        ObjectInputStream objectIS = new ObjectInputStream(is);
        try{
            return (T) objectIS.readObject();
        } catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        } finally {
            objectIS.close();
        }
    }
}
