package com.sancaijia.building.core.web;


import javax.servlet.http.HttpServletRequest;

/**
 *current thread 工具类
 * 2020年9月11日21:36:33
 * cc
 */
public class WebContext {
    private static final ThreadLocal<HttpServletRequest> CONTEXT_REQUEST = new ThreadLocal();
//    private static final ThreadLocal<Token> CONTEXT_TOKEN = new ThreadLocal();

    private WebContext() {
    }

    public static HttpServletRequest getRequest() {
        return (HttpServletRequest)CONTEXT_REQUEST.get();
    }

    public static void cacheRequest(HttpServletRequest request) {
        CONTEXT_REQUEST.set(request);
    }

    public static void clearRequest() {
        CONTEXT_REQUEST.remove();
    }

//    public static Token getToken() {
//        return (Token) CONTEXT_TOKEN.get();
//    }
//
//    public static void cacheToken(Token token) {
//        CONTEXT_TOKEN.set(token);
//    }

//    public static void cleanToken() {
//        CONTEXT_TOKEN.remove();
//    }



    public static void clear() {
        clearRequest();
//        cleanToken();
    }
}
