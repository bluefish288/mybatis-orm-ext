package com.mybatisext.mybatis.intercept;


import com.mybatisext.mybatis.intercept.handlers.FindByIdInterceptHandler;
import com.mybatisext.mybatis.intercept.handlers.InsertUpdateInterceptHandler;
import com.mybatisext.mybatis.intercept.handlers.PageInterceptHandler;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class,method = "query",args = {MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class}),
        @Signature(type = Executor.class,method = "update",args = {MappedStatement.class,Object.class}),
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = ParameterHandler.class, method = "setParameters", args = PreparedStatement.class)
})
public class CustomInterceptor implements Interceptor {

    private List<InterceptHandler> handlers = new ArrayList<>();

    public CustomInterceptor() {
        handlers.add(new PageInterceptHandler());
        handlers.add(new InsertUpdateInterceptHandler());
        handlers.add(new FindByIdInterceptHandler());
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object target = invocation.getTarget();
        Object[] args = invocation.getArgs();

        if(target instanceof Executor){

            MappedStatement mappedStatement = (MappedStatement) args[0];
            Object parameter = args[1];

            for(InterceptHandler handler : handlers){
                handler.onQueryOfExecutor(mappedStatement, parameter);
            }

            return invocation.proceed();
        }

        if(target instanceof StatementHandler){

            Connection connection = (Connection) args[0];

            for(InterceptHandler handler : handlers){
                handler.onPrepareOfStatementHandler((StatementHandler) target, connection);
            }

            return invocation.proceed();
        }

        if(target instanceof ParameterHandler){

            PreparedStatement preparedStatement = (PreparedStatement) args[0];

            for(InterceptHandler handler : handlers){
                handler.onSetParametersOfParameterHandler((ParameterHandler) target, preparedStatement);
            }

            return invocation.proceed();
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {

        if(target instanceof Executor
                || target instanceof ParameterHandler
                || target instanceof StatementHandler){
            return Plugin.wrap(target, this);
        }

        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }


}