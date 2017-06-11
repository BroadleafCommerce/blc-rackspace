Rackspace CloudFiles Integrations Module
==========================

This module contains Broadleaf integrations with Rackspace CloudFiles APIs.

## Steps to enable this module

1. Add the dependency management section to your **parent** `pom.xml`:
    ```xml
    <dependency>
        <groupId>org.broadleafcommerce</groupId>
        <artifactId>broadleaf-rackspace</artifactId>
        <version>2.0.0-GA</version>
        <type>jar</type>
        <scope>compile</scope>
    </dependency>
    ```

2. Pull this dependency into your `core/pom.xml`:
    ```xml
    <dependency>
        <groupId>org.broadleafcommerce</groupId>
        <artifactId>broadleaf-rackspace</artifactId>
    </dependency>
    ```
    
## Running/Building Locally

> Note: If you are a BLC developer, pull down the 'ThirdPartyIntegrationConfig' project and do a local maven install.

1. Fill in appropriate values within `src/main/resources/config/bc/rackspace/common.properties` but **do not check in**

```properties
broadleaf.rackspace.cloudfiles.provider=<provider>
broadleaf.rackspace.cloudfiles.username=<username>
broadleaf.rackspace.cloudfiles.apikey=<apikey>
broadleaf.rackspace.cloudfiles.region=<region>
broadleaf.rackspace.cloudfiles.container=<container>
broadleaf.rackspace.cloudfiles.container.subdirectory=<subdirectory>
```
