package org.mybatis.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import javax.annotation.Resource;

/**
 * @Title: StartUp.java
 * @Package com.fendo.mybatis_generator_plus
 * @Description: TODO
 * @author fendo
 * @date 2017年10月5日 下午3:53:17
 * @version V1.0
 */
class Main {
    private static Properties prop;
    static {
        prop = new Properties();
    }
    public static void main(String[] args) throws URISyntaxException {
        System.out.println(Main.class.getResource("/"));

        try {
            System.out.println("--------------------start generator-------------------");
            List<String> warnings = new ArrayList<String>();
            boolean overwrite = true;
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            File file = new File(Main.class.getClass().getResource("/config.xml").getPath());
            ConfigurationParser cp = new ConfigurationParser(warnings);
            InputStream is = new FileInputStream(file);
            Configuration config = cp.parseConfiguration(is);
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
            System.out.println("--------------------end generator-------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (XMLParserException e) {
            e.printStackTrace();
        }
    }
//    public String outResources(){
//        return this.
//    }
}