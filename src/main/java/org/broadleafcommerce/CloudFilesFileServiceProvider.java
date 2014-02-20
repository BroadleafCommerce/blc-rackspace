package org.broadleafcommerce;

import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.FileServiceProvider;
import org.broadleafcommerce.common.file.service.type.FileApplicationType;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.cloudfiles.CloudFilesApiMetadata;
import org.jclouds.cloudfiles.CloudFilesClient;
import org.jclouds.openstack.swift.domain.SwiftObject;

import java.io.File;
import java.util.List;


public class CloudFilesFileServiceProvider implements FileServiceProvider {

    @Override
    public File getResource(String name) {
        return getResource(name, FileApplicationType.ALL);
    }

    @Override
    public File getResource(String name, FileApplicationType fileApplicationType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addOrUpdateResources(FileWorkArea workArea, List<File> files, boolean removeFilesFromWorkArea) {
        BlobStoreContext ctx = ContextBuilder.newBuilder(new CloudFilesApiMetadata())
                .credentials("someusername", "someapikey")
                .buildView(BlobStoreContext.class);
        CloudFilesClient client = ContextBuilder.newBuilder("cloudfiles-us").buildApi(CloudFilesClient.class);
        SwiftObject obj = client.newSwiftObject();
        obj.getInfo().setName("somefile.txt");
        //obj.setpayload
        //BlobStore store = ctx.getBlobStore();
    }

    @Override
    public boolean removeResource(String name) {
        // TODO Auto-generated method stub
        return false;
    }

}
