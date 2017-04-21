package cn.featherfly.jdbc.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.util.PatternMatchUtils;

/**
 * 
 * 
 * <pre>
 * 
 * 此类实现了两个职责（为了减少类的数量将两个功能合并到一起了）：
 *   读/写动态数据库选择处理器
 *   通过AOP切面实现读/写选择
 *   
 *   
 * ★★读/写动态数据库选择处理器★★
 * 1、首先读取<tx:advice>事务属性配置
 * 
 * 2、对于所有读方法设置 read-only="true" 表示读取操作（以此来判断是选择读还是写库），其他操作都是走写库
 *    如<tx:method name="×××" read-only="true"/>
 *    
 * 3、 forceChoiceReadOnWrite用于确定在如果目前是写（即开启了事务），下一步如果是读，
 *    是直接参与到写库进行读，还是强制从读库读<br/>
 *      forceChoiceReadOnWrite:true 表示目前是写，下一步如果是读，强制参与到写事务（即从写库读）
 *                                  这样可以避免写的时候从读库读不到数据
 *                                  
 *                                  通过设置事务传播行为：SUPPORTS实现
 *                                  
 *      forceChoiceReadOnWrite:false 表示不管当前事务是写/读，都强制从读库获取数据
 *                                  通过设置事务传播行为：NOT_SUPPORTS实现（连接是尽快释放）                
 *                                  『此处借助了 NOT_SUPPORTS会挂起之前的事务进行操作 然后再恢复之前事务完成的』
 * 4、配置方式
 *  <bean id="readWriteDataSourceTransactionProcessor" class=
"cn.javass.common.datasource.ReadWriteDataSourceProcessor">
 *      <property name="forceChoiceReadWhenWrite" value="false"/>
 *  </bean>
 *
 * 5、目前只适用于<tx:advice>情况 TODO 支持@Transactional注解事务
 *  
 *  
 *  
 * ★★通过AOP切面实现读/写库选择★★
 * 
 * 1、首先将当前方法 与 根据之前【读/写动态数据库选择处理器】  提取的读库方法 进行匹配
 * 
 * 2、如果匹配，说明是读取数据：
 *  2.1、如果forceChoiceReadOnWrite:true，即强制走读库
 *  2.2、如果之前是写操作且forceChoiceReadOnWrite:false，将从写库进行读取
 *  2.3、否则，到读库进行读取数据
 * 
 * 3、如果不匹配，说明默认将使用写库进行操作
 * 
 * 4、配置方式
 *      <aop:aspect order="-2147483648" ref=
"readWriteDataSourceTransactionProcessor">
 *          <aop:around pointcut-ref="txPointcut" method=
"determineReadOrWriteDB"/>
 *      </aop:aspect>
 *  4.1、此处order = Integer.MIN_VALUE 即最高的优先级（请参考http://jinnianshilongnian.iteye.com/blog/1423489）
 *  4.2、切入点：txPointcut 和 实施事务的切入点一样
 *  4.3、determineReadOrWriteDB方法用于决策是走读/写库的，请参考
 *       &#64;see ReadWriteDataSourceDecision
 *       &#64;see ReadWriteDataSource
 * 
 * </pre>
 */
public class ReadWriteDataSourceProcessor2 implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ReadWriteDataSourceProcessor2.class);

    private boolean forceChoiceReadWhenWrite = false;

    private TransactionAspectSupport transactionAspectSupport;

    // private Set<String> writeMethodSet = new HashSet<>();

    /**
     * 当之前操作是写的时候，是否强制从从库读 默认（false） 当之前操作是写，默认强制从写库读
     * 
     * @param forceChoiceReadWhenWrite
     */

    public void setForceChoiceReadWhenWrite(boolean forceChoiceReadWhenWrite) {
        this.forceChoiceReadWhenWrite = forceChoiceReadWhenWrite;
    }

    public Object determineReadOrWriteDB(ProceedingJoinPoint pjp) throws Throwable {
        if (pjp.getSignature() instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();

            final RuleBasedTransactionAttribute txAttr = (RuleBasedTransactionAttribute) transactionAspectSupport
                    .getTransactionAttributeSource()
                    .getTransactionAttribute(methodSignature.getMethod(), pjp.getSignature().getDeclaringType());
            
            boolean choice = false;
            if (ReadWriteDataSourceDecision.isChoiceNone()) {
                // 表示第一个标记，事物可能嵌套，所有只有第一个标记的才才能进行清除
                choice = true;
            }
            
            System.out.println();
            System.out.println(methodSignature.getMethod().getName() + " isRead " + isChoiceReadDB(txAttr));
            System.out.println();
            
            if (isChoiceReadDB(txAttr)) {
                ReadWriteDataSourceDecision.markRead();
            } else {
                ReadWriteDataSourceDecision.markWrite();
            }

            try {
                return pjp.proceed();
            } finally {
                if (choice) {
                    ReadWriteDataSourceDecision.reset();
                }
            }
        }

        throw new RuntimeException("! pjp.getSignature() instanceof MethodSignature");

        // boolean choice = false;
        // if (ReadWriteDataSourceDecision.isChoiceNone()) {
        // // 表示第一个标记，事物可能嵌套，所有只有第一个标记的才才能进行清除
        // choice = true;
        // }
        // if (isChoiceReadDB(pjp.getSignature().getName())) {
        // ReadWriteDataSourceDecision.markRead();
        // } else {
        // ReadWriteDataSourceDecision.markWrite();
        // }
        //
        // try {
        // return pjp.proceed();
        // } finally {
        // if (choice) ReadWriteDataSourceDecision.reset();
        // }

    }

    // private boolean isChoiceWriteDB(String methodName) {
    // String bestNameMatch = null;
    // for (String mappedName : this.writeMethodSet) {
    // if (isMatch(methodName, mappedName)) {
    // bestNameMatch = mappedName;
    // break;
    // }
    // }
    // Boolean isChoiceWirte = readMethodMap.get(bestNameMatch);
    // return isChoiceWirte == Boolean.TRUE;
    // }

    private boolean isChoiceReadDB(TransactionAttribute txAttr) {
        if (forceChoiceReadWhenWrite) {
            //不管之前操作是读还是写，默认强制从读库读 （设置为NOT_SUPPORTED即可）
            return true;
        }
        if(ReadWriteDataSourceDecision.isChoiceWrite()) {
            //如果之前选择了写库 现在还选择 写库
            return false;
        }
        return txAttr.isReadOnly();
    }

    protected boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }

    /**
     * 返回transactionAspectSupport
     * 
     * @return transactionAspectSupport
     */
    public TransactionAspectSupport getTransactionAspectSupport() {
        return transactionAspectSupport;
    }

    /**
     * 设置transactionAspectSupport
     * 
     * @param transactionAspectSupport
     *            transactionAspectSupport
     */
    public void setTransactionAspectSupport(TransactionAspectSupport transactionAspectSupport) {
        this.transactionAspectSupport = transactionAspectSupport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // YUFEI_TODO Auto-generated method stub
        return null;
    }
}
