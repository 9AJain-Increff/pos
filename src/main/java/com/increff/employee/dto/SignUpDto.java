package com.increff.employee.dto;

import com.increff.employee.model.UserForm;
import com.increff.employee.pojo.UserPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.increff.employee.util.ConversionUtil.convertToUserPojo;

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
    public void addUser(UserForm form) throws ApiException {
        UserPojo user = convertToUserPojo(form);
        Set<String > set = getAdmins();
        if(set.contains(user.getEmail())){
            user.setRole("supervisor");
        }
        else{
            user.setRole("operator");
        }
        userService.addUser(user);
    }

}
