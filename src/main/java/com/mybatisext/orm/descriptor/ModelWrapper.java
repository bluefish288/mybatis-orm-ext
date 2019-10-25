package com.mybatisext.orm.descriptor;


import com.mybatisext.enumdata.EnumValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ModelWrapper<T> {

    private final static Logger logger = LoggerFactory.getLogger(ModelWrapper.class);

    private Object model;

    private Map<String, Object> columnValueMap;

    private ModelDescriptor<T> descriptor;

    private Lock lock = new ReentrantLock();

    public ModelWrapper(Object model) {
        this.model = model;

        Class<T> clazz = (Class<T>) model.getClass();
        this.descriptor = ModelDescriptorContext.INSTANCE.get(clazz);

        init();
    }

    private void init() {
        Collection<FieldDescriptor> fds = descriptor.fieldDescriptors();
        columnValueMap = new LinkedHashMap<>(fds.size());

        lock.lock();
        try {
            for (FieldDescriptor pd : fds) {
                if (pd.getName().toLowerCase().equals("id")) {
                    continue;
                }
                Object value = getValue(pd);
                columnValueMap.put(pd.getColumn(), value);
            }
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }

    }

    public Integer getDataId() {
        return (Integer) descriptor.getFieldValue("id", model);

    }

    public String tableName() {
        return descriptor.tableName();
    }

    public Set<String> columns() {
        return columnValueMap.keySet();
    }

    public Map<String, Object> columnValueMap() {
        return columnValueMap;
    }

    private Object getValue(FieldDescriptor fd) throws InvocationTargetException, IllegalAccessException {

        Method readMethod = fd.getReadMethod();
        if (null == readMethod) {
            return null;
        }
        Object value = fd.getReadMethod().invoke(model);
        if (null == value) {
            return null;
        }
        if (value instanceof EnumValue) {
            return ((EnumValue) value).getCode();
        }
        return value;
    }
}
