package com.example.database.service;


import com.example.database.entity.User;
import com.example.database.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    public UserMapper mapper;

    public User getUser(String username){return mapper.getUser(username);}

    public int save(User user){return mapper.save(user);}
}
