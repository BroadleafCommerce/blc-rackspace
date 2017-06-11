# Module Installation 
The Broadleaf Rackspace module requires [configuration](#configuration-changes) and [third-party property configuration](#third-party-property-configuration)

## Dependency Notes
The Rackspace module has a hard dependency on Google Guava because of [JClouds](http://jclouds.apache.org/). 
JClouds is *extremely* sensitive to the version of Guava as it uses it extensively. 
For this reason, Maven's dependency conflict resolution cannot be trusted to always pick the appropriate Guava version.

Let us know [on GitHub](https://github.com/broadleafcommerce/blc-rackspace) if you run into any problems with the hard Guava dependency. This should not have much, if any, impact on your project.

## Configuration Changes
**Step 0** Add the Broadleaf snapshots repository to your **parent** `pom.xml` if not already there:

```xml
<repository>
    <id>public snapshots</id>
    <name>public snapshots</name>
    <url>http://nexus.broadleafcommerce.org/nexus/content/repositories/snapshots/</url>
</repository>
```

**Step 1.**  Add the dependency management section to your **parent** `pom.xml`:
    
```xml
<dependency>
    <groupId>org.broadleafcommerce</groupId>
    <artifactId>broadleaf-rackspace</artifactId>
    <version>2.0.0-SNAPSHOT</version>
    <type>jar</type>
    <scope>compile</scope>
</dependency>
```

**Step 2.**  Add the dependency into your `core/pom.xml`:
    
```xml
<dependency>
    <groupId>org.broadleafcommerce</groupId>
    <artifactId>broadleaf-rackspace</artifactId>
</dependency>
```

## Third Party Property Configuration
This module requires you to configure properties specific to your Cloud Files account.   

### Credentials
Broadleaf requires access to your Rackspace Cloud Files account via their cloud API. See [Generating Your API Key](http://www.rackspace.com/knowledge_center/article/rackspace-cloud-essentials-1-generating-your-api-key) for more information on these two properties.  

Once you generate your API key, you will need to copy the values and add the following properties to your `common-shared.properties` file located in your core project.
 
```properties
broadleaf.rackspace.cloudfiles.provider=<provider>
broadleaf.rackspace.cloudfiles.username=<username>
broadleaf.rackspace.cloudfiles.apikey=<apikey>
broadleaf.rackspace.cloudfiles.region=<region>
broadleaf.rackspace.cloudfiles.container=<container>
broadleaf.rackspace.cloudfiles.container.subdirectory=<subdirectory>
```

### Storage Location Information 
Broadleaf also needs to know where to upload your files within your Rackspace Cloud Files account. The properties below indicate which container the files should be stored in.

```properties
# The name of the container to upload files to
broadleaf.rackspace.cloudfiles.container=
# Subdirectory within the container that the files should be accessible from
broadleaf.rackspace.cloudfiles.containerSubDirectory=
```

> If the referenced container does not already exist in your Cloud Files account, the container is automatically created for you

### CDN URLs
Cloud Files has additional functionality to CDN-enable your container with Akamai. Broadleaf can use this to rewrite asset URLs with the CDN url. This is as simple as viewing your containers, selecting the gear icon next to your container and hitting the 'Make Public' link:

![Enable CDN](enable-cdn-popup.png)

After you confirm, click the gear icon again and select 'View All Links':

![Enable CDN](cdn-view-links.png)

Using these URLs, within `common-shared.properties` populate the `asset.server.url.prefix` and `asset.server.url.prefix.secure`. For instance:

```properties
asset.server.url.prefix=http://62750dcbb924c39053af-80569172e6349bb1d95403cb08b0bcdf.r99.cf2.rackcdn.com
asset.server.url.prefix.secure=https://dc0493140a8ea8d605b0-80569172e6349bb1d95403cb08b0bcdf.ssl.cf2.rackcdn.com
```

> If you use Rackspace Akamai CDN in this fashion you will not be able to use the file operations from the [asset server](http://docs.broadleafcommerce.org/core/current/broadleaf-concepts/additional-configuration/asset-server-configuration). A way around this would be to use something [Cloud Front](http://aws.amazon.com/cloudfront/) or a separate Akamai account that allows you to set your Broadleaf server as the origin server. If you decide to do this then there is no reason to enable the Cloud Files CDN on your container.
