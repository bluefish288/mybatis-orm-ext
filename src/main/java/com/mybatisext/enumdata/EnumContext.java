package com.mybatisext.enumdata;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EnumContext {

    public Map<Class, EnumData> dataMap = new ConcurrentHashMap<Class, EnumData>();

    private Lock lock = new ReentrantLock(true);

    private EnumContext() {}

    public static EnumContext INSTANCE = new EnumContext();

    public Object get(Class enumClazz, Object code) {
        EnumData enumData = dataMap.get(enumClazz);
        if (null == enumData) {
            if(!enumClazz.isEnum()){
                return null;
            }
            if (enumClazz.equals(EnumValue.class)) {
                return null;
            }
            if (!Arrays.asList(enumClazz.getInterfaces()).contains(EnumValue.class)) {
                return null;
            }

            lock.lock();
            try {
                enumData = dataMap.get(enumClazz);
                if(null!=enumData){
                    return enumData;
                }
                enumData = new EnumData(enumClazz);
                INSTANCE.dataMap.put(enumClazz, enumData);
            }finally {
                lock.unlock();
            }

        }
        return enumData.get(code);
    }
}
