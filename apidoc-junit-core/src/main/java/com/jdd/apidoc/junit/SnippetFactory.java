/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.apidoc.junit;

import java.io.*;

import static com.jdd.apidoc.junit.Constants.LINE_SEPARATOR;

/**
 * Api文档片断生成工厂.
 *
 * @author xujiuxing
 */
public final class SnippetFactory {

    private SnippetFactory() {
    }

    /**
     * 生成请求入参示例.
     *
     * @param className  类名
     * @param methodName 方法名
     * @param jsonDemo   json字符串
     */
    public static void createRequestDemo(String className, String methodName, String jsonDemo) {
        if (className == null || "".equals(className) || "Object".equals(className)) {
            return;
        }
        File file = new File(getDefaultOutputDirectory(), className + File.separator + methodName + File.separator + "request-demo.adoc");
        String content = getRequestText(jsonDemo);
        fileWrite(file, content);
    }

    /**
     * 生成返回结果示例.
     *
     * @param className  类名
     * @param methodName 方法名
     * @param jsonDemo   json字符串
     */
    public static void createResponseDemo(String className, String methodName, String jsonDemo) {
        if (className == null || "".equals(className) || "Object".equals(className)) {
            return;
        }
        File file = new File(getDefaultOutputDirectory(), className + File.separator + methodName + File.separator + "response-demo.adoc");
        String content = getResponseText(jsonDemo);
        fileWrite(file, content);
    }

    /**
     * 生成apidoc文档片断.
     *
     * @param file    snippet文件
     * @param content 内容
     */
    public static void fileWrite(File file, String content) {
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            write(file, content, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getDefaultOutputDirectory() {
        return new File("target/generated-snippets/jsf");
    }

    private static void write(File file, String content, boolean isAppend) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, isAppend), "UTF-8"));
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getRequestText(String json) {
        return getText("请求入参示例：", json);
    }

    private static String getResponseText(String json) {
        return getText("返回结果示例：", json);
    }

    private static String getText(String title, String json) {
        return new StringBuilder(title)
                .append(LINE_SEPARATOR)
                .append("[source,json]")
                .append(LINE_SEPARATOR)
                .append("----")
                .append(LINE_SEPARATOR)
                .append(json)
                .append(LINE_SEPARATOR)
                .append("----")
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR)
                .toString();
    }

}
