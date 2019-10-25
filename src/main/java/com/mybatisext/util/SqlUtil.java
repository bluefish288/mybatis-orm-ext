package com.mybatisext.util;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import java.util.List;

public class SqlUtil {

    private final static String DEFAULT_COUNT_FIELD = "1";

    public static String getCountSql(String selectSql){
        selectSql = cleanSql(selectSql).toLowerCase();


        SQLStatementParser parser = new MySqlStatementParser(selectSql);


        SQLSelectStatement selectStatement = (SQLSelectStatement) parser.parseSelect();

        SQLSelect sqlSelect = selectStatement.getSelect();

        SQLSelectQueryBlock sqlSelectQueryBlock = sqlSelect.getQueryBlock();

        StringBuilder sql = new StringBuilder();

        String fromStr = SQLUtils.toSQLString(sqlSelectQueryBlock.getFrom());

        sql.append("select count("+getCountField(sqlSelectQueryBlock)+") from ").append(fromStr);

        SQLExpr where = sqlSelectQueryBlock.getWhere();

        if(null!=where){
            String whereStr = SQLUtils.toSQLString(where);
            sql.append(" where ").append(whereStr);
        }

        return sql.toString();

    }

    private static String getCountField(SQLSelectQueryBlock sqlSelectQueryBlock){
        if(sqlSelectQueryBlock.getDistionOption() != SQLSetQuantifier.DISTINCT){
            // 没有distinct
            return DEFAULT_COUNT_FIELD;
        }
        List<SQLSelectItem> selectItems = sqlSelectQueryBlock.getSelectList();
        if(null == selectItems || selectItems.size() == 0){
            return DEFAULT_COUNT_FIELD;
        }
        SQLSelectItem first = selectItems.get(0);

        if(null == first){
            return DEFAULT_COUNT_FIELD;
        }

        if(first.getExpr() instanceof SQLPropertyExpr){
            SQLPropertyExpr expr = (SQLPropertyExpr) first.getExpr();
            return "DISTINCT "+expr.getOwnernName()+"."+expr.getName();
        }

        return DEFAULT_COUNT_FIELD;
    }

    public static String cleanSql(String selectSql) {
        char[] chars = selectSql.toCharArray();
        StringBuilder builder = new StringBuilder();

        boolean isSpace = false;
        for (char c : chars) {
            if (Character.isSpaceChar(c)) {
                if (!isSpace) {
                    isSpace = true;
                    builder.append(" ");
                }
            } else {
                isSpace = false;
            }
            if (!isSpace) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static void main(String[] args){
        String sql = "SELECT DISTINCT r.id as requirementId, r.flow_id, r.title, r.created, f.requirement_state, f.scheme_state, a.plat_identifier FROM flow_requirements r  LEFT JOIN track_flows f on r.flow_id = f.id LEFT JOIN flow_requirement_apps a ON r.id = a.requirement_id";

        System.out.println(getCountSql(sql));

        String sql2 = "SELECT r.id as requirementId, r.flow_id, r.title, r.created, f.requirement_state, f.scheme_state, a.plat_identifier FROM flow_requirements r  LEFT JOIN track_flows f on r.flow_id = f.id LEFT JOIN flow_requirement_apps a ON r.id = a.requirement_id";

        System.out.println(getCountSql(sql2));


    }
}