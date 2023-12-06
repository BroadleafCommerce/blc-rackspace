/*-
 * #%L
 * BroadleafCommerce Rackspace CloudFiles
 * %%
 * Copyright (C) 2009 - 2022 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt).
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.vendor.rackspace.cloudfiles;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.file.FileServiceException;
import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.BroadleafFileService;
import org.broadleafcommerce.common.file.service.FileServiceProvider;
import org.broadleafcommerce.common.file.service.type.FileApplicationType;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.jclouds.ContextBuilder;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;
import org.springframework.stereotype.Service;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * {@link FileServiceProvider} implementation that deals with Rackspace Cloud Files
 */
@Service("blCloudFilesFileServiceProvider")
public class CloudFilesFileServiceProvider implements FileServiceProvider {

    @Resource(name = "blCloudFilesConfigurationService")
    protected CloudFilesConfigurationService cloudFilesConfigurationService;

    @Resource(name = "blFileService")
    protected BroadleafFileService fileService;

    @Override
    public File getResource(String name) {
        return getResource(name, FileApplicationType.ALL);
    }

    @Override
    public File getResource(String name, FileApplicationType fileApplicationType) {
        File returnFile = fileService.getLocalResource(buildResourceName(name));
        OutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            CloudFilesConfiguration cloudConfig = cloudFilesConfigurationService.lookupCloudFilesConfiguration();
            ObjectApi objectApi = getCloudFilesObjectApi(cloudConfig);

            SwiftObject object = objectApi.get(buildResourceName(name));

            if (object != null && object.getPayload() != null) {
                inputStream = object.getPayload().openStream();

                if (!returnFile.getParentFile().exists()) {
                    if (!returnFile.getParentFile().mkdirs()) {
                        // Other thread could have created - check one more time.
                        if (!returnFile.getParentFile().exists()) {
                            throw new RuntimeException("Unable to create parent directories for file: " + name);
                        }
                    }
                }
                outputStream = new FileOutputStream(returnFile);
                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Error writing CloudFiles file to local file system", ioe);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Error closing input stream while writing CloudFiles file to file system", e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Error closing output stream while writing CloudFiles file to file system", e);
                }

            }
        }
        return returnFile;
    }

    @Override
    public void addOrUpdateResources(FileWorkArea workArea, List<File> files, boolean removeFilesFromWorkArea) {
        addOrUpdateResourcesForPaths(workArea, files, removeFilesFromWorkArea);
    }

    @Override
    public List<String> addOrUpdateResourcesForPaths(FileWorkArea workArea, List<File> files, boolean removeFilesFromWorkArea) {
        CloudFilesConfiguration cloudConfig = cloudFilesConfigurationService.lookupCloudFilesConfiguration();
        ObjectApi objectApi = getCloudFilesObjectApi(cloudConfig);

        return addOrUpdateResourcesInternal(cloudConfig, objectApi, workArea, files, removeFilesFromWorkArea);
    }

    protected List<String> addOrUpdateResourcesInternal(CloudFilesConfiguration cloudConfig, ObjectApi objectApi, FileWorkArea workArea, List<File> files, boolean removeFilesFromWorkArea) {
        List<String> resourcePaths = new ArrayList<String>();
        for (File srcFile : files) {
            if (!srcFile.getAbsolutePath().startsWith(workArea.getFilePathLocation())) {
                throw new FileServiceException("Attempt to update file " + srcFile.getAbsolutePath() +
                        " that is not in the passed in WorkArea " + workArea.getFilePathLocation());
            }

            String fileName = srcFile.getAbsolutePath().substring(workArea.getFilePathLocation().length());
            String resourceName = buildResourceName(fileName);

            ByteSource byteSource = Files.asByteSource(srcFile);
            Payload filePayload = Payloads.newByteSourcePayload(byteSource);
            objectApi.put(resourceName, filePayload);
            resourcePaths.add(fileName);
        }

        return resourcePaths;
    }

    @Override
    public boolean removeResource(String name) {
        CloudFilesConfiguration cloudConfig = cloudFilesConfigurationService.lookupCloudFilesConfiguration();
        ObjectApi objectApi = getCloudFilesObjectApi(cloudConfig);

        objectApi.delete(buildResourceName(name));
        File returnFile = fileService.getLocalResource(buildResourceName(name));
        if (returnFile != null) {
            returnFile.delete();
        }
        return true;
    }

    /**
     * hook for overriding name used for resource in Cloud Files
     *
     * @param name
     * @return
     */
    protected String buildResourceName(String name) {
        // Strip the starting slash to prevent empty directories in CloudFiles as well as required references by // in the
        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        String baseDirectory = cloudFilesConfigurationService.lookupCloudFilesConfiguration().getContainerSubdirectory();
        if (StringUtils.isNotEmpty(baseDirectory)) {
            if (baseDirectory.startsWith("/")) {
                baseDirectory = baseDirectory.substring(1);
            }
        } else {
            // ensure subDirectory is non-null
            baseDirectory = "";
        }

        String siteSpecificResourceName = getSiteSpecificResourceName(name);
        return FilenameUtils.concat(baseDirectory, siteSpecificResourceName);
    }

    protected String getSiteSpecificResourceName(String resourceName) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            Site site = brc.getNonPersistentSite();
            if (site != null) {
                String siteDirectory = getSiteDirectory(site);
                if (resourceName.startsWith("/")) {
                    resourceName = resourceName.substring(1);
                }
                return FilenameUtils.concat(siteDirectory, resourceName);
            }
        }

        return resourceName;
    }

    protected String getSiteDirectory(Site site) {
        return "site-" + site.getId();
    }

    protected ObjectApi getCloudFilesObjectApi(CloudFilesConfiguration config) {
        CloudFilesApi cloudFilesApi = ContextBuilder.newBuilder(config.getProvider())
                .credentials(config.getUsername(), config.getApiKey())
                .buildApi(CloudFilesApi.class);
        return cloudFilesApi.getObjectApi(config.getRegion(), config.getContainer());
    }

    public void setCloudFilesConfigurationService(CloudFilesConfigurationService cloudFilesConfigurationService) {
        this.cloudFilesConfigurationService = cloudFilesConfigurationService;
    }

    public void setFileService(BroadleafFileService fileService) {
        this.fileService = fileService;
    }
}
