package com.mybatisext.mybatis;

import com.mybatisext.enumdata.EnumValue;
import com.mybatisext.mybatis.intercept.CustomInterceptor;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;

public class CustomSqlSessionFactoryBean extends SqlSessionFactoryBean{

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Configuration configuration = super.getObject().getConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.getTypeHandlerRegistry().register(EnumValue.class, EnumValueHandler.class);
        configuration.addInterceptor(new CustomInterceptor());
    }
}