package chapter14;

import com.mysql.jdbc.Driver;

import java.io.IOException;
import java.sql.*;

public class DBOperate1 {

    public static void main(String[] args) throws Exception {

        java.sql.Connection con = MyConnection.get();

        // //创建sql查询语句
        // String sql = "select NO,NAME,AGE,CLASS from students where name like ? and age=?";
        //
        // //创建数据库执行对象
        // PreparedStatement stmt = con.prepareStatement(sql);
        // //设置sql语句参数，查找名字以“小”开头，年龄23岁的记录
        // stmt.setObject(1, "小%");
        // stmt.setObject(2, 23);
        //
        // //从数据库的返回集合中读出数据
        // ResultSet rs = stmt.executeQuery();
        //
        // //循环遍历结果
        // while (rs.next()) {
        //     //不知道字段类型的情况下，也可以用rs.getObject(…)来打印输出结果
        //     System.out.print(rs.getString(1) + "\t");
        //     System.out.print(rs.getString(2) + "\t");
        //     System.out.print(rs.getInt(3) + "\t");
        //     System.out.print(rs.getString(4) + "\n");
        // }
        // System.out.println("------------------------------------");


        //设置插入记录的sql语句(如何避免重复插入学号相同的信息？)
        String sql = "insert into peoples2(NO,NAME,AGE,CLASS,IP) values(?,?,?,?,?)";
        com.mysql.jdbc.PreparedStatement stmt = (com.mysql.jdbc.PreparedStatement) con.prepareStatement(sql);
        stmt.setObject(1, "20191002914");
        stmt.setObject(2, "陈楚权");
        stmt.setObject(3, "100");
        stmt.setObject(4, "软工1903");
        stmt.setObject(5, "10.173.216.179");
        ResultSet rs = null;
        try {
            stmt.executeUpdate();
            //查询是否插入数据成功
            sql = "select  * from peoples2 where NAME=?";
            stmt = (com.mysql.jdbc.PreparedStatement) con.prepareStatement(sql);
            stmt.setObject(1, "陈楚权");
            rs = stmt.executeQuery();

            //再次循环遍历结果，看是否成功
            while (rs.next()) {
                System.out.print(rs.getString(1) + "\t");
                System.out.print(rs.getString(2) + "\t");
                System.out.print(rs.getInt(3) + "\t");
                System.out.print(rs.getString(4) + "\n");
                System.out.print(rs.getString(5) + "\n");
                System.out.print(rs.getString(6) + "\n");
            }
        } catch (SQLException e) {

        } finally {
            //释放资源
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (con != null)
                con.close();
        }

    }

}
