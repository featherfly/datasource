package cn.featherfly.jdbc.datasource;

import javax.annotation.Resource;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
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
    }
    
    private User create() {
        User user = new User();
        user.setUsername("name_" + RandomUtils.getRandomInt(1000));
        return user;
    }
    
    @Test
    public void testWriteRead() {
        
        System.out.println(userService.get(1000001l));
        
        User user = create();
                
        User wu = userService.save(user);
//        user = new User();
//        user.setUsername("name_" + RandomUtils.getRandomInt(1000));
//        userService.save(user);
//        user = new User();
//        user.setUsername("name_" + RandomUtils.getRandomInt(1000));
//        userService.save(user);
        
//        userService.save2(user);
                
        Assert.assertEquals(wu.getUsername(), user.getUsername());
        
        User ru  = userService.get(user.getId());
        Assert.assertNull(ru);
        
        
        ru = userService.get(1000001l);
        System.err.println(ru);
        
        userService.saveWithException(create(), create());
    }
}
