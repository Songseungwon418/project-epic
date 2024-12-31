package com.ssw.epicgames.controllers;

import com.ssw.epicgames.entities.EmailTokenEntity;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView getLogout(HttpSession session) {
        session.setAttribute("user", null);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        //이미 로그인된 유저면
        if (session.getAttribute("user") != null) {
            //홈으로
            modelAndView.setViewName("redirect:/");
        } else {
            modelAndView.setViewName("/user/login");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getLogin(@RequestHeader(value = "x-request-by", required = false) String _requestBy,
                           HttpSession session, UserEntity user) {
        if (_requestBy == null || !_requestBy.equals("xmlhttprequest")) {
            throw new RuntimeException("fuck you");
        }

//        if(session.getAttribute("user") != null) {
//            return "이미 로그인 되어있습니다.";
//        }

        Result result = this.userService.login(user);
        if (result == CommonResult.SUCCESS) {
            session.setAttribute("user", user);
        }
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRegister() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/user/register");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String register(HttpServletRequest request, UserEntity user) throws MessagingException {
        Result result = this.userService.register(request, user);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/validate-email-token", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getValidateEmailToken(EmailTokenEntity emailToken) {
        Result result = this.userService.validateEmailToken(emailToken);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(Result.NAME, result.nameToLower());
        modelAndView.setViewName("/user/validateEmailToken");
        return modelAndView;
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getForgotPassword() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/user/forgotPassword");
        return modelAndView;
    }


    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String forgotPassword(HttpServletRequest request,
                                 @RequestParam(value = "email", required = false) String email) throws MessagingException {
        Result result = this.userService.provokeForgotPassword(request, email);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/recover-password", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRecoverPassword(@RequestParam(value = "userEmail", required = false) String userEmail,
                                           @RequestParam(value = "key", required = false) String key) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("userEmail", userEmail);
        modelAndView.addObject("key", key);
        modelAndView.setViewName("/user/recoverPassword");
        return modelAndView;
    }

    @RequestMapping(value = "/recover-password", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchRecoverPassword(EmailTokenEntity emailToken,
                                       @RequestParam(value = "password", required = false) String password) {
        Result result = this.userService.resolveRecoverPassword(emailToken, password);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }
}
