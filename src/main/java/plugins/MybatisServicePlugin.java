package plugins;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;

import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by asuka on 3/14/2018.
 * 建立一个继承自公用mapper和公用service的service 结构是
 *  注释来自 叩丁狼教育 的https://www.jianshu.com/p/66e201b1790a
 * package
 * slf4j
 * spring
 * spring annotation
 * public class xxxxxxxx extends baseService
 */
public class MybatisServicePlugin extends PluginAdapter {
    private List<String> mapperFiles;
    public MybatisServicePlugin() {
        mapperFiles = new ArrayList<String>();
    }

    //配置spring
    private FullyQualifiedJavaType springService;
    private FullyQualifiedJavaType springAutowired;
    //配置log
    private FullyQualifiedJavaType slf4jLogger;
    private FullyQualifiedJavaType slf4jLoggerFactory;
    //mybatis注解
    private FullyQualifiedJavaType mybatisAnnotation;
    //提取公用的service和mapper
    private FullyQualifiedJavaType baseMapper;
    private FullyQualifiedJavaType baseService;
    //获取生成路径
    private FullyQualifiedJavaType targetJava;
    private FullyQualifiedJavaType targetXml;
    private FullyQualifiedJavaType targetService;
    private FullyQualifiedJavaType targetMapper;
    private FullyQualifiedJavaType targetEntity;
    private FullyQualifiedJavaType targetExample;


    //判断上面的javaType
    @Override
    public boolean validate(List<String> list) {
        //生成地址
        targetJava = new FullyQualifiedJavaType(properties.getProperty("target") + "/java");//从配置文件获取相应字段
        targetXml = new FullyQualifiedJavaType(properties.getProperty("target") + "/xml");
        targetService = new FullyQualifiedJavaType(properties.getProperty("target") + "/java/table/Service");
        targetMapper = new FullyQualifiedJavaType(properties.getProperty("target") + "/java/table/Mapper");
        targetEntity = new FullyQualifiedJavaType(properties.getProperty("target") + "/java/table/Entity");
        targetExample = new FullyQualifiedJavaType(properties.getProperty("target") + "/java/table/Entity");
        baseMapper = new FullyQualifiedJavaType(properties.getProperty("baseMapper"));
        baseService = new FullyQualifiedJavaType(properties.getProperty("baseService"));
        mybatisAnnotation = new FullyQualifiedJavaType(properties.getProperty("mybatisAnnotation"));

        //配置import
        slf4jLogger = new FullyQualifiedJavaType("org.slf4j.Logger");
        slf4jLoggerFactory = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
        springAutowired = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
        springService = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
        return true;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles() {
        //创建一个XML文档，注意这个Document不是JAVA DOM的，而是org.mybatis.generator.api.dom.xml.Document
        //在这里传入了两个静态常量，这两个常量就是mybatis配置文件需要用到的DTD，
        //在XmlConstants里面还有很多常量，比如MYBATIS3_MAPPER_SYSTEM_ID和MYBATIS3_MAPPER_PUBLIC_ID（看名字应该知道是什么内容吧~）
        Document document = new Document(
                XmlConstants.MYBATIS3_MAPPER_CONFIG_PUBLIC_ID,
                XmlConstants.MYBATIS3_MAPPER_CONFIG_SYSTEM_ID);
        XmlElement root = new XmlElement("configuration"); //$NON-NLS-1$
        document.setRootElement(root);
        //接着创建根目录，<configuration>，和JavaDOM基本一样，就不啰嗦了；
        //创建一个注释防止误操作
        root.addElement(new TextElement("<!--")); //$NON-NLS-1$
        root.addElement(new TextElement("  这个文件由MyBatis Generator生成.")); //$NON-NLS-1$
        root.addElement(new TextElement("  请勿修改，如有拓展需求请增加插件/或新开启mapper")); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        sb.append("创建时间"+new Date());
        sb.append('.');
        root.addElement(new TextElement(sb.toString()));
        root.addElement(new TextElement("-->")); //$NON-NLS-1$

        //创建mappers节点；
        XmlElement mappers = new XmlElement("mappers"); //$NON-NLS-1$
        root.addElement(mappers);

        //准备根据搜集到的本次生成的mapper.xml文件，为mappers生成mapper子元素
        XmlElement mapper;
        //为每一个mapper.xml文件生成一个对应的mapper子元素；从这里就可以明确的看出，在mapperFiles集合中保存的确实是mapper.xml文件的路径；
        for (String mapperFile : mapperFiles) {
            mapper = new XmlElement("mapper"); //$NON-NLS-1$
            mapper.addAttribute(new Attribute("resource", mapperFile)); //$NON-NLS-1$
            mappers.addElement(mapper);
        }
        //信息量非常大的一句代码，通过这句代码可以看出：
        //1，MBG使用GeneratedXmlFile对象来包装一个要生成的XML文件的所有相关内容；
        //2，该对象的构造方法包含了所有需要的信息
        //3，第一个参数，是该XML文件的内容，即Document；
        //4，第二个参数，是该XML文件的文件名，可以很清楚的看到，先得到fileName参数，否则使用默认的MapperConfig.xml命名（所以，后缀名是要自己传给MBG的）
        //5，第三个参数和第四个参数，分别是生成XML文件的targetPackage和targetProject；所以，可以看到MBG把文件的具体生成过程完全包装，只需要我们提供package和project即可；
        //6，第四个参数代表是否合并，
        //7，最后一个参数是提供一个XML文件格式化工具，直接使用上下文的xmlFormatter即可（这个是可以在<context>元素中配置的哦~~）
        GeneratedXmlFile gxf = new GeneratedXmlFile(document, properties
                .getProperty("fileName", "MapperConfig.xml"), //$NON-NLS-1$ //$NON-NLS-2$
                properties.getProperty("targetPackage"), //$NON-NLS-1$
                properties.getProperty("targetProject"), //$NON-NLS-1$
                false, context.getXmlFormatter());

        //最后返回要生成的这个文件，交给MBG去生成；
        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>(1);
        answer.add(gxf);

        return answer;
    }
    //继续 重写mapper文件用法
}
