package com.zaxk.study.mybatis;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * MySQL 分页生成插件。
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 10, 2012
 */
public final class MySQLPaginationPlugin extends PluginAdapter {

    public final String pageClass = new Page().getClass().getName();
    public final String enableRepository = "enableRepository";
    public final String enableCache = "enableCache";
    public final String repositoryType = "org.springframework.stereotype.Repository";
    public final String cacheableType = "org.springframework.cache.annotation.Cacheable";


    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {        // add field, getter, setter for limit clause
        addPage(topLevelClass, introspectedTable, "page");
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        XmlElement page = new XmlElement("if");
        page.addAttribute(new Attribute("test", "page != null"));
        page.addElement(new TextElement("limit #{page.begin} , #{page.length}"));
        element.addElement(page);
        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    /**
     * 根据generatorConfig.xml配置文件中table节点和javaClientGenerator节点的信息生成java mapper文件
     * table节点优先级高于java client节点
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {

        Properties javaClientProperties = context.getJavaClientGeneratorConfiguration().getProperties();
        Properties tableProperties = introspectedTable.getTableConfiguration().getProperties();


        String enableRepositoryValue = javaClientProperties.getProperty(enableRepository);

        if (isNotEmptyString(enableRepositoryValue) && enableRepositoryValue.equals("true")) {
            FullyQualifiedJavaType repository = new FullyQualifiedJavaType(repositoryType);
            interfaze.addImportedType(repository);
            interfaze.addAnnotation("@Repository");
        }

        String cacheValue = isNotEmptyString(tableProperties.getProperty(enableCache)) ? tableProperties.getProperty(enableCache) : javaClientProperties.getProperty(enableCache);

        if (cacheValue != null && cacheValue.length() > 0 && !cacheValue.equals("false")) {
            addCacheable(interfaze, cacheValue);
        }

        return true;
    }

    private void addCacheable(Interface interfaze, String cacheValue) {

        FullyQualifiedJavaType cacheable = new FullyQualifiedJavaType(cacheableType);

        Set<FullyQualifiedJavaType> importTypes = interfaze.getImportedTypes();
        if (importTypes.contains(cacheable)) {
            return;
        }

        interfaze.addImportedType(cacheable);
        StringBuilder sb = new StringBuilder();
        sb.append("@Cacheable(value = \"");
        sb.append(cacheValue);
        sb.append("\")");
        interfaze.addAnnotation(sb.toString());
    }

    public boolean isEmptyString(String str) {
        return str == null || str.trim().length() == 0;
    }

    public boolean isNotEmptyString(String str) {
        return !isEmptyString(str);
    }

    /**
     * @param topLevelClass
     * @param introspectedTable
     * @param name
     */
    private void addPage(TopLevelClass topLevelClass, IntrospectedTable introspectedTable,
                         String name) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType(pageClass));
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(new FullyQualifiedJavaType(pageClass));
        field.setName(name);
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
        char c = name.charAt(0);
        String camel = Character.toUpperCase(c) + name.substring(1);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + camel);
        method.addParameter(new Parameter(new FullyQualifiedJavaType(pageClass), name));
        method.addBodyLine("this." + name + "=" + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType(pageClass));
        method.setName("get" + camel);
        method.addBodyLine("return " + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    /**
     * This plugin is always valid - no properties are required
     */
    public boolean validate(List<String> warnings) {
        return true;
    }
}