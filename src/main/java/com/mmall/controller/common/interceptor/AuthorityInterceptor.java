package com.mmall.controller.common.interceptor;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/6.
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService iUserService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        log.info("preHandle");
        HandlerMethod handlerMethod=(HandlerMethod) o;
        String methodName=handlerMethod.getBean().getClass().getName();
        String className=handlerMethod.getBean().getClass().getSimpleName();

        StringBuffer requestParamBuffer=new StringBuffer();
        Map paramaMap= httpServletRequest.getParameterMap();
        Iterator it=paramaMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry entry=(Map.Entry)it.next();
            String mapKey=(String)entry.getKey();
            String mapValue= StringUtils.EMPTY;
            Object obj=entry.getValue();
            if(obj instanceof String[]){
                String[] strs=(String[])obj;
                mapValue= Arrays.toString(strs);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }

        User user=null;
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isNotEmpty(loginToken)){
            String userStrJson= RedisPoolUtil.get(loginToken);
            user= JsonUtil.string2Obj(userStrJson,User.class);
        }
        if(user==null||(user.getRole().intValue()!=Const.Role.ROLE_ADMIN)){
            //ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            httpServletResponse.reset();
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            PrintWriter out=httpServletResponse.getWriter();

            if(user==null){
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截，用户未登录")));
            }else{
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截，用户无权限")));
            }
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
