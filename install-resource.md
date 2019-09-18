#通过安装可以任意指定名字
mvn install:install-file -Dfile=E:\myworkspace\resource\target\rs-resource\resource-1.0.10.jar -DgroupId=com.ajie -DartifactId=rs-resource -Dversion=1.0.10 -Dpackaging=jar
#复制源码
copy /Y E:\myworkspace\resource\target\rs-resource\resource-1.0.10-sources.jar D:\maven\repository\com\ajie\rs-resource\1.0.10\rs-resource-1.0.10-sources.jar

#因为最终输出的jar包名为rs-resourc（build包插件输出的jar包一定要带上项目名的作为前缀），所以需要执行上面的操作
