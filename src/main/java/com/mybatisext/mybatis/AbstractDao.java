package com.mybatisext.mybatis;

import com.mybatisext.orm.descriptor.FieldDescriptor;
import com.mybatisext.orm.descriptor.ModelDescriptor;
import com.mybatisext.orm.descriptor.ModelDescriptorContext;
import com.mybatisext.orm.descriptor.ModelWrapper;
import com.mybatisext.page.Page;
import com.mybatisext.page.PageParam;
import org.apache.ibatis.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface AbstractDao {

    public final static String INSERT_METHOD_NAME = "insertByModel";

    public final static String UPDATE_METHOD_NAME = "updateByModel";

    public final static String UPDATE_WITH_NULL_METHOD_NAME = "updateByModelWithNull";

    public final static String FIND_BY_ID_METHOD = "findModelById";

    final static String KEY_PROPERTY = "model.id";

    @InsertProvider(type = ModelSqlProvider.class, method = INSERT_METHOD_NAME)
    @Options(useGeneratedKeys = true, keyProperty = KEY_PROPERTY)
    public boolean insertByModel(InsertModel insertModel);

    @UpdateProvider(type = ModelSqlProvider.class, method = UPDATE_METHOD_NAME)
    public int updateByModel(Object model);

    @UpdateProvider(type = ModelSqlProvider.class, method = UPDATE_WITH_NULL_METHOD_NAME)
    public int updateByModelWithNull(Object model);

    public default  <R, P extends PageParam> Page<R> findPage(P pageParam, Function<P, List<R>> dataListFunc){

        if(null == pageParam || null == dataListFunc){
            return Page.EMPTY_PAGE;
        }

        List<R> datas = dataListFunc.apply(pageParam);

        if(null == datas || datas.size() == 0){
            return Page.EMPTY_PAGE;
        }

        int totalCount = pageParam.isAll() ? datas.size() : ArgHolder.PAGE_COUNT_HOLDER.get();
        ArgHolder.PAGE_COUNT_HOLDER.remove();

        Page<R> page = new Page<>(pageParam, totalCount);
        page.getDataList().addAll(datas);
        return page;
    }

    @SelectProvider(type = ModelSqlProvider.class, method = FIND_BY_ID_METHOD)
    public <T> T findModelById(@Param("clazz") Class<T> clazz, @Param("id") Integer id);



    class ModelSqlProvider {

        private final static Logger logger = LoggerFactory.getLogger(ModelSqlProvider.class);

        public String insertByModel(InsertModel insertModel){

            Object model = insertModel.getModel();

            ModelWrapper<?> wrapper = new ModelWrapper<>(model);

            String tableName = wrapper.tableName();

            Map<String, Object> columnValueMap = wrapper.columnValueMap();


            StringBuilder insertStatement = new StringBuilder();
            List<Object> values = new ArrayList<>();


            insertStatement.append("INSERT");
            if(insertModel.isIgnoreOnDuplicate()){
                insertStatement.append(" IGNORE");
            }
            insertStatement.append(" INTO ")
                    .append(tableName)
                    .append(" (");

            int columnCount = 0;
            for(String column : columnValueMap.keySet()){
                Object value = columnValueMap.get(column);
                if(null== value){
                    continue;
                }

                if(columnCount > 0){
                    insertStatement.append(", ");
                }
                insertStatement.append(column);

                values.add(value);

                columnCount ++ ;

            }

            if(columnCount == 0){
                return null;
            }

            insertStatement.append(") VALUES(");

            for (int i = 0; i < columnCount; i++) {
                if (i > 0) {
                    insertStatement.append(", ");
                }
                insertStatement.append("?");
            }
            insertStatement.append(")");

            ArgHolder.INSERT_UPDATE_ARG_HOLDER.set(values);

            return insertStatement.toString();
        }

        public String updateByModel(@Param("model") Object model){

            ModelWrapper<?> wrapper = new ModelWrapper<>(model);

            String tableName = wrapper.tableName();

            Integer dataId = wrapper.getDataId();

            Map<String, Object> columnValueMap = wrapper.columnValueMap();


            StringBuilder updateStatement = new StringBuilder();
            List<Object> values = new ArrayList<>();

            updateStatement.append("UPDATE ")
                    .append(tableName)
                    .append(" SET ");

            int columnCount = 0;
            for(String column : columnValueMap.keySet()){
                Object value = columnValueMap.get(column);
                if(null== value){
                    continue;
                }

                if(columnCount > 0){
                    updateStatement.append(", ");
                }
                updateStatement.append(column).append(" = ?");

                values.add(value);

                columnCount ++ ;

            }

            updateStatement.append(" where id = ?");
            values.add(dataId);

            ArgHolder.INSERT_UPDATE_ARG_HOLDER.set(values);

            return updateStatement.toString();
        }

        public String updateByModelWithNull(@Param("model") Object model){

            ModelWrapper<?> wrapper = new ModelWrapper<>(model);

            String tableName = wrapper.tableName();

            Integer dataId = wrapper.getDataId();

            Map<String, Object> columnValueMap = wrapper.columnValueMap();


            StringBuilder updateStatement = new StringBuilder();
            List<Object> values = new ArrayList<>();

            updateStatement.append("UPDATE ")
                    .append(tableName)
                    .append(" SET ");

            int columnCount = 0;
            for(String column : columnValueMap.keySet()){
                Object value = columnValueMap.get(column);

                if(columnCount > 0){
                    updateStatement.append(", ");
                }
                updateStatement.append(column).append(" = ?");

                values.add(value);

                columnCount ++ ;

            }

            updateStatement.append(" where id = ?");
            values.add(dataId);

            ArgHolder.INSERT_UPDATE_ARG_HOLDER.set(values);

            return updateStatement.toString();
        }

        public String findModelById(Map<String,Object> map){
            Class<?> clazz = (Class<?>) map.get("clazz");
            Integer id = (Integer) map.get("id");
            ModelDescriptor<?> descriptor = ModelDescriptorContext.INSTANCE.get(clazz);
            if(null == descriptor){
                return null;
            }

            StringBuilder columns = new StringBuilder();

            for (FieldDescriptor fd : descriptor.fieldDescriptors()) {
                if (columns.length() > 0) {
                    columns.append(", ");
                }
                columns.append(fd.getColumn());
            }

            String sql = "select "+columns.toString()+" from " + descriptor.tableName() + " where id = "+id;

            logger.info(sql);

            return sql;
        }



    }
}