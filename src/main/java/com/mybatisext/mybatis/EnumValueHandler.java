package com.mybatisext.mybatis;

import com.mybatisext.enumdata.EnumContext;
import com.mybatisext.enumdata.EnumValue;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnumValueHandler extends BaseTypeHandler<EnumValue> {

    private Class<EnumValue> cls;

    public EnumValueHandler(Class<EnumValue> cls) {
        this.cls = cls;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EnumValue parameter, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {
            ps.setObject(i, parameter.getCode());
        } else {
            ps.setObject(i, parameter.getCode(), jdbcType.TYPE_CODE); // see r3589
        }
    }

    @Override
    public EnumValue getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object rst = rs.getObject(columnName);
        if(rs.wasNull()){
            return null;
        }
        return null == rst ? null : (EnumValue) EnumContext.INSTANCE.get(cls, rst);
    }

    @Override
    public EnumValue getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object rst = rs.getObject(columnIndex);
        return null == rst ? null : (EnumValue) EnumContext.INSTANCE.get(cls, rst);
    }

    @Override
    public EnumValue getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object s = cs.getObject(columnIndex);
        return s == null ? null : (EnumValue) EnumContext.INSTANCE.get(cls, s);
    }
}