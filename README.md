# RabbitMQ Redis

```xml
<!-- Only needed to run tests in a version of IntelliJ IDEA that bundles older versions -->
<!-- module创建的spring boot中，测试无法使用，要在pom.xml中加入如下依赖 -->
<!--https://junit.org/junit5/docs/current/user-guide/#running-tests-ide-intellij-idea-->
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-launcher</artifactId>

            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>

            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>

            <scope>test</scope>
        </dependency>
```