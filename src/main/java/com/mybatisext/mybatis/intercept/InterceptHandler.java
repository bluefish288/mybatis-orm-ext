package com.mybatisext.mybatis.intercept;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface InterceptHandler {

    public void onQueryOfExecutor(MappedStatement ms, Object parameter) throws Throwable;

    public void onPrepareOfStatementHandler(StatementHandler statementHandler, Connection connection) throws Throwable;

    public void onSetParametersOfParameterHandler(ParameterHandler parameterHandler, PreparedStatement preparedStatement) throws Throwable;

}