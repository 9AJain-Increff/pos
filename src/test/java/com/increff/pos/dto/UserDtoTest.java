//package com.increff.pos.dto;
//
//import com.increff.pos.exception.ApiException;
//import com.increff.pos.model.form.UserForm;
//import com.increff.pos.pojo.UserPojo;
//import com.increff.pos.service.UserService;
//import com.increff.pos.util.MockUtil;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class UserDtoTest {
//    @Autowired
//    private UserDto userDto;
//
//    @Autowired
//    private UserService userService;
//
//    @Rule
//    public ExpectedException exceptionRule = ExpectedException.none();
//
//    @Test
//    public void addSuccess() throws ApiException {
//        UserForm userForm = MockUtil.getMockUserForm();
//        UserPojo actual = userDto.add(userForm);
//        UserPojo expected = userService.getByEmail(actual.getEmail());
//        AssertUtils.assertEqualUsers(expected, actual);
//    }
//
//}
