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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.BroadleafFileServiceExtensionManager;
import org.broadleafcommerce.common.file.service.BroadleafFileServiceImpl;
import org.broadleafcommerce.common.site.domain.SiteImpl;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class CloudFilesFileServiceProviderTest extends AbstractCloudFilesTest {

    public static final String CLOUDFILES_CONTAINER_SUBDIR_PROP = "broadleaf.rackspace.cloudfiles.container.subdirectory";
    private static CloudFilesFileServiceProvider cloudFilesProvider = new CloudFilesFileServiceProvider();

    private static final String TEST_FILE_CONTENTS = "abcdefghijklmnopqrstuvwxyz\n"
            + "01234567890112345678901234\n"
            + "!@#$%^&*()-=[]{};':',.<>/?\n"
            + "01234567890112345678901234\n"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public class CloudFilesBroadleafFileService extends BroadleafFileServiceImpl {
        public CloudFilesBroadleafFileService() {
            extensionManager = new BroadleafFileServiceExtensionManager();
        }
    }

    @BeforeClass
    public static void setupProvider() {
        cloudFilesProvider.setCloudFilesConfigurationService(configService);
        cloudFilesProvider.setFileService(new CloudFilesFileServiceProviderTest().new CloudFilesBroadleafFileService());
    }

    @Test
    public void testFileProcesses() throws IOException {
        resetAllProperties();
        String filename = "blcTestFile.txt";
        boolean ok = uploadTestFileTestOk(filename);
        assertTrue("File added to CloudFiles with no exception.", ok);

        ok = checkTestFileExists(filename);
        assertTrue("File retrieved from CloudFiles with no exception.", ok);

        ok = deleteTestFile(filename);

        // The file should not exist on CloudFiles
        ok = !checkTestFileExists(filename);
        assertTrue("File removed from CloudFiles with no exception.", ok);
    }

    @Test
    public void testSubDirectory() throws IOException {
        resetAllProperties();
        String filename = "blcTestFile.txt";
        String subDirectory = "img";
        verifyFileUploadRaw(filename, subDirectory);
    }

    @Test
    public void testSubDirectoryWithSlashes() throws IOException {
        resetAllProperties();
        String filename = "/blcTestFile.txt";
        String subDirectory = "/img/";
        verifyFileUploadRaw(filename, subDirectory);
    }

    @Test
    public void testSiteSpecificFile() throws IOException {
        // initialize the site before resetting properties to get the properties cache right
        BroadleafRequestContext context = new BroadleafRequestContext();
        SiteImpl site = new SiteImpl();
        site.setId(10l);
        site.setName("Test Site");
        context.setNonPersistentSite(site);
        BroadleafRequestContext.setBroadleafRequestContext(context);

        resetAllProperties();

        String filename = "/blcTestFile.txt";
        String subDirectory = "/img/";
        verifyFileUploadRaw(filename, subDirectory);

        BroadleafRequestContext.setBroadleafRequestContext(new BroadleafRequestContext());
    }

    @Test
    public void testRemoveAddedResourceByName() {
        resetAllProperties();
        String fileName = "blcTestFile.txt";
        propService.setProperty(CLOUDFILES_CONTAINER_SUBDIR_PROP, "/img/");
        List<String> resourceNames = uploadTestFileWithResult(fileName);
        assertTrue("No resource names return", CollectionUtils.isNotEmpty(resourceNames));
        assertTrue("More than 1 resource returned when only uploading a single resource", resourceNames.size() == 1);

        assertTrue(cloudFilesProvider.removeResource(resourceNames.get(0)));
    }

    @Test
    public void testNotFoundReturnsNonExistentFile() {
        resetAllProperties();
        File file = cloudFilesProvider.getResource("blahblahgarbledygoopcannotfind.ext");
        assertTrue("The returned file should not exist", !file.exists());
    }

    @Test
    public void testSubDirectoryTree() throws IOException {
        resetAllProperties();
        String filename = "/blcTestFile.txt";
        String subDirectory = "/img/sub1/sub2";
        verifyFileUploadRaw(filename, subDirectory);
    }

    /**
     * Differs from {@link #checkTestFileExists(String)} in that this uses the jClouds client directly and does
     * not go through the Broadleaf file service API. This will create a test file, upload it to CloudFiles via the
     * Broadleaf file service API, verify that the file exists via the raw jCloud client, and then delete the file
     * from the container via the file service API again
     *
     * @param filename the name of the file to upload
     * @param directoryName directory that the file should be stored in on CloudFiles
     */
    protected void verifyFileUploadRaw(String filename, String directoryName) throws IOException {
        propService.setProperty(CLOUDFILES_CONTAINER_SUBDIR_PROP, directoryName);

        boolean ok = uploadTestFileTestOk(filename);
        assertTrue("File added to CloudFiles with no exception.", ok);

        // Use the CloudFiles client directly to ensure that it was uploaded to the sub-directory
        CloudFilesConfiguration cloudFilesConfig = configService.lookupCloudFilesConfiguration();
        ObjectApi objectApi = cloudFilesProvider.getCloudFilesObjectApi(cloudFilesConfig);
        String objKey = cloudFilesProvider.getSiteSpecificResourceName(filename);
        if (StringUtils.isNotEmpty(directoryName)) {
            objKey = directoryName + "/" + objKey;
        }

        // Replace the starting slash and remove the double-slashes if the directory ended with a slash
        objKey = objKey.startsWith("/") ? objKey.substring(1) : objKey;
        objKey = FilenameUtils.normalize(objKey);

        SwiftObject object = objectApi.get(objKey);

        InputStream inputStream = object.getPayload().openStream();
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        String fileContents = writer.toString();
        inputStream.close();
        writer.close();

        assertEquals("Retrieved the file successfully from CloudFiles", fileContents, TEST_FILE_CONTENTS);

        ok = deleteTestFile(filename);
        assertTrue("File removed from CloudFiles with no exception.", ok);
    }

    protected boolean deleteTestFile(String filename) {
        boolean ok;
        try {
            cloudFilesProvider.removeResource(filename);
            ok = true;
        } catch (Exception e) {
            e.printStackTrace();
            ok = false;
        }
        return ok;
    }

    protected boolean checkTestFileExists(String filename) {
        boolean ok = false;
        try {
            File f = cloudFilesProvider.getResource(filename);
            if (f.exists()) {
                Scanner fileScanner = new Scanner(f);
                fileScanner.useDelimiter("\\Z");
                String content = fileScanner.next();
                int contentLength = content.length();
                if (contentLength > 10) {
                    System.out.println("Returned file contents: " + content);
                    ok = TEST_FILE_CONTENTS.equals(content);
                }
                fileScanner.close();
            }
        } catch (Exception e) {
            ok = false;
        }
        return ok;
    }

    protected boolean uploadTestFileTestOk(String filename) {
        boolean ok;
        try {
            uploadTestFileWithResult(filename);
            ok = true;
        } catch (Exception e) {
            e.printStackTrace();
            ok = false;
        }
        return ok;
    }

    protected List<String> uploadTestFileWithResult(String filename) {
        try {
            // Add the file to the amazon bucket.
            List<File> files = new ArrayList<File>();
            File sampleFile = createSampleFile(filename);
            FileWorkArea workArea = new FileWorkArea();
            File parentFile = sampleFile.getAbsoluteFile().getParentFile();
            workArea.setFilePathLocation(parentFile.getAbsolutePath());
            files.add(sampleFile);
            return cloudFilesProvider.addOrUpdateResourcesForPaths(workArea, files, false);
        } catch (IOException e) {
            return new ArrayList<String>();
        }
    }

    private static File createSampleFile(String fileName) throws IOException {
        File file = new File(fileName.startsWith("/") ? fileName.substring(1) : fileName);
        file.deleteOnExit();
        file.deleteOnExit();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write(TEST_FILE_CONTENTS);
        writer.close();
        return file;
    }

}
