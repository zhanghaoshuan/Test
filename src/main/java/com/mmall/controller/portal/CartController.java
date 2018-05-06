package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Cart;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.util.RedisShardedPoolUtil;
import com.mmall.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/4/12.
 */
@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpServletRequest httpServletRequest){
        String cookieValue= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson= RedisShardedPoolUtil.get(cookieValue);
        User user= JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iCartService.list(user.getId());
    }

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpServletRequest httpServletRequest, Integer count, Integer productId){
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson= RedisShardedPoolUtil.get(cookieValue);
        User user=JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        //购物车核心逻辑；
        return  iCartService.add(user.getId(),productId,count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpServletRequest httpServletRequest,Integer count,Integer productId){
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson=RedisShardedPoolUtil.get(cookieValue);
        User user=JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iCartService.update(user.getId(),productId,count);
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpServletRequest httpServletRequest,String productIds){
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson=RedisShardedPoolUtil.get(cookieValue);
        User user=JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public  ServerResponse<CartVo> selectAll(HttpServletRequest httpServletRequest){
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson=RedisShardedPoolUtil.get(cookieValue);
        User user=JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public  ServerResponse<CartVo> unSelectAll(HttpServletRequest httpServletRequest){
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson=RedisShardedPoolUtil.get(cookieValue);
        User user=JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.UN_CHECK);
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public  ServerResponse<CartVo> unSelect(HttpServletRequest httpServletRequest,Integer productId){
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson=RedisShardedPoolUtil.get(cookieValue);
        User user=JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UN_CHECK);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public  ServerResponse<CartVo> Select(HttpServletRequest httpServletRequest,Integer productId){
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson=RedisShardedPoolUtil.get(cookieValue);
        User user=JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest httpServletRequest){
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson=RedisShardedPoolUtil.get(cookieValue);
        User user=JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }

        return iCartService.getCartProductCount(user.getId());
    }
}
