/*
 * #%L
 * BroadleafCommerce Rackspace Integrations
 * %%
 * Copyright (C) 2014 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.vendor.rackspace.cloudfiles

/**
 * DTO for configuration relating to Cloud Files
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
class CloudFilesConfiguration {
    
    static final String USERNAME_PROP = 'broadleaf.rackspace.cloudfiles.username'
    static final String APIKEY_PROP = 'broadleaf.rackspace.cloudfiles.apikey'
    static final String ENDPOINT_PROP = 'broadleaf.rackspace.cloudfiles.endpoint'
    static final String CONTAINER_PROP = 'broadleaf.rackspace.cloudfiles.container'
    static final String CONTAINER_SUBDIR_PROP = 'broadleaf.rackspace.cloudfiles.containerSubDirectory'

    def username
    def apikey
    def endpoint
    def zone
    def container
    def containerSubDirectory
    
}
