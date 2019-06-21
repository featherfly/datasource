
package cn.featherfly.jdbc.rwds.service;

import javax.annotation.Resource;

import org.springframework.lang.UsesSunHttpServer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mchange.lang.LongUtils;

import cn.featherfly.common.lang.LangUtils;
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
public class UserService {
    
    @Resource
    JdbcPersistence jdbcPersistence;
            
    @Transactional
    public void save(User user) {
        System.out.println("invoke save");
        jdbcPersistence.save(user);
    }
    
    public User saveAndGet(User user) {
        System.out.println("invoke saveAndGet");
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
        System.out.println(LangUtils.getInvoker());
        System.out.println(LangUtils.getInvokers());
        System.out.println("invoke noTransactional");
        save(u1);
        save(u2);
        throw new RuntimeException("noTransactional exception");
    }
    
    @Transactional
    public void exceptionTransactional(User u1, User u2) {
        System.out.println("invoke exceptionTransactional(u1, u2)");
        save(u1);
        save(u2);
        throw new RuntimeException("saveWithException exception");
    }
    
    @Transactional
    public void user(User u1, User u2) {
        System.out.println("invoke user(u1, u2)");
        save(u1);
        save(u2);
    }
    
    @Transactional
    public User user(User u1, User u2, Long id) {
        user(u1, u2);
        return get(id);
    }
    
    @Transactional(readOnly = true)
    public User get(Long id) {
        System.out.println("invoke get");
        return jdbcPersistence.get(id, User.class);
    }
    
    public User getAndSave(Long id) {
        System.out.println("invoke getAndSave");
        User u = jdbcPersistence.get(id, User.class);
        return this.saveAndGet(u);
    }
    
    @Transactional(readOnly = true)
    public User inReadTransactionWrite(Long id) {
        System.out.println("invoke getAndSave");
        User u = jdbcPersistence.get(id, User.class);
        return this.saveAndGet(u);
    }
}
