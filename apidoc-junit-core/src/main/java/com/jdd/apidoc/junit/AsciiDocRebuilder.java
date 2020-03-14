/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.apidoc.junit;

import static com.jdd.apidoc.junit.Constants.LINE_SEPARATOR;

import java.io.*;

/**
 * 测试类中调用接口返回的结果，插入到asciidoc文档中.
 *
 * @author xujiuxing
 */
public class AsciiDocRebuilder extends AbstractApiDocRebuilder {

    @Override
    public DocType getDocType() {
        return DocType.asciidoc;
    }

    @Override
    public void appendResultDemo(String apiReferrence, String resultDemo) {
        try {
            File root = getRootProject(new File("").getCanonicalFile());
            if (isMavenProject(root)) {
                appendResultDemo(root, apiReferrence, resultDemo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    void rebuild(File file, String apiReferrence, String resultDemo) throws IOException {
        if (!file.exists() || (System.currentTimeMillis() - file.lastModified()) > 600 * 1000) {
            return;
        }

        int pos = apiReferrence.indexOf('#');
        String methodName = apiReferrence.substring(pos + 1);

        InputStream input = new FileInputStream(file);

        BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        File newFile = new File(file.getParentFile(), methodName + getDocType().getExtension());
        BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), "UTF-8"));
        String line = null;
        String currentApiReferrenceLine = null;
        StringBuffer sb = new StringBuffer();
        while ((line = br.readLine()) != null) {
            if (line.startsWith("- 接口名") && line.endsWith(apiReferrence)) {
                currentApiReferrenceLine = line;
            } else if (line.startsWith("== ") || line.startsWith("=== ")) {
                if (currentApiReferrenceLine != null && currentApiReferrenceLine.endsWith(apiReferrence)) {
                    appendJsonResultDemo(sb, resultDemo);
                    currentApiReferrenceLine = null;
                }
            } else if ("[source,json]".equals(line)) {
                currentApiReferrenceLine = null;
            }
            sb.append(line);
            sb.append(LINE_SEPARATOR);
        }

        if (currentApiReferrenceLine != null && currentApiReferrenceLine.endsWith(apiReferrence)) {
            appendJsonResultDemo(sb, resultDemo);
        }

        bos.write(sb.toString());

        bos.close();
        br.close();
        input.close();
        file.delete();
        newFile.renameTo(file);
    }

    private void appendJsonResultDemo(StringBuffer sb, String resultDemo) {
        sb.append("==== 返回结果示例：");
        sb.append(LINE_SEPARATOR);
        sb.append("[source,json]");
        sb.append(LINE_SEPARATOR);
        sb.append("----");
        sb.append(LINE_SEPARATOR);
        sb.append(resultDemo);
        sb.append(LINE_SEPARATOR);
        sb.append("----");
        sb.append(LINE_SEPARATOR);
        sb.append(LINE_SEPARATOR);
    }

}
