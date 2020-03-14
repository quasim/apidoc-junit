/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.apidoc.junit;

/**
 * 重写API文档接口.
 *
 * @author xujiuxing
 *
 */
public interface ApiDocRebuilder {

    /**
     * 文档类型.
     * @return DocType
     */
    DocType getDocType();

    /**
     * API接口返回的Json字符串写入asciidoc和markdown文档里.
     * @param apiReferrence 接口唯一标识
     * @param resultDemo API接口返回的Json字符串
     */
    void appendResultDemo(String apiReferrence, String resultDemo);

}
