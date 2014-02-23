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
package org.broadleafcommerce.vendor.rackspace.cloudfiles;

import static org.junit.Assert.*
import static org.mockito.Mockito.*

import org.apache.commons.io.FilenameUtils
import org.broadleafcommerce.common.config.service.SystemPropertiesService
import org.broadleafcommerce.common.file.domain.FileWorkArea
import org.broadleafcommerce.common.file.service.BroadleafFileServiceImpl
import org.broadleafcommerce.common.util.BLCSystemProperty
import org.jclouds.ContextBuilder
import org.jclouds.cloudfiles.CloudFilesApiMetadata
import org.jclouds.cloudfiles.CloudFilesClient
import org.jclouds.openstack.swift.domain.SwiftObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


/**
 * This is being run with the PowerMockRunner so that we can mock the static BLCSystemProperty invocations. We can't do this
 * with Groovy's static metaClass because there are core Java invocations. The only way the metaClass will work is if all of the
 * invocations are in Groovy which they are not
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@RunWith(PowerMockRunner)
@PrepareForTest(BLCSystemProperty)
class CloudFilesTest {
    
    static List fileLines = ['abcdefghijklmnopqrstuvwxyz',
                              '01234567890112345678901234',
                              "!@#%^&*()-=[]{};':',.<>/?",
                              '01234567890112345678901234',
                              'ABCDEFGHIJKLMNOPQRSTUVWXYZ']
    
    static def filename = 'blcTestFile.txt'
    static def propMap = [:]
    
    static CloudFilesFileServiceProvider provider = new CloudFilesFileServiceProvider()
    
    static void resetAllProperties() {
        propMap[CloudFilesConfiguration.USERNAME_PROP] = findProperty('broadleaf-rackspace.cloudfiles.username', 'user')
        propMap[CloudFilesConfiguration.APIKEY_PROP] = findProperty('broadleaf-rackspace.cloudfiles.apikey', 'user')
        propMap[CloudFilesConfiguration.CONTAINER_PROP] = findProperty('broadleaf-rackspace.cloudfiles.container', 'user')
        propMap[CloudFilesConfiguration.ENDPOINT_PROP] = findProperty('broadleaf-rackspace.cloudfiles.endpoint', '')
        propMap[CloudFilesConfiguration.CONTAINER_SUBDIR_PROP] = findProperty('broadleaf-rackspace.cloudfiles.containerSubDirectory', '')
    }
    
    /**
     * Reset everything before every test
     */
    @Before
    void setup() {
        resetAllProperties()
        PowerMockito.mockStatic(BLCSystemProperty)
        SystemPropertiesService propService = PowerMockito.mock(SystemPropertiesService)
        when(propService.resolveSystemProperty(any())).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.arguments
                Object mock = invocation.mock
                propMap[args[0]]
            }
        })
        when(BLCSystemProperty.systemPropertiesService).thenReturn(propService)
        provider.fileService = new BroadleafFileServiceImpl()
    }
    
    @Test
    def void testBasicUpload() {
        uploadFile(filename)
        validateUpload(filename, true)
    }
    
    @Test
    def void testBasicGet() {
        uploadFile(filename)
        assertEquals(fileLines.join("\n") + "\n", provider.getResource(filename).text)
        provider.removeResource(filename)
    }
    
    @Test
    def void testBasicDelete() {
        uploadFile(filename)
        validateUpload(filename, false)
        provider.removeResource(filename)
    }
    
    @Test
    def void testSubdirectoryUpload() {
        String subdir = '/dir1/dir2'
        propMap[CloudFilesConfiguration.CONTAINER_SUBDIR_PROP] = subdir
        uploadFile(filename)
        validateUpload(FilenameUtils.concat(subdir.substring(1), filename), true)
    }
    
    @Test
    def void testAbsolutePaths() {
        uploadFile('/' + filename)
        validateUpload(filename, true)
    }
    
    def void validateUpload(String path, boolean cleanup) {
        CloudFilesConfiguration conf = provider.lookupConfiguration()
        CloudFilesClient client = ContextBuilder.newBuilder(new CloudFilesApiMetadata())
            .credentials(conf.username, conf.apikey)
            .endpoint(conf.endpoint)
            .buildApi(CloudFilesClient)
        SwiftObject obj = client.getObject(propMap[CloudFilesConfiguration.CONTAINER_PROP], path, null)
        InputStream instream = obj.payload.openStream()
        String text = instream.text
        instream.close()
        assertEquals(fileLines.join("\n") + "\n", text)
        
        if (cleanup) {
            client.removeObject(propMap[CloudFilesConfiguration.CONTAINER_PROP], path)
        }
    }
    
    def void uploadFile(String filename) {
        FileWorkArea workArea = new FileWorkArea()
        File sampleFile = createSampleFile(filename.startsWith('/') ? filename.substring(1) : filename)
        workArea.setFilePathLocation(sampleFile.getParent())
        provider.addOrUpdateResources(workArea, [sampleFile], false);
    }

    static String findProperty(String propertyName, String defaultValue) {
        Properties properties = new Properties();
        def res = System.getProperty(propertyName);
        if (!res) {
            CloudFilesFileServiceProvider.getResource("/config/bc/override/common.properties").withInputStream { is ->
                properties.load(is)
                res = properties.getProperty(propertyName)
            }
        }
        if (!res) {
            defaultValue
        }
        res
    }
    
    /**
     * Writes a temporary file with some data
     */
    def createSampleFile(def name) {
        File sample = new File(name)
        sample.withWriter { out ->
            fileLines.each { line ->
                out.writeLine(line)
            }
        }
        sample
    }

}
