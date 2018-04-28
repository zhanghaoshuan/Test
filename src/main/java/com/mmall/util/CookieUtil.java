package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2018/4/27.
 */
public class CookieUtil {
    private final static String COOKIE_DOMAIN=".fuzai.com";
    private final static String COOKIE_NAME="mmall_login_token";
    private final static Logger logger= LoggerFactory.getLogger(CookieUtil.class);

    public static void writeloginToken(HttpServletResponse response,String token){
        Cookie ck=new Cookie(COOKIE_NAME,token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setPath("/");//代表设置在根目录
        ck.setHttpOnly(true);//防止脚本攻击带来的信息泄露风险  可以让浏览器不把cookie发送给第三方，
        ck.setMaxAge(-1);//如果是-1代表没有期限 ，cookie永久有效
        logger.info("write cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
        response.addCookie(ck);
    }

    public static  String readLoginToken(HttpServletRequest request){
        Cookie[] cks=request.getCookies();
        if(cks!=null){
            for(Cookie ck:cks){
                logger.info("read cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    logger.info("return cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    public static  void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cks=request.getCookies();
        if(cks!=null){
            for(Cookie ck:cks){
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){

                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");//代表设置在根目录

                    ck.setMaxAge(0);//如果是-1代表没有期限 ，cookie永久有效
                    logger.info("return cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                    return ;
                }
            }
        }
        return;
    }
}
