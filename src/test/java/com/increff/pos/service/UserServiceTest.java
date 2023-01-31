//package com.increff.pos.service;
//
//import com.increff.pos.exception.ApiException;
//import com.increff.pos.pojo.UserPojo;
//import com.increff.pos.util.MockUtil;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class UserServiceTest extends AbstractUnitTest{
//
//    @Autowired
//    private UserDao userDao;
//
//    @Autowired
//    private UserService userService;
//
//    @Rule
//    public ExpectedException exceptionRule = ExpectedException.none();
//
//    @Test
//    public void testAddUser() throws ApiException {
//        UserPojo user = MockUtil.getMockUser();
//        userService.addUser(user);
//        UserPojo actual = userDao.ge(user.getEmail());
//        AssertUtils.assertEqualUsers(user, actual);
//    }
//}
