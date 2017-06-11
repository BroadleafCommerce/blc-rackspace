/*
 * #%L
 * BroadleafCommerce Rackspace CloudFiles
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class CloudFilesConfigurationServiceTest extends AbstractCloudFilesTest {

    @Test
    public void checkForAllPropertiesSet() {
        resetAllProperties();
        boolean ok;
        try {
            configService.lookupCloudFilesConfiguration();
            ok = true;
        } catch(IllegalArgumentException iae) {
            ok = false;
        }
        assertTrue("No exception thrown", ok);
    }

    @Test
    public void checkForMissingProvider() {
        resetAllProperties();
        propService.setProperty("broadleaf.rackspace.cloudfiles.provider", "");
        boolean ok;
        try {
            configService.lookupCloudFilesConfiguration();
            ok = true;
        } catch (IllegalArgumentException iae) {
            ok = false;
        }
        assertTrue("Expected to get an exception.", !ok);
    }

    @Test
    public void checkForMissingUsername() {
        resetAllProperties();
        propService.setProperty("broadleaf.rackspace.cloudfiles.username", "");
        boolean ok;
        try {
            configService.lookupCloudFilesConfiguration();
            ok = true;
        } catch (IllegalArgumentException iae) {
            ok = false;
        }
        assertTrue("Expected to get an exception.", !ok);
    }

    @Test
    public void checkForMissingApiKey() {
        resetAllProperties();
        propService.setProperty("broadleaf.rackspace.cloudfiles.apikey", "");
        boolean ok;
        try {
            configService.lookupCloudFilesConfiguration();
            ok = true;
        } catch (IllegalArgumentException iae) {
            ok = false;
        }
        assertTrue("Expected to get an exception.", !ok);
    }

    @Test
    public void checkForMissingContainer() {
        resetAllProperties();
        propService.setProperty("broadleaf.rackspace.cloudfiles.container", "");
        boolean ok;
        try {
            configService.lookupCloudFilesConfiguration();
            ok = true;
        } catch (IllegalArgumentException iae) {
            ok = false;
        }
        assertTrue("Expected to get an exception.", !ok);
    }

    @Test
    public void checkForMissingRegion() {
        resetAllProperties();
        propService.setProperty("broadleaf.rackspace.cloudfiles.region", "");
        boolean ok;
        try {
            configService.lookupCloudFilesConfiguration();
            ok = true;
        } catch (IllegalArgumentException iae) {
            ok = false;
        }
        assertTrue("Expected to get an exception.", !ok);
    }



}
