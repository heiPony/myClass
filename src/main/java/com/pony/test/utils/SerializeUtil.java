package com.pony.test.utils;


import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {
    public SerializeUtil() {
    }

    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;

        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception var4) {
            return null;
        }
    }

}
