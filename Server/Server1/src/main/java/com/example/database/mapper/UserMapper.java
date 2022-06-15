package com.example.database.mapper;

import com.example.database.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    /**
     * 新增用户
     * @param user
     * @return
     */
    int save(User user);


    /**
     * 根据用户名查询
     * @param username
     * @return
     */
    User getUser(String username);


}
