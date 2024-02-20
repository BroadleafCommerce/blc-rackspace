/*-
 * #%L
 * BroadleafCommerce Rackspace CloudFiles
 * %%
 * Copyright (C) 2009 - 2024 Broadleaf Commerce
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

import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blCloudFilesConfigurationService")
public class CloudFilesConfigurationServiceImpl implements CloudFilesConfigurationService {

    @Resource(name = "blSystemPropertiesService")
    protected SystemPropertiesService systemPropertiesService;

    @Override
    public CloudFilesConfiguration lookupCloudFilesConfiguration() {
        CloudFilesConfiguration cloudConfig = new CloudFilesConfiguration();

        String provider = lookupProperty("broadleaf.rackspace.cloudfiles.provider");
        String username = lookupProperty("broadleaf.rackspace.cloudfiles.username");
        String apiKey = lookupProperty("broadleaf.rackspace.cloudfiles.apikey");
        String container = lookupProperty("broadleaf.rackspace.cloudfiles.container");
        String containerSubDir = lookupProperty("broadleaf.rackspace.cloudfiles.container.subdirectory");
        String region = lookupProperty("broadleaf.rackspace.cloudfiles.region");

        Assert.hasLength(provider, "CloudFiles provider can not be empty.");
        Assert.hasLength(username, "CloudFiles username can not be empty.");
        Assert.hasLength(apiKey, "CloudFiles apiKey can not be empty.");
        Assert.hasLength(container, "CloudFiles container can not be empty.");
        Assert.hasLength(region, "CloudFiles region can not be empty.");

        cloudConfig.setProvider(provider);
        cloudConfig.setUsername(username);
        cloudConfig.setApiKey(apiKey);
        cloudConfig.setContainer(container);
        cloudConfig.setContainerSubdirectory(containerSubDir);
        cloudConfig.setRegion(region);

        return cloudConfig;
    }

    protected String lookupProperty(String propertyName) {
        return systemPropertiesService.resolveSystemProperty(propertyName);
    }

    protected void setSystemPropertiesService(SystemPropertiesService systemPropertiesService) {
        this.systemPropertiesService = systemPropertiesService;
    }
}
