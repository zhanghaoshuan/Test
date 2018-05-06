package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.RedisPool;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;

import com.mmall.pojo.User;
import com.mmall.service.IUserService;

import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/3/30.
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;
    /**
     * 用户的登录
     * @param  username
     * @param  password
     * @param  session
     * @return
     */
    @RequestMapping(value="login.do",method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
        ServerResponse<User> response=iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        CookieUtil.writeloginToken(httpServletResponse,session.getId());
//        CookieUtil.readLoginToken(httpServletRequest);
//        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),3600);
        return  response;

    }


    @RequestMapping(value="logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        RedisShardedPoolUtil.del(cookieValue);
        return ServerResponse.createBySuccess("退出成功");
    }

    @RequestMapping(value="register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value="check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value="get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest){
        //User user=(User) session.getAttribute(Const.CURRENT_USER);
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson=RedisShardedPoolUtil.get(cookieValue);
        User user=JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        return ServerResponse.createBySuccess(user);
    }

    @RequestMapping(value="forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value="forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value="forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    @RequestMapping(value="reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest httpServletRequest,String passwordOld,String passwordNew){
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson=RedisShardedPoolUtil.get(cookieValue);
        User user=JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    @RequestMapping(value="update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpServletRequest httpServletRequest,User user){
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson=RedisShardedPoolUtil.get(cookieValue);
        User currentUser=JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }

        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response= iUserService.updateInformation(user);
        if(response.isSuccess()){
            String userJsonStr=JsonUtil.obj2String(response.getData());
            RedisShardedPoolUtil.setEx(cookieValue,userJsonStr,3600);
//            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value="get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpServletRequest httpServletRequest){
        String cookieValue=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(cookieValue)){
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        String userJson= RedisShardedPoolUtil.get(cookieValue);
        User currentUser=JsonUtil.String2Obj(userJson, new TypeReference<User>() {});
        if(currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }

        return iUserService.getInformation(currentUser.getId());
    }
}