/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.apidoc.junit;

import com.jdd.apidoc.junit.javassist.util.HotSwapAgent;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.mockito.Mockito;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.listeners.InvocationListener;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * spring测试类注入监听器。主要作用是拦截接口被调用时返回的结果。有两类接口调用：
 * 1.调用接口真实实现类（spring注入），该类接口被注入时，重新建构代理类，并织入切面，由切面类将拦截结果写入ApiDoc文档，
 * 2.调用接口Mock类（暂时只支持Mockito），使用javassist改写Mockito.class和MockSettingsImpl.class，
 * 将每个Mock类设置调用监听器ApiDocInvocationListener.class，监听器拦截Mock类返回的结果，并将结果写入ApiDoc文档。
 *
 * @author xujiuxing
 */
public class ApiDocDependencyInjectionTestExecutionListener extends DependencyInjectionTestExecutionListener {

    private final MethodAfterReturningAdvice methodAfterReturningAdvice;

    public ApiDocDependencyInjectionTestExecutionListener() {
        methodAfterReturningAdvice = new MethodAfterReturningAdvice();
    }

    @Override
    protected void injectDependencies(TestContext testContext) throws Exception {
        super.injectDependencies(testContext);
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) testContext.getApplicationContext();
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) testContext.getApplicationContext();
        Object bean = testContext.getTestInstance();
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            Map<String, ?> beans = applicationContext.getBeansOfType(field.getType());
            for (String beanName : beans.keySet()) {
                Object realObject = applicationContext.getBean(beanName);
                if (null != realObject && beanDefinitionRegistry.containsBeanDefinition(beanName)) {
                    ProxyFactory proxyFactory = new ProxyFactory(realObject);
                    proxyFactory.addAdvice(methodAfterReturningAdvice);
                    Object proxyInstance = proxyFactory.getProxy();
                    beanDefinitionRegistry.removeBeanDefinition(beanName);
                    applicationContext.getBeanFactory().registerSingleton(beanName, proxyInstance);
                    ReflectionTestUtils.setField(bean, field.getName(), proxyInstance);
                }
            }
        }
        rebuildMock();
    }

    /**
     * 使用javassist改写Mockito.class和MockSettingsImpl.class，将每个Mock类设置调用监听器ApiDocInvocationListener.class.
     *
     * @throws Exception 异常
     */
    private void rebuildMock() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass1 = pool.get(Mockito.class.getName());
        CtClass ctClass2 = pool.get(MockSettingsImpl.class.getName());
        if (ctClass1.isFrozen() || ctClass2.isFrozen()) {
            return;
        }
        CtMethod ctMethod1 = ctClass1.getDeclaredMethod("withSettings");
        ctMethod1.setBody("{ return new org.mockito.internal.creation.MockSettingsImpl().defaultAnswer(RETURNS_DEFAULTS).invocationListeners(null); }");

        CtMethod ctMethod2 = ctClass2.getDeclaredMethod("addListeners");
        ctMethod2.addLocalVariable("_listener", pool.get(InvocationListener.class.getName()));

        String code = " if ($1 == null) { "
                + " _listener = new com.jd.apidoc.junit.mockito2.ApiDocInvocationListener(); "
                + " $1 = new org.mockito.listeners.InvocationListener[] { _listener }; "
                + " }";
        ctMethod2.insertBefore(code);

        HotSwapAgent.redefine(new Class[]{Mockito.class, MockSettingsImpl.class}, new CtClass[]{ctClass1, ctClass2});
    }
}
