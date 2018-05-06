package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
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
 * Created by Administrator on 2018/5/4.
 */
@Controller
@RequestMapping("/user/session")
public class UserSpringSessionController {
    @Autowired
    private IUserService iUserService;

    /**
     * 用户的登录
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }

        return response;

    }


    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess("退出成功");
    }


    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session,HttpServletRequest httpServletRequest) {
        //User user=(User) session.getAttribute(Const.CURRENT_USER);
//        String cookieValue = CookieUtil.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(cookieValue)) {
//            return ServerResponse.createByErrorMessage("查询不到用户");
//        }
//        String userJson = RedisShardedPoolUtil.get(cookieValue);
//        User user = JsonUtil.String2Obj(userJson, new TypeReference<User>() {
//        });

        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("查询不到用户");
        }
        return ServerResponse.createBySuccess(user);
    }
}
