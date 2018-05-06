package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/4/13.
 */

@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest httpServletRequest , Shipping shipping){
        String cookieValue= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson= RedisShardedPoolUtil.get(cookieValue);
        User user= JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iShippingService.add(user.getId(),shipping);
    }

    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpServletRequest httpServletRequest ,Integer shippingId){
        String cookieValue= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson= RedisShardedPoolUtil.get(cookieValue);
        User user= JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iShippingService.del(user.getId(),shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest httpServletRequest ,Shipping shipping){
        String cookieValue= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson= RedisShardedPoolUtil.get(cookieValue);
        User user= JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iShippingService.update(user.getId(),shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpServletRequest httpServletRequest ,Integer shippingId){
        String cookieValue= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson= RedisShardedPoolUtil.get(cookieValue);
        User user= JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iShippingService.select(user.getId(),shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                         HttpServletRequest httpServletRequest){
        String cookieValue= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson= RedisShardedPoolUtil.get(cookieValue);
        User user= JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iShippingService.list(user.getId(),pageNum,pageSize);
    }
}
