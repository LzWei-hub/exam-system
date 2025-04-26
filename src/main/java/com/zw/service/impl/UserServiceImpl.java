package com.zw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zw.dto.UserDTO;
import com.zw.dto.UserQueryDTO;
import com.zw.dto.UserRoleDTO;
import com.zw.dto.UserStatusDTO;
import com.zw.dto.UserUpdateDTO;
import com.zw.entity.User;
import com.zw.exception.BusinessException;
import com.zw.mapper.UserMapper;
import com.zw.service.PermissionService;
import com.zw.service.RoleService;
import com.zw.service.UserService;
import com.zw.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    public UserServiceImpl(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User register(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (getByUsername(userDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRealName(userDTO.getRealName());
        user.setRole(userDTO.getRole());
        user.setEmail(userDTO.getEmail());
        user.setStatus(1); // 默认启用状态

        // 保存用户
        save(user);
        return user;
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }
    
    @Override
    public User findById(Long userId) {
        return getById(userId);
    }

    @Override
    public UserInfoVO getUserInfo(String username) {
        User user = getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }
    
    @Override
    @Transactional
    public UserInfoVO updateUserInfo(String username, UserUpdateDTO userUpdateDTO) {
        User user = getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 只更新不为null的字段
        if (userUpdateDTO.getRealName() != null) {
            user.setRealName(userUpdateDTO.getRealName());
        }
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getAvatar() != null) {
            user.setAvatar(userUpdateDTO.getAvatar());
        }
        
        // 更新用户信息
        updateById(user);
        
        // 返回更新后的用户信息
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }
    
    @Override
    @Transactional
    public UserInfoVO updateUserStatus(Long userId, UserStatusDTO statusDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新用户状态
        user.setStatus(statusDTO.getStatus());
        updateById(user);
        
        // 返回更新后的用户信息
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }
    
    @Override
    public IPage<UserInfoVO> getUserList(UserQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like(User::getUsername, queryDTO.getUsername());
        }
        
        if (StringUtils.hasText(queryDTO.getRealName())) {
            wrapper.like(User::getRealName, queryDTO.getRealName());
        }
        
        if (StringUtils.hasText(queryDTO.getRole())) {
            wrapper.eq(User::getRole, queryDTO.getRole());
        }
        
        if (queryDTO.getStatus() != null) {
            wrapper.eq(User::getStatus, queryDTO.getStatus());
        }
        
        // 创建分页对象
        Page<User> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 执行分页查询
        Page<User> userPage = page(page, wrapper);
        
        // 转换结果
        Page<UserInfoVO> resultPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        
        List<UserInfoVO> userInfoList = userPage.getRecords().stream().map(user -> {
            UserInfoVO userInfoVO = new UserInfoVO();
            BeanUtils.copyProperties(user, userInfoVO);
            return userInfoVO;
        }).collect(Collectors.toList());
        
        resultPage.setRecords(userInfoList);
        
        return resultPage;
    }
    
    @Override
    @Transactional
    public UserInfoVO updateUserRole(Long userId, UserRoleDTO roleDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新用户角色
        user.setRole(roleDTO.getRole());
        updateById(user);
        
        // 返回更新后的用户信息
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }
    
    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        return removeById(userId);
    }
    
    @Override
    @Transactional
    public UserInfoVO createUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (getByUsername(userDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setStatus(1); // 默认启用状态
        
        // 保存用户
        save(user);
        
        // 返回用户信息
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(User user) {
        // 检查用户名是否已存在
        if (getByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 设置默认状态为启用
        user.setStatus(1);
        
        // 保存用户
        save(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(User user) {
        // 不允许修改用户名和密码
        user.setUsername(null);
        user.setPassword(null);
        
        updateById(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 重置为默认密码 123456
        user.setPassword(passwordEncoder.encode("123456"));
        updateById(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        updateById(user);
    }
    
    @Override
    public User getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            return null;
        }
        
        // 获取用户角色
        user.setRoles(roleService.getRolesByUserId(userId));
        // 获取用户权限
        user.setPermissions(permissionService.getUserPermissions(userId));
        
        return user;
    }
} 