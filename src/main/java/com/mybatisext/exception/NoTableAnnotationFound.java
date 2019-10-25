package com.mybatisext.exception;


public class NoTableAnnotationFound  extends InternalException {

    public NoTableAnnotationFound(Class clazz) {
        super("no @Table annotation found on class "+clazz.getName());
    }
}