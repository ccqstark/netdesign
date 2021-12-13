package chapter14;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.PreparedStatement;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBOperate2 {
    public static void main(String[] args) throws Exception {

        java.sql.Connection con = MyConnection.get();

        DatabaseMetaData metaData = (DatabaseMetaData) con.getMetaData();//返回数据库的一些元信息
        /*
        以下语句调用会返回一个ResultSet结果集，该结果集含4列，其中有一列含表名（该列名称为TABLE_NAME，可以通过rsTables.getString("TABLE_NAME")获得）
        可通过遍历rsTables得到包含的表名称
        */
        ResultSet rsTables = metaData.getTables(null,null,null,new String[]{"TABLE"});
        //用于保存表名的数组列表，供之后遍历访问
        ArrayList<String> tablesName = new ArrayList<>();
        System.out.println("该数据库中包含的表：");
        while (rsTables.next())
        {
            String tableName = rsTables.getString("TABLE_NAME");
            System.out.println(tableName);
            tablesName.add(tableName);
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        for (String tableName : tablesName)
        {
            //保存字段名
            ArrayList<String> filedsName = new ArrayList<>();
            String sql = "select * from " + tableName ;
            stmt = (PreparedStatement) con.prepareStatement(sql);
            rs = stmt.executeQuery();
            ResultSetMetaData fields = rs.getMetaData();//会返回该表的字段信息
            int n = fields.getColumnCount();//有多少个字段
            System.out.println(tableName + "字段：");
            for(int i=1;i<=n;i++)
            {
                //getColumnName可以获得字段名
                String fieldName = fields.getColumnName(i);
                System.out.println(fieldName);

            }
            // System.out.println();
            // System.out.println(tableName + "数据：");
            // //如果有必要，还可以循环遍历列表结果，获取有价值信息
            // while (rs.next())
            // {
            //
            // }
            System.out.println("-----------------------------------");
        }
    }
}
