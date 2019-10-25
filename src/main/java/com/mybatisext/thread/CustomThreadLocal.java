package com.mybatisext.thread;

import java.util.Collection;

public class CustomThreadLocal<T> extends ThreadLocal<T> {

    @Override
    public void remove() {
        Object obj = super.get();
        if (null != obj) {
            if (obj instanceof Collection) {
                Collection c = (Collection) obj;
                try {
                    c.clear();
                }catch (UnsupportedOperationException e){
                    // ignore
                }
            }
            obj = null;
        }

        super.remove();
    }
}