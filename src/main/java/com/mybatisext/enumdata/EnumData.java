package com.mybatisext.enumdata;




import com.mybatisext.exception.InternalException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnumData<T> {

    private Class enumClazz;

    private Map<T, EnumValue> enumValueMap = new ConcurrentHashMap<T, EnumValue>();

    public EnumData(Class enumClazz){
        this.enumClazz = enumClazz;

        if(!enumClazz.isEnum()){
            throw new InternalException();
        }

        for(Object obj : enumClazz.getEnumConstants()){
            if(obj instanceof EnumValue){
                EnumValue<T> enumValue = (EnumValue<T>) obj;
                enumValueMap.put(enumValue.getCode(),enumValue);
            }
        }

    }

    public EnumValue get(Object code){
        return enumValueMap.get(code);
    }

    @Override
    public String toString() {
        return "EnumData{" +
                "enumClazz=" + enumClazz +
                ", enumValueMap=" + enumValueMap +
                '}';
    }
}
