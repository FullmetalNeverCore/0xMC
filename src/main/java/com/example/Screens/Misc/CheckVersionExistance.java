package com.mcl;

import java.io.File;

public class CheckVersionExistance {

    //checking if version is already exists in the folder.
    public static boolean isVersionExists(String version) {
        File  _folder = new File("minecraft");
        File _versionFolder = new File(_folder, String.format("versions/%s",version));
        

        if(_folder.exists() && _folder.isDirectory()){
           if(_versionFolder.exists() && _versionFolder.isDirectory()){
                System.out.println(String.format("DBUG: Version Exist - %s",_versionFolder.getAbsolutePath()));
                return true;
           }
        }
        return false;
    }
}
