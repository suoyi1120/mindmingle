// package com.group02.mindmingle;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// public class MindmingleApplication {

//     public static void main(String[] args) {
//         SpringApplication.run(MindmingleApplication.class, args);
//     }

// }


package com.group02.mindmingle;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerApplicationContext;
import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootApplication
public class MindmingleApplication implements CommandLineRunner {

    @Autowired
    private WebServerApplicationContext webServerApplicationContext;

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(MindmingleApplication.class, args);
    }

    @Override
    public void run(String... args) throws SQLException {
        int port = webServerApplicationContext.getWebServer().getPort();
        String host = "localhost"; // 默认是本地运行
        System.out.println("🚀 Spring Boot 运行地址: http://" + host + ":" + port);

        // 打印数据库连接信息
        System.out.println("🔗 连接的数据库: " + dataSource.getConnection().getMetaData().getURL());
    }
}
