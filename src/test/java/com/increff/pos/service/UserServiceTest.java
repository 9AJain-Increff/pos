package com.increff.pos.service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.auth.UserRole;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.util.AssertUtil;
import com.increff.pos.util.MockUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class UserServiceTest extends AbstractUnitTest{

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testAddUser() throws ApiException {
        UserPojo user = MockUtil.getMockUser();
        userService.addUser(user);
        UserPojo actual = userDao.selectByEmail(user.getEmail());
        AssertUtil.assertEqualUsers(user, actual);
    }
    //TODO ... check for enum in role
//    @Test
//    public void testAddAdminUser() throws ApiException {
//        UserPojo user = MockUtil.getMockUser();
//        user.setEmail("admin@test.com");
//        userService.addUser(user);
//        UserPojo actual = userDao.selectByEmail(user.getEmail());
//        AssertUtil.assertEqualUsers(user, actual);
//        Assert.assertEquals(actual.getRole(), UserRole.SUPERVISOR);
//    }


    @Test
    public void testSelectByEmailForInvalidEmail() throws ApiException {
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("No user found with email");
        userService.getUserByEmail("invalid@email.com");
    }

    @Test
    public void testSelectByEmail() throws ApiException {
        UserPojo mockUser = MockUtil.getMockUser();
        userDao.insert(mockUser);
        UserPojo actual = userService.getUserByEmail(mockUser.getEmail());
        AssertUtil.assertEqualUsers(mockUser, actual);
    }
    @Test
    public void addingDuplicateUserThrowsApiException() throws ApiException {
        UserPojo original = MockUtil.getMockUser();
        userDao.insert(original);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("User with given email already exists");
        UserPojo duplicate = MockUtil.getMockUser();
        userService.addUser(duplicate);
    }

//    @Test
//    public void testGetAllUsers() {
//        List<UserPojo> expectedUsers = Arrays.asList(
//                new UserPojo(null, "email1@mail.com", "Pass1", UserRole.OPERATOR),
//                new UserPojo(null, "email2@mail.com", "Pass2", UserRole.SUPERVISOR),
//                new UserPojo(null, "email3@mail.com", "Pass2", UserRole.OPERATOR)
//        );
//        expectedUsers.forEach(userDao::insert);
//
//        List<User> actualUsers = userService.getAll();
//        AssertUtils.assertEqualList(expectedUsers, actualUsers, AssertUtils::assertEqualUsers);
//    }

    @Test
    public void deleteUserWithValidId() throws ApiException {
        UserPojo mockUser = MockUtil.getMockUser();
        userDao.insert(mockUser);
        userService.delete(mockUser.getId());

        boolean userStillExists = userDao
                .selectAll()
                .stream()
                .anyMatch(user -> user.getId().equals(mockUser.getId()));

        Assert.assertFalse(userStillExists);
    }

//    @Test
//    public void deleteUserWithInvalidIdThrowsException() throws ApiException {
//        exceptionRule.expect(ApiException.class);
//        int invalidId = -1;
//        exceptionRule.expectMessage("No user found with ID: " + invalidId);
//        userService.delete(invalidId);
//    }

}
