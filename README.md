# mybatis-generator-plugin
mybatis generator分页插件，原作者：<a href="mailto:DL88250@gmail.com">Liang Ding</a>，针对项目实际情况对此插件新增了一些功能。

##新增功能
1. Java Mapper文件自动补充@Repository注解（可配置）

使用spring的MapperScannerConfigurer可自动扫描注册Mapper，但是由于未加注解，IDE会报错提示不能注入对象。

属性enableRepository设置为true即可开启此功能，配置如下：

    <javaClientGenerator targetPackage="com.talkweb.ylimaf.web.dao"
                         targetProject="src/main/java" type="XMLMAPPER">
        <property name="enableSubPackages" value="true"/>
        <property name="enableRepository" value="true"/>
    </javaClientGenerator>
        
生成的Mapper文件如下：

    import org.springframework.stereotype.Repository;
    
    @Repository
    public interface UserMapper {
    }

2. Java Mapper文件自动补充@Cacheable注解（可配置）

使用spring的缓存组件可在Mapper上加入@Cacheable注解实现缓存功能

可在javaClientGenerator报文节点加上属性enableCache，值需设置为cache的名字，注意此配置会将所有的Mapper都补充上@Cacheable注解，可以理解为默认或全局缓存配置，配置如下：

    <javaClientGenerator targetPackage="com.talkweb.ylimaf.web.dao" targetProject="src/main/java" type="XMLMAPPER">
        <property name="enableSubPackages" value="true"/>
        <property name="enableRepository" value="true"/>
        <property name="enableCache" value="eternalCache"/>
    </javaClientGenerator>

生成的Mapper文件如下：

    import org.springframework.cache.annotation.Cacheable;
    import org.springframework.stereotype.Repository;
    
    @Repository
    @Cacheable(value = "eternalCache")
    public interface DictItemMapper {
    }
    
在table报文节点中加上属性enableCache，值需设置为cache的名字，此配置可以覆盖javaClientGenerator报文节点中的默认全局配置，配置如下：

    <table tableName="USER" domainObjectName="User" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="true">
        <property name="enableCache" value="userCache"/>
    </table>
    
生成的Mapper文件如下：

    import org.springframework.cache.annotation.Cacheable;
    import org.springframework.stereotype.Repository;
    
    @Repository
    @Cacheable(value = "userCache")
    public interface UserMapper {
    }
    
另外，在table报文节点中将属性enableCache设置为false，可以关闭对应Mapper文件的自动补充功能，配置如下：

    <table tableName="RESOURCE" domainObjectName="Resource" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="true">
        <property name="enableCache" value="false"/>
    </table>
    
生成的Mapper文件如下：

    import org.springframework.stereotype.Repository;
    
    @Repository
    public interface ResourceMapper {
    }