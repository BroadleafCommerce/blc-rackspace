/*
 * #%L
 * BroadleafCommerce Rackspace Integrations
 * %%
 * Copyright (C) 2014 - 2015 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.vendor.rackspace.cloudfiles;

import org.apache.commons.io.FilenameUtils
import org.broadleafcommerce.common.file.domain.FileWorkArea
import org.broadleafcommerce.common.file.service.BroadleafFileService
import org.broadleafcommerce.common.file.service.FileServiceProvider
import org.broadleafcommerce.common.file.service.type.FileApplicationType
import org.broadleafcommerce.common.util.BLCSystemProperty
import org.jclouds.ContextBuilder
import org.jclouds.cloudfiles.CloudFilesApiMetadata
import org.jclouds.cloudfiles.CloudFilesClient
import org.jclouds.openstack.swift.domain.SwiftObject
import org.springframework.stereotype.Service

import javax.annotation.Resource

/**
 * {@link FileServiceProvider} implementation that deals with Rackspace Cloud Files
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Service("blCloudFilesFileServiceProvider")
class CloudFilesFileServiceProvider implements FileServiceProvider {

    @Resource(name = "blFileService")
    protected BroadleafFileService fileService
    
    @Override
    File getResource(String name) {
        getResource(name, FileApplicationType.ALL)
    }

    @Override
    File getResource(String name, FileApplicationType fileApplicationType) {
        File localFile = fileService.getLocalResource(name);
        if (!localFile.parentFile.exists()) {
            if (!localFile.parentFile.mkdirs()) {
                // Other thread could have created - check one more time.
                if (!localFile.parentFile().exists()) {
                    throw new RuntimeException("Unable to create parent directories for file: " + name);
                }
            }
        }
        
        SwiftObject remoteFile = client.getObject(lookupConfiguration().container, buildResourceName(name), null)
        InputStream inStream = remoteFile.payload.openStream()
        OutputStream outStream = new FileOutputStream(localFile)
        
        // Pipe the remote file to the local file
        outStream << inStream
        
        outStream.close();
        inStream.close();
        
        localFile;
    }

    @Override
    void addOrUpdateResources(FileWorkArea workArea, List<File> files, boolean removeFilesFromWorkArea) {
        files.each { File file ->
            SwiftObject obj = client.newSwiftObject()
            obj.info.name = buildResourceName(FilenameUtils.getName(file.getAbsolutePath()))
            obj.setPayload(file)
            CloudFilesConfiguration conf = lookupConfiguration()
            if (!client.containerExists(conf.container)) {
                client.createContainer(conf.container)
            }
            client.putObject(conf.container, obj)
            client.close()
        }
    }

    @Override
    boolean removeResource(String name) {
        client.removeObject(lookupConfiguration().container, buildResourceName(name))
        true;
    }
    
    /**
     * Builds a {@link CloudFilesClient} based on configuration from {@link #lookupConfiguration()}
     * @return a {@link CloudFilesClient} based on configuration from {@link #lookupConfiguration()}
     */
    protected CloudFilesClient getClient() {
        CloudFilesConfiguration conf = lookupConfiguration()
        ContextBuilder.newBuilder(new CloudFilesApiMetadata())
            .credentials(conf.username, conf.apikey)
            .endpoint(conf.endpoint)
            .buildApi(CloudFilesClient)
    }
    
    /**
     * Responsible for sanitizing the given resource into something suitable to be placed in a Cloud Files container. This
     * will take off leading slashes and potentially prepend a {@link CloudFilesConfiguration#getContainerSubDirectory()}
     * 
     * @param name the file name that we should use
     * @return a path suitable to use in calls to a Cloud Files container
     */
    protected String buildResourceName(String name) {
        // Strip the starting slash to prevent empty directories in S3 as well as required references by // in the
        // public S3 URL
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        
        String subdir = lookupConfiguration().containerSubDirectory
        if (subdir) {
            if (subdir.startsWith("/")) {
                subdir = subdir.substring(1)
            }
            name = FilenameUtils.concat(subdir, name)
        }
    
        name
    }
    
    /**
     * Instantiates a new {@link CloudFilesConfiguration} based on system properties resolved from {@link BLCSystemProperty}
     * @return a new, populated {@link CloudFilesConfiguration} instance
     */
    protected CloudFilesConfiguration lookupConfiguration() {
       new CloudFilesConfiguration().with {
            username = BLCSystemProperty.systemPropertiesService.resolveSystemProperty(CloudFilesConfiguration.USERNAME_PROP)
            apikey = BLCSystemProperty.systemPropertiesService.resolveSystemProperty(CloudFilesConfiguration.APIKEY_PROP)
            container = BLCSystemProperty.systemPropertiesService.resolveSystemProperty(CloudFilesConfiguration.CONTAINER_PROP)
            endpoint = BLCSystemProperty.systemPropertiesService.resolveSystemProperty(CloudFilesConfiguration.ENDPOINT_PROP)
            containerSubDirectory = BLCSystemProperty.systemPropertiesService.resolveSystemProperty(CloudFilesConfiguration.CONTAINER_SUBDIR_PROP)
            return it
        }
    }
    
}
