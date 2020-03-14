/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.apidoc.junit;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * 重写API文档抽象类.
 *
 * @author xujiuxing
 */
public abstract class AbstractApiDocRebuilder implements ApiDocRebuilder {

    /**
     * directory是否maven工程或模块.
     *
     * @param directory 工程目录
     * @return 是否maven工程
     */
    protected boolean isMavenProject(File directory) {
        File file = new File(directory, "pom.xml");
        return file.exists();
    }

    /**
     * directory是否maven根目录.
     *
     * @param directory 工程目录
     * @return 是否maven根工程
     */
    protected File getRootProject(File directory) {
        if (directory.isDirectory()) {
            if (isMavenProject(directory)) {
                if (isMavenProject(directory.getParentFile())) {
                    return getRootProject(directory.getParentFile());
                }
                return directory;
            }
            return directory;
        } else {
            return getRootProject(directory.getParentFile());
        }
    }

    /**
     * 遍历project工程src/docs目录下的接口文档写入JSON demo.
     *
     * @param project maven根目录或模块
     * @param apiReferrence 接口唯一标识
     * @param resultDemo API接口返回的Json字符串
     */
    protected void appendResultDemo(File project, String apiReferrence, String resultDemo) {
        final DocType docType = this.getDocType();
        String[] dirs = new String[]{"src", "docs", docType.name()};
        String path = StringUtils.arrayToDelimitedString(dirs, File.separator);
        File dir = new File(project, path);
        File[] docFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(docType.getExtension());
            }
        });

        if (docFiles != null) {
            for (File file : docFiles) {
                try {
                    rebuild(file, apiReferrence, resultDemo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        File[] subProjects = project.listFiles();
        for (File file : subProjects) {
            if (isMavenProject(file)) {
                appendResultDemo(file, apiReferrence, resultDemo);
            }
        }
    }

    /**
     * file接口文档写入JSON demo.
     *
     * @param file 生成的API接口文件
     * @param apiReferrence 接口唯一标识
     * @param resultDemo API接口返回的Json字符串
     * @throws IOException 异常
     */
    abstract void rebuild(File file, String apiReferrence, String resultDemo) throws IOException;
}
