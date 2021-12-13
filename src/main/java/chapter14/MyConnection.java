package chapter14;

import com.mysql.jdbc.Driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class MyConnection {

    public static java.sql.Connection get() throws Exception {

        // 读取配置文件
        Properties props = new Properties();
        props.load(new java.io.FileInputStream("src/main/resources/dbconf.properties"));

        // URL resource = MyConnection.class.getResource("/dbconf.properties");
        // File file = new File(resource.toURI());
        // System.out.println(resource.toURI());
        // FileInputStream f = new FileInputStream(file);
        // props.load(f);

        //加载MySQL驱动器，其中com.mysql.jdbc.Driver就是由下载的mysql驱动包提供
        Class jdbcDriver = Class.forName(props.getProperty("DBC_DRIVER"));
        //注册MySQL驱动器
        java.sql.DriverManager.registerDriver((Driver) jdbcDriver.newInstance());

        //指定数据库所在位置，先用本地地址测试，访问本地的数据库
        String dbUrl = props.getProperty("DB_URL");
        //指定用户名和密码
        String dbUser = props.getProperty("DB_USER");
        String dbPwd = props.getProperty("DB_PASSWORD");

        //创建数据库连接对象
        java.sql.Connection con = java.sql.DriverManager.getConnection(dbUrl, dbUser, dbPwd);

        return con;
    }

}
