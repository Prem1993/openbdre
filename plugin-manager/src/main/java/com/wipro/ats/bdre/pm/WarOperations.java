package com.wipro.ats.bdre.pm;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by cloudera on 6/14/16.
 */
public class WarOperations {
    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);

    public void listOfFiles(File file,File parent,String md,String localizationFile) throws IOException{
        LOGGER.info("entering directory name : " + file.getName() + " The absolute location of dir is : " + file.getAbsolutePath());
        for(File file1 : file.listFiles()){
            if(file1.isDirectory()){
                String relativePath = file1.getAbsolutePath().replace(parent.getAbsolutePath() ,"");
                String webappPath = "";
                if ("mdui".equals(md)) {
                    webappPath = System.getProperty("user.home") + "/bdre/lib/webapps/mdui" + relativePath;
                }else{
                    webappPath = System.getProperty("user.home") + "/bdre/lib/webapps/mdrest" + relativePath;
                }
                if (!new File(webappPath).exists()){
                    new File(webappPath).mkdir();
                }
                if ("mdui".equals(md)) {
                    listOfFiles(file1, parent,"mdui",localizationFile);
                }else{
                    listOfFiles(file1, parent,"mdrest",localizationFile);
                }
            }else{
                String relativePath = file1.getAbsolutePath().replace(parent.getAbsolutePath() ,"");
                String webappPath = "";
                if ("mdui".equals(md)) {
                    webappPath = System.getProperty("user.home") + "/bdre/lib/webapps/mdui" + relativePath;
                }else{
                    webappPath = System.getProperty("user.home") + "/bdre/lib/webapps/mdrest" + relativePath;
                }
                if (new File(webappPath).exists() && ! Files.isSymbolicLink(new File(webappPath).toPath())){
                    if(relativePath.contains(localizationFile)){
                        try {
                            Path sourcePath = new File(file1.getAbsolutePath()).toPath();
                            byte[] data = Files.readAllBytes(sourcePath);
                            Path targetPath = new File(webappPath).toPath();
                            Files.write(targetPath, data, StandardOpenOption.APPEND);
                            Path authLocalizationFile = new File(webappPath.replaceAll("/mdui", "/auth")).toPath();
                            Files.write(authLocalizationFile, data, StandardOpenOption.APPEND);
                        }catch (IOException io) {
                            LOGGER.error(io + " : " + io.getMessage());
                            throw io;
                        }
                    }else{
                        continue;
                    }
                }else {
                    if(relativePath.contains(localizationFile)){
                        try {
                            Path sourcePath = new File(file1.getAbsolutePath()).toPath();
                            byte[] data = Files.readAllBytes(sourcePath);
                            String fileName = file1.getAbsolutePath().substring(0,file1.getAbsolutePath().lastIndexOf("."));
                            String language = fileName.substring(fileName.lastIndexOf("_"),fileName.length());
                            webappPath=webappPath.replaceAll("_[a-z][a-z][.]","_"+language + ".");
                            Path targetPath = new File(webappPath).toPath();
                            Files.write(targetPath, data, StandardOpenOption.APPEND);
                            Path authLocalizationFile = new File(webappPath.replaceAll("/mdui", "/auth")).toPath();
                            Files.write(authLocalizationFile, data, StandardOpenOption.APPEND);
                        }catch (IOException io) {
                            LOGGER.error(io + " : " + io.getMessage());
                            throw io;
                        }
                    }else {
                        Path targetPath = new File(webappPath).toPath();
                        if (new File(webappPath).exists()) {
                            new File(webappPath).delete();
                        }
                        Path sourcePath = new File(file1.getAbsolutePath()).toPath();
                        try {
                            Files.copy(sourcePath, targetPath);
                        } catch (IOException io) {
                            LOGGER.error(io + " : " + io.getMessage());
                            throw io;
                        }
                    }
                }
            }
        }
    }
}
