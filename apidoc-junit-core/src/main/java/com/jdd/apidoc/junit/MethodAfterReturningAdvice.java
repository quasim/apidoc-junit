/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.apidoc.junit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * 该拦截器会在调用接口方法后执行.
 *
 * @author xujiuxing
 */
public class MethodAfterReturningAdvice implements AfterReturningAdvice {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        String apiReferrence = method.getDeclaringClass().getName() + "#" + method.getName();
        if (returnValue != null) {
            DocType.rebuildAll(apiReferrence, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(returnValue));
        }
        String className = method.getDeclaringClass().getSimpleName();
        if (args != null && args.length > 0) {
            SnippetFactory.createRequestDemo(className, method.getName(), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(returnValue));
        }
        if (returnValue != null) {
            SnippetFactory.createResponseDemo(className, method.getName(), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(returnValue));
        }
    }

}
