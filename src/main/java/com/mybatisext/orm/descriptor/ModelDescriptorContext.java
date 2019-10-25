package com.mybatisext.orm.descriptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ModelDescriptorContext {

    private Map<Class<?>,ModelDescriptor> contextMap = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock(true);

    public <T> ModelDescriptor<T> get(Class<T> clazz){

        ModelDescriptor<T> descriptor = contextMap.get(clazz);
        if(null == descriptor){
            lock.lock();
            try {
                if(null != descriptor){
                    return descriptor;
                }
                descriptor = new ModelDescriptor<>(clazz);
                contextMap.putIfAbsent(clazz, descriptor);
            }finally {
                lock.unlock();
            }

        }

        return descriptor;
    }

    private ModelDescriptorContext(){}

    public static ModelDescriptorContext INSTANCE = new ModelDescriptorContext();

}
