package com.mybatisext.mybatis.intercept.handlers;

import com.mybatisext.mybatis.AbstractDao;
import com.mybatisext.mybatis.ArgHolder;
import com.mybatisext.thread.CustomThreadLocal;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class InsertUpdateInterceptHandler extends AbstractInterceptHandler {

    private final static ThreadLocal<MappedStatement> MAPPED_STATEMENT_THREAD_LOCAL = new CustomThreadLocal<>();

    @Override
    public void onQueryOfExecutor(MappedStatement ms, Object parameter) throws Throwable {

        String sqlId = ms.getId();

        if((!sqlId.endsWith(AbstractDao.INSERT_METHOD_NAME)) && (!sqlId.endsWith(AbstractDao.UPDATE_METHOD_NAME)) && (!sqlId.endsWith(AbstractDao.UPDATE_WITH_NULL_METHOD_NAME))){
            return;
        }

        MAPPED_STATEMENT_THREAD_LOCAL.set(ms);

    }

    @Override
    public void onPrepareOfStatementHandler(StatementHandler statementHandler, Connection connection) throws Throwable {

    }

    @Override
    public void onSetParametersOfParameterHandler(ParameterHandler parameterHandler, PreparedStatement preparedStatement) throws Throwable {

        MappedStatement mappedStatement = MAPPED_STATEMENT_THREAD_LOCAL.get();
        if(null == mappedStatement){
            return;
        }

        List<Object> insertArgs = ArgHolder.INSERT_UPDATE_ARG_HOLDER.get();
        if (null == insertArgs || insertArgs.size() == 0) {
            return;
        }

        for (int i = 0; i < insertArgs.size(); i++) {
            preparedStatement.setObject(i + 1, insertArgs.get(i));
        }

        ArgHolder.INSERT_UPDATE_ARG_HOLDER.remove();
        MAPPED_STATEMENT_THREAD_LOCAL.remove();
    }
}