package com.mybatisext.enumdata;

import java.io.Serializable;

/**
 * Created by xiebo on 15-2-27.
 */
public interface EnumValue<T> extends Serializable {

    public T getCode();

}
