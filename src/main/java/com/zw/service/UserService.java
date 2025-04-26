package com.zw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.dto.UserDTO;
import com.zw.dto.UserQueryDTO;
import com.zw.dto.UserRoleDTO;
import com.zw.dto.UserStatusDTO;
import com.zw.dto.UserUpdateDTO;
import com.zw.entity.User;
import com.zw.vo.UserInfoVO;

public interface UserService extends BaseService<User> {
    
    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
    
    /**
     * 注册用户
     */
    void register(User user);
    
    /**
     * 更新用户信息
     */
    void updateUserInfo(User user);
    
    /**
     * 修改密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    void resetPassword(Long userId);
    
    /**
     * 更新用户状态
     */
    void updateStatus(Long userId, Integer status);
    
    /**
     * 获取用户详细信息（包含角色和权限）
     */
    User getUserInfo(Long userId);
    
    UserInfoVO getUserInfo(String username);
    UserInfoVO updateUserInfo(String username, UserUpdateDTO userUpdateDTO);
    UserInfoVO updateUserStatus(Long userId, UserStatusDTO statusDTO);
    User findById(Long userId);
    
    // 管理员功能
    IPage<UserInfoVO> getUserList(UserQueryDTO queryDTO);
    UserInfoVO updateUserRole(Long userId, UserRoleDTO roleDTO);
    boolean deleteUser(Long userId);
    UserInfoVO createUser(UserDTO userDTO);
} 