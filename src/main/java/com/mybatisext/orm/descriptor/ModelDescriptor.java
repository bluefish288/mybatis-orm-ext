package com.mybatisext.orm.descriptor;




import com.mybatisext.exception.NoColumnAnnotationFound;
import com.mybatisext.exception.NoTableAnnotationFound;
import com.mybatisext.orm.annotation.Table;
import com.mybatisext.util.CollectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class ModelDescriptor<T> {

    private Class<T> clazz;

    private String tableName;

    private Map<String,FieldDescriptor> fieldDescriptorMap;

    public ModelDescriptor(Class<T> clazz){
        this.clazz = clazz;

        Table table = clazz.getAnnotation(Table.class);
        if(null == table){
            throw new NoTableAnnotationFound(this.clazz);
        }
        tableName = table.value();

        fieldDescriptorMap = CollectionUtil.toLinkedHashMap(FieldDescriptor::getName, Function.<FieldDescriptor>identity(), DescriptorUtil.fieldDescriptors(this.clazz));

        if(fieldDescriptorMap.size() == 0){
            throw new NoColumnAnnotationFound(this.clazz);
        }
    }

    public String tableName(){
        return tableName;
    }

    public Object getFieldValue(String fieldName, Object obj) {
        FieldDescriptor fd = fieldDescriptorMap.get(fieldName);
        if(null == fd){
            return null;
        }
        Method readMethod = fd.getReadMethod();
        if(null == readMethod){
            return null;
        }
        try {
            return readMethod.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    public Collection<FieldDescriptor> fieldDescriptors(){
        return fieldDescriptorMap.values();
    }

}
