package com.increff.pos.dto;

import com.increff.pos.model.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.increff.pos.util.ConversionUtil.convertToUserPojo;

@Component
public class SignUpDto {
    @Autowired
    private UserService userService;

    @Value("${admins}")
    private String admins;
    private Set<String> getAdmins(){
        Set<String > set = new HashSet<>();
        List<String> list = Arrays.asList(admins.split(","));
        for (String s: list){
            set.add(s);
        }
        return set;

    }
    public UserPojo addUser(UserForm form) throws ApiException {
        UserPojo user = convertToUserPojo(form);
        Set<String > set = getAdmins();
        if(set.contains(user.getEmail())){
            // TODO: 29/01/23 use enums for rolw
            user.setRole("supervisor");
        }
        else{
            user.setRole("operator");
        }
        return userService.addUser(user);
    }

}
