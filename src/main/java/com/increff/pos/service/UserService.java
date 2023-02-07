package com.increff.pos.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import com.increff.pos.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.pojo.UserPojo;
import org.springframework.web.servlet.ModelAndView;

@Service
public class UserService {

    @Autowired
    private UserDao dao;

    @Transactional
    public UserPojo addUser(UserPojo p) throws ApiException {
        normalize(p);
        UserPojo existing = dao.selectByEmail(p.getEmail());
        if (existing != null) {
            throw new ApiException("User with given email already exists");
        }
        return dao.insert(p);
    }

    public UserPojo getUserByEmail(String email) throws ApiException {
        UserPojo user = dao.selectByEmail(email);
        if (user == null) {
            throw new ApiException("No user found with email");
        }
        return user;
    }
    public UserPojo checkUserByEmail(String email)  {
        UserPojo user = dao.selectByEmail(email);
        return user;
    }

    @Transactional
    public List<UserPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional
    public void delete(Integer id) {

        dao.delete(id);
    }

    protected static void normalize(UserPojo p) {
        p.setEmail(p.getEmail().toLowerCase().trim());
    }
}
