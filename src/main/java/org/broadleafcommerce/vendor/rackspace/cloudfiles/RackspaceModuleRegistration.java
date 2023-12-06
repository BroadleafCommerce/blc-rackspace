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

import org.broadleafcommerce.common.module.BroadleafModuleRegistration;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class RackspaceModuleRegistration implements BroadleafModuleRegistration {

    public static final String MODULE_NAME = "Rackspace";
    
    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

}
