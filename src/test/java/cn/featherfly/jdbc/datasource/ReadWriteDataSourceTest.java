package cn.featherfly.jdbc.datasource;

import javax.annotation.Resource;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cn.featherfly.common.lang.ClassLoaderUtils;
import cn.featherfly.common.lang.RandomUtils;
import cn.featherfly.jdbc.datasource.service.UserService;

/**
 * <p>
 * ReadWriteDataSourceTest
 * </p>
 * 
 * @author 钟冀
 */
@ContextConfiguration(locations = {"classpath:app.xml"})
@ActiveProfiles("dev")
//@Rollback(false)
//public class ReadWriteDataSourceTest extends AbstractTransactionalTestNGSpringContextTests {
public class ReadWriteDataSourceTest extends AbstractTestNGSpringContextTests {
    
    @Resource
    UserService userService;
    
    @BeforeClass
    public void init() {
        DOMConfigurator.configure(ClassLoaderUtils.getResource("log4j.xml", this.getClass()));
        System.err.println(applicationContext.getBeansOfType(TransactionAttributeSource.class));
        System.err.println(applicationContext.getBeansOfType(TransactionAspectSupport.class));
        
    }
    
    private User create() {
        User user = new User();
        user.setUsername("name_" + RandomUtils.getRandomInt(1000));
        return user;
    }
    
    @Test(expectedExceptions = ReadWriteDataSourceTransactionException.class)
    public void testNoTransactional() {
        userService.noTransactional(create(), create());
    }
    
    @Test(expectedExceptions = DuplicateKeyException.class)
    public void testUser() {
        User u1 = create();
        User u2 = create();
        u1.setUsername("user_" + RandomUtils.getRandomInt(10000));        
        u2.setUsername("user_" + RandomUtils.getRandomInt(10000));        
        userService.user(u1, u2);

        User user = create();
        userService.user(user, user);
    }
    
    @Test(expectedExceptions = TransientDataAccessResourceException.class)
    public void testReadOnly() {
        userService.getAndSave(1000001l);
    }
    
    @Test(expectedExceptions = RuntimeException.class)
    public void testSaveWithException() {
        userService.saveWithException(create(), create());
    }

    @Test
    public void testWriteRead() {
        System.out.println(userService.get(1000001l));
        
        User user = create();
                
        User wu = userService.save(user);
                
        Assert.assertEquals(wu.getUsername(), user.getUsername());
        
        User ru  = userService.get(user.getId());        
        Assert.assertNull(ru);
        
        
        ru = userService.get(1000001l);
        Assert.assertEquals(ru.getUsername(), "name1_r");
        System.err.println(ru);
    }
}
