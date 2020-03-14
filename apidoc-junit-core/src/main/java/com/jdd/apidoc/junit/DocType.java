/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.apidoc.junit;

/**
 * API文档类型枚举.
 *
 * @author xujiuxing
 *
 */
public enum DocType {

    markdown(".md", new MarkdownDocRebuilder()), asciidoc(".adoc", new AsciiDocRebuilder());

    private String extension;
    private ApiDocRebuilder rebuilder;

    private DocType(String extension, ApiDocRebuilder rebuilder) {
        this.extension = extension;
        this.rebuilder = rebuilder;
    }

    public String getExtension() {
        return extension;
    }

    public ApiDocRebuilder getRebuilder() {
        return rebuilder;
    }

    /**
     * API接口返回的Json字符串写入asciidoc和markdown文档里.
     * @param apiReferrence 接口唯一标识
     * @param resultDemo API接口返回的Json字符串
     */
    public static void rebuildAll(String apiReferrence, String resultDemo) {
        DocType[] values = DocType.values();
        for (DocType docType : values) {
            docType.getRebuilder().appendResultDemo(apiReferrence, resultDemo);
        }
    }

}
