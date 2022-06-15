package com.example.database.controller;

import com.example.database.entity.User;
import com.example.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/re_log")
public class UserController {

    @Autowired
    public UserService service;

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String register(@RequestParam String username,@RequestParam String password){
        System.out.println("用户名:" + username);
        System.out.println("密码:" + password);
        if(service.getUser(username) != null){
            return "用户已存在";
        }else {
            User user = new User(username,password);
            int result = service.save(user);
            if(result >= 1){
                return "添加成功";
            }else{
                return "添加失败";
            }
        }
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(@RequestParam String username,@RequestParam String password){
        if(service.getUser(username)!=null){
            String pwd = service.getUser(username).getPassword();
            if(pwd.equals(password)){
                System.out.println("用户名:" + username);
                System.out.println("密码:" + password);
                System.out.println("登录成功");
                return "登录成功";
            }else{
                return "密码错误";
            }
        }else{
            return "没有此用户";
        }
    }

}
