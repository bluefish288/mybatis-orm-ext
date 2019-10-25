package com.mybatisext.mybatis.intercept.handlers;

import com.mybatisext.mybatis.ArgHolder;
import com.mybatisext.page.PageParam;
import com.mybatisext.thread.CustomThreadLocal;
import com.mybatisext.util.ReflectHelper;
import com.mybatisext.util.SqlUtil;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PageInterceptHandler extends AbstractInterceptHandler {

    private final static Logger logger = LoggerFactory.getLogger(PageInterceptHandler.class);

    private final static ThreadLocal<MappedStatement> MAPPED_STATEMENT_THREAD_LOCAL = new CustomThreadLocal<>();

    @Override
    public void onQueryOfExecutor(MappedStatement ms, Object parameter) throws Throwable {

        if(!(parameter instanceof PageParam)){
            return;
        }

        logger.info(ms.getId());

        MAPPED_STATEMENT_THREAD_LOCAL.set(ms);
    }

    @Override
    public void onPrepareOfStatementHandler(StatementHandler statementHandler, Connection connection) throws Throwable {

        MappedStatement mappedStatement = MAPPED_STATEMENT_THREAD_LOCAL.get();

        if(null == mappedStatement){
            return;
        }

        BoundSql boundSql = statementHandler.getBoundSql();


        if(!(boundSql.getParameterObject() instanceof PageParam)){
            return;
        }

        PageParam pageParam = (PageParam) boundSql.getParameterObject();

        String selectSql = boundSql.getSql();
        logger.info(selectSql);

        String countSql = SqlUtil.getCountSql(selectSql);
        logger.info(countSql);


        int count = queryCount(mappedStatement, countSql, boundSql, pageParam, connection);

        if(pageParam.getCurrentPage() < 1){
            pageParam.setCurrentPage(1);
        }else{
            int totalPage = (count % pageParam.getPageSize() == 0) ? (count / pageParam.getPageSize()) : ((count / pageParam.getPageSize())+1);
            if(totalPage > 0){
                if(pageParam.getCurrentPage() > totalPage){
                    pageParam.setCurrentPage(totalPage);
                }
            }
        }

        int offset = (pageParam.getCurrentPage() - 1) * pageParam.getPageSize();

        selectSql += " limit " + offset + "," + pageParam.getPageSize();

        logger.info(selectSql);

        ReflectHelper.setValueByFieldName(boundSql, "sql", selectSql);

        ArgHolder.PAGE_COUNT_HOLDER.set(count);

        MAPPED_STATEMENT_THREAD_LOCAL.remove();
    }

    @Override
    public void onSetParametersOfParameterHandler(ParameterHandler parameterHandler, PreparedStatement preparedStatement) throws Throwable {

    }

    private int queryCount(MappedStatement mappedStatement, String countSql, BoundSql boundSql, Object parameterObject, Connection connection) throws SQLException {
        BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), parameterObject);

        if(null!=boundSql.getParameterMappings()){
            for(ParameterMapping mapping : boundSql.getParameterMappings()){
                String prop = mapping.getProperty();
                if(boundSql.hasAdditionalParameter(prop)){
                    countBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
                }
            }
        }

        MappedStatement countMappedStatement = buildMappedStatement(mappedStatement, new SqlSource() {
            @Override
            public BoundSql getBoundSql(Object parameterObject) {
                return countBoundSql;
            }
        }, mappedStatement.getId() + "_LIMIT", mappedStatement.getResultMaps());

        ParameterHandler parameterHandler = new DefaultParameterHandler(countMappedStatement, parameterObject, countBoundSql);

        PreparedStatement countStmt = connection.prepareStatement(countSql);

        parameterHandler.setParameters(countStmt);

        ResultSet rs = countStmt.executeQuery();
        int count = rs.next() ? rs.getInt(1) : 0;

        logger.info(String.valueOf(count));

        rs.close();
        countStmt.close();

        return count;
    }
}