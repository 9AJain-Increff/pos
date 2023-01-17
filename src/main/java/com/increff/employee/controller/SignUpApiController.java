package com.increff.employee.controller;

import com.increff.employee.dto.SignUpDto;
import com.increff.employee.model.InfoData;
import com.increff.employee.model.UserForm;
import com.increff.employee.pojo.UserPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Api
@RestController
public class SignUpApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private SignUpDto signUpDto;

    @ApiOperation(value = "Initializes application")
    @RequestMapping(path = "/site/signup", method = RequestMethod.GET)
    public void signUp(UserForm form) throws ApiException {
        signUpDto.addUser(form);
    }

}
