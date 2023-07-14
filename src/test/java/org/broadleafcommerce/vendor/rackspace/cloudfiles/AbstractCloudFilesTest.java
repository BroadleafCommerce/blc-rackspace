/*
 * #%L
 * BroadleafCommerce Rackspace CloudFiles
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.config.service.SystemPropertiesServiceImpl;
import org.junit.BeforeClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class AbstractCloudFilesTest {

    protected static TestSystemPropertiesService propService = new TestSystemPropertiesService();
    protected static CloudFilesConfigurationServiceImpl configService = new CloudFilesConfigurationServiceImpl();

    @BeforeClass
    public static void setup() {
        configService.setSystemPropertiesService(propService);
    }

    protected void resetAllProperties() {
        propService.setProperty("broadleaf.rackspace.cloudfiles.provider", findProperty("broadleaf.rackspace.cloudfiles.provider", "rackspace-cloudfiles-us"));
        propService.setProperty("broadleaf.rackspace.cloudfiles.username", findProperty("broadleaf.rackspace.cloudfiles.username", "broadleaf-test"));
        propService.setProperty("broadleaf.rackspace.cloudfiles.apikey", findProperty("broadleaf.rackspace.cloudfiles.apikey", "apiKey"));
        propService.setProperty("broadleaf.rackspace.cloudfiles.region", findProperty("broadleaf.rackspace.cloudfiles.region", "DFW"));
        propService.setProperty("broadleaf.rackspace.cloudfiles.container", findProperty("broadleaf.rackspace.cloudfiles.container", "containerName"));
        propService.setProperty("broadleaf.rackspace.cloudfiles.container.subdirectory", findProperty("broadleaf.rackspace.cloudfiles.container.subdirectory", ""));
    }

    public static class TestSystemPropertiesService extends SystemPropertiesServiceImpl {

        Map<String, String> mapCache = new HashMap<>();

        public void setProperty(String propertyName, String value) {
            mapCache.put(propertyName, value);
        }

        @Override
        public String resolveSystemProperty(String name) {
            return mapCache.get(name);
        }

    }

    protected String findProperty(String propertyName, String defaultValue) {
        String returnName = null;
        try {
            final Properties properties = new Properties();

            returnName = System.getProperty(propertyName);
            if (returnName == null) {
                InputStream path = this.getClass().getResourceAsStream("/config/bc/override/common.properties");
                if (path != null) {
                    properties.load(path);
                }
                returnName = properties.getProperty(propertyName);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (StringUtils.isEmpty(returnName)) {
            return defaultValue;
        }
        return returnName;
    }
}
