package cn.featherfly.jdbc.rwds;

import static org.testng.Assert.assertEquals;

import javax.annotation.Resource;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cn.featherfly.common.lang.ClassLoaderUtils;
import cn.featherfly.common.lang.RandomUtils;
import cn.featherfly.jdbc.rwds.service.UserService;
import cn.featherfly.jdbc.rwds.service.UserService2;

/**
 * <p>
 * ReadWriteDataSourceTest
 * </p>
 * 
 * @author 钟冀
 */
@ContextConfiguration(locations = {"classpath:app.xml"})
@ActiveProfiles("dev")
/* 
 * 使用 AbstractTransactionalTestNGSpringContextTests 会造成读写分离错误
 *  所以直接使用AbstractTestNGSpringContextTests写入真实数据库，使用不了@Rollback
*/
//@Rollback
//public class ReadWriteDataSourceTest extends AbstractTransactionalTestNGSpringContextTests {
public class ReadWriteDataSourceTest extends AbstractTestNGSpringContextTests {
    
    @Resource
    UserService userService;
    @Resource
    UserService2 userService2;
    
    final Long id = 1000001l;
    final String usernameRead = "name1_r";
    final String usernameWrite = "name1_w";
    
    @BeforeClass
    public void init() {
        DOMConfigurator.configure(ClassLoaderUtils.getResource("log4j.xml", this.getClass()));
//        System.err.println(applicationContext.getBeansOfType(TransactionAttributeSource.class));
//        System.err.println(applicationContext.getBeansOfType(TransactionAspectSupport.class));
        
    }
    
    private User create() {
        User user = new User();
        user.setUsername("name_" + RandomUtils.getRandomInt(1000));
        return user;
    }
    
//    @Test(expectedExceptions = ReadWriteDataSourceTransactionException.class)
    @Test(expectedExceptions = TransientDataAccessResourceException.class)
    // 如果在xml配置中没有配置<tx:method name="*" read-only="true" /> 也没有使用@Tranctional标注，就会触发ReadWriteDataSourceTransactionException
    public void testNoTransactional() {
        userService.noTransactional(create(), create());
    }
    
    @Test(expectedExceptions = DuplicateKeyException.class)
    public void testUserException() {
        User u1 = create();
        User u2 = create();
        u1.setUsername("user_" + RandomUtils.getRandomInt(10000));        
        u2.setUsername("user_" + RandomUtils.getRandomInt(10000));        
        userService.user(u1, u2);
        User user = create();
        userService.user(user, user);
    }
    
    @Test
    public void testReadOnly() {
        User u = userService.get(id);
        assertEquals(u.getId(), id);
        assertEquals(u.getUsername(), usernameRead);
    }
    
    @Test(expectedExceptions = TransientDataAccessResourceException.class)
    public void testReadOnly2() {
        userService.getAndSave(id);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSaveWithException() {
        userService.saveWithException(create(), create());
    }

    @Test
    public void testWriteRead() {
        System.out.println(userService.get(id));
        
        User user = create();
                
        User wu = userService.saveAndGet(user);
                
        Assert.assertEquals(wu.getUsername(), user.getUsername());
        
        User ru  = userService.get(user.getId());        
        Assert.assertNull(ru);
        
        
        ru = userService.get(id);
        Assert.assertEquals(ru.getUsername(), usernameRead);
        System.err.println(ru);
    }
    
    @Test
    public void testUser() {
        User u1 = create();
        User u2 = create();
        u1.setUsername("user_" + RandomUtils.getRandomInt(10000));        
        u2.setUsername("user_" + RandomUtils.getRandomInt(10000));
        User uw = userService.user(u1, u2, id);
        System.out.println(uw);
        assertEquals(uw.getId(), id);
        assertEquals(uw.getUsername(), usernameWrite);
        
        User ur = userService.get(id);
        System.out.println(ur);
        assertEquals(ur.getId(), id);
        assertEquals(ur.getUsername(), usernameRead);
    }
    
    @Test
    public void testUser2() {
        User u1 = create();
        User u2 = create();
        u1.setUsername("user_" + RandomUtils.getRandomInt(10000));        
        u2.setUsername("user_" + RandomUtils.getRandomInt(10000));
        User uw = userService2.userSave(u1, u2, id);
        System.out.println(uw);
        assertEquals(uw.getId(), id);
        assertEquals(uw.getUsername(), usernameWrite);
        
        User ur = userService2.get(id);
        System.out.println(ur);
        assertEquals(ur.getId(), id);
        assertEquals(ur.getUsername(), usernameRead);
    }
    
    @Test
    public void testUser3() {
        User u1 = create();
        User u2 = create();
        u1.setUsername("user_" + RandomUtils.getRandomInt(10000));        
        u2.setUsername("user_" + RandomUtils.getRandomInt(10000));
        User uw = userService2.userSave2(u1, u2, id);        
        System.out.println(uw);
        assertEquals(uw.getId(), id);
        assertEquals(uw.getUsername(), usernameWrite);
        
        User ur = userService2.get(id);
        System.out.println(ur);
        assertEquals(ur.getId(), id);
        assertEquals(ur.getUsername(), usernameRead);
    }
    
    @Test
    public void testGetSave() {
        User u1 = create();
        u1.setUsername("user_" + RandomUtils.getRandomInt(10000));
        userService2.getSave(u1);
    }
}
