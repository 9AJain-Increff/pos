package com.increff.pos.controller;

import java.util.ArrayList;
import java.util.List;

import com.increff.pos.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.exception.ApiException;
import com.increff.pos.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class AdminApiController {

    @Autowired
    private UserDto userDto;

    @ApiOperation(value = "Adds a user")
    @RequestMapping(path = "/api/admin/user", method = RequestMethod.POST)
    public void addUser(@RequestBody UserForm form) throws ApiException {

        userDto.add(form);
    }

    @ApiOperation(value = "Deletes a user")
    @RequestMapping(path = "/api/admin/user/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable Integer id) {
        userDto.delete(id);
    }

    @ApiOperation(value = "Gets list of all users")
    @RequestMapping(path = "/api/admin/user", method = RequestMethod.GET)
    public List<UserData> getAllUser() {
        List<UserPojo> list = userDto.getAll();
        List<UserData> list2 = new ArrayList<UserData>();
        for (UserPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    private static UserData convert(UserPojo p) {
        UserData d = new UserData();
        d.setEmail(p.getEmail());
        d.setRole(p.getRole().toString());
        d.setId(p.getId());
        return d;
    }



}
