package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.auth.UserRole;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
@Component
public class UserDto {

    @Autowired
    private UserService userService;
    public void add(UserForm form) throws ApiException {
        userService.addUser(convert(form));
    }
    public void delete(Integer id)  {
        userService.delete(id);
    }

    public List<UserPojo> getAll(){
        return userService.getAll();
    }
    public void addUser(UserPojo p) throws ApiException {
        userService.addUser(p);
    }

    public UserPojo checkUserExist(LoginForm form)  {

        return userService.checkUserByEmail(form.getEmail());
    }

    private static UserPojo convert(UserForm f) {
        UserPojo p = new UserPojo();
        p.setEmail(f.getEmail());
        p.setRole(UserRole.getRole(f.getRole()));
        p.setPassword(f.getPassword());
        return p;
    }
}
