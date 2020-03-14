/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.apidoc.junit.mockito;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jdd.apidoc.junit.SnippetFactory;
import org.mockito.internal.invocation.InvocationImpl;
import org.mockito.invocation.DescribedInvocation;
import org.mockito.listeners.InvocationListener;
import org.mockito.listeners.MethodInvocationReport;

import java.lang.reflect.Method;

/**
 * Mockito调用监听类.
 *
 * @author xujiuxing
 */
public class ApiDocInvocationListener implements InvocationListener {

    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setPrettyPrinting().create();

    @Override
    public void reportInvocation(MethodInvocationReport methodInvocationReport) {
        Object returnedValue = methodInvocationReport.getReturnedValue();
        DescribedInvocation invocation = methodInvocationReport.getInvocation();
        Method method = ((InvocationImpl) invocation).getMethod();
        String className = method.getDeclaringClass().getSimpleName();
        Object[] args = ((InvocationImpl) invocation).getArguments();
        if (args != null && args.length > 0) {
            SnippetFactory.createRequestDemo(className, method.getName(), gson.toJson(args));
        }
        if (returnedValue != null) {
            SnippetFactory.createResponseDemo(className, method.getName(), gson.toJson(returnedValue));
        }
    }

}
