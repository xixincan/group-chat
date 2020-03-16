package com.xxc.common.util;

import cn.hutool.log.StaticLog;
import com.xxc.entity.exp.ValidException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author xixincan
 * 2020-03-12
 * @version 1.0.0
 */
public class MySerializeUtil {

    public static byte[] serialize(Object object) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            // 序列化
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (Exception e) {
            StaticLog.error("对象序列化失败:{}", e);
            StaticLog.error(e);
        }
        return null;
    }

    public static <T> T deserialize(byte[] bytes, Class<T> tClass) {
        // 反序列化
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            Object object = ois.readObject();
            if (object.getClass().isAssignableFrom(tClass)) {
                return (T) object;
            }
            throw new ValidException("反序列化类型不匹配");
        } catch (Exception e) {
            StaticLog.error("对象反序列化失败:{}", e);
            StaticLog.error(e);
        }
        return null;
    }

}
