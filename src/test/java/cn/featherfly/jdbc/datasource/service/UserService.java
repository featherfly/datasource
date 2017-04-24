
package cn.featherfly.jdbc.datasource.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.featherfly.jdbc.datasource.User;
import cn.featherfly.persistence.jdbc.JdbcPersistence;

/**
 * <p>
 * UserService
 * </p>
 * 
 * @author 钟冀
 */
@Service
public class UserService {
    
    @Resource
    JdbcPersistence jdbcPersistence;
    
    @Transactional
    public User save(User user) {
        System.out.println("invoke save");
        jdbcPersistence.save(user);
        return get(user.getId());
    }
    
    public User save1(User user) {
        System.out.println("invoke save1");
        jdbcPersistence.save(user);
        return get(user.getId());
    }
    
    public User saveWithException(User u1, User u2) {
        System.out.println("invoke saveWithException");
        save(u1);
        save(u2);
        throw new RuntimeException("saveWithException exception");
    }
    
    public void exception(User u1, User u2) {
        System.out.println("invoke exception");
        save(u1);
        save(u2);
        throw new RuntimeException("exception exception");
    }
    
    public void noTransactional(User u1, User u2) {
        System.out.println("invoke noTransactional");
        save(u1);
        save(u2);
        throw new RuntimeException("noTransactional exception");
    }
    
    @Transactional
    public void exceptionTransactional(User u1, User u2) {
        System.out.println("invoke exceptionTransactional");
        save(u1);
        save(u2);
        throw new RuntimeException("saveWithException exception");
    }
    
    @Transactional
    public void user(User u1, User u2) {
        System.out.println("invoke user");
        save(u1);
        save(u2);
    }
    
    @Transactional(readOnly = true)
    public User get(Long id) {
        System.out.println("invoke get");
        return jdbcPersistence.get(id, User.class);
    }
    
    public User getAndSave(Long id) {
        System.out.println("invoke get");
        User u = jdbcPersistence.get(id, User.class);
        return save(u);
    }
}
