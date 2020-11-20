package com.sancaijia.building.core.model.redis.cache;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JedisAgent implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisAgent.class);
    private JedisOwner delegate;
    private static final String GET_METHOD = "get";

    public JedisAgent() {
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.toLowerCase().startsWith("get")) {
            if (args.length < 1) {
                LOGGER.error("getJedis method must has [key] parameter.");
                return null;
            }

            String key = (String)args[0];
            if (StringUtils.isEmpty(key)) {
                LOGGER.error("get from redis, [key] is empty.");
                return null;
            }
        }

        Object result = method.invoke(this.delegate, args);
        this.delegate.freeResource();
        return result;
    }

    public Object bind(JedisOwner delegate) {
        LOGGER.debug("build proxy instance.");
        this.delegate = delegate;
        return Proxy.newProxyInstance(delegate.getClass().getClassLoader(), delegate.getClass().getInterfaces(), this);
    }
}
