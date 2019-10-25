package com.mybatisext.mybatis.intercept.handlers;

import com.mybatisext.mybatis.AbstractDao;
import com.mybatisext.util.ReflectHelper;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class FindByIdInterceptHandler extends AbstractInterceptHandler  {



    @Override
    public void onQueryOfExecutor(MappedStatement ms, Object parameter) throws Throwable {

        String sqlId = ms.getId();

        if(!sqlId.endsWith(AbstractDao.FIND_BY_ID_METHOD)){
            return;
        }

        if(!(parameter instanceof MapperMethod.ParamMap)){
            return;
        }

        MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) parameter;

        if(!paramMap.containsKey("clazz")){
            return;
        }

        Class cls = (Class) paramMap.get("clazz");

        if (null == cls) {
            return;
        }

        ResultMap originResultMap = ms.getResultMaps().get(0);

        ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), originResultMap.getId(), cls, originResultMap.getResultMappings(), originResultMap.getAutoMapping()).build();

//        List<ResultMap> resultMaps = Collections.unmodifiableList(Collections.singletonList(resultMap));

        List<ResultMap> resultMaps = new ArrayList<>(1);
        resultMaps.add(resultMap);

        ReflectHelper.setValueByFieldName(ms, "resultMaps", resultMaps);

    }

    @Override
    public void onPrepareOfStatementHandler(StatementHandler statementHandler, Connection connection) throws Throwable {

    }

    @Override
    public void onSetParametersOfParameterHandler(ParameterHandler parameterHandler, PreparedStatement preparedStatement) throws Throwable {

    }
}