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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class CloudFilesConfiguration {

    private String provider;
    private String username;
    private String apiKey;
    private String region;
    private String container;
    private String containerSubdirectory;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getContainerSubdirectory() {
        return containerSubdirectory;
    }

    public void setContainerSubdirectory(String containerSubdirectory) {
        this.containerSubdirectory = containerSubdirectory;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(provider)
                .append(username)
                .append(apiKey)
                .append(region)
                .append(container)
                .append(containerSubdirectory)
                .build();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CloudFilesConfiguration) {
            CloudFilesConfiguration that = (CloudFilesConfiguration) obj;
            return new EqualsBuilder()
                    .append(this.provider, that.provider)
                    .append(this.username, that.username)
                    .append(this.apiKey, that.apiKey)
                    .append(this.region, that.region)
                    .append(this.container, that.container)
                    .append(this.containerSubdirectory, that.containerSubdirectory)
                    .build();
        }
        return false;
    }

}
