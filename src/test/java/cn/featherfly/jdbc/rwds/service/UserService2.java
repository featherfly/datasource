
package cn.featherfly.jdbc.rwds.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.featherfly.jdbc.rwds.User;
import cn.featherfly.persistence.jdbc.JdbcPersistence;

/**
 * <p>
 * UserService
 * </p>
 * 
 * @author 钟冀
 */
@Service
public class UserService2 {
    
    @Resource
    JdbcPersistence jdbcPersistence;
    @Resource
    UserService userService;
    
    public User get(Long id) {
        System.out.println("invoke get");
        return jdbcPersistence.get(id, User.class);
    }
    
    @Transactional
    public void userSave(User user) {
        System.out.println("invoke userSave");
        jdbcPersistence.save(user);
    }
    
    @Transactional
    public User userSave(User u1, User u2, Long id) {
        System.out.println("invoke userSave(u1, u2)");
        userSave(u1);
        userService.save(u2);
        return get(id);
    }
    
    @Transactional
    public User userSave2(User u1, User u2, Long id) {
        System.out.println("invoke userSave2(u1, u2)");
        userSave(u1);
        userSave(u2);
        return userService.get(id);
    }
    
    @Transactional
    public void getSave(User u1) {
        System.out.println("invoke getSave(u1)");
        userSave(u1);
    }
}
