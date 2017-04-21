
package cn.featherfly.jdbc.datasource;

import java.lang.reflect.Method;
import java.util.Properties;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * <p>
 * ReadWriteDataSourceTransactionInterceptor
 * </p>
 * 
 * @author 钟冀
 */
public class ReadWriteDataSourceTransactionInterceptor extends TransactionInterceptor{
    
    private static final Logger log = LoggerFactory.getLogger(ReadWriteDataSourceTransactionInterceptor.class);
    
    private boolean forceChoiceReadWhenWrite = false;
    
    /**
     * 
     */
    private static final long serialVersionUID = -464676569160780587L;

    /**
     * 
     */
    public ReadWriteDataSourceTransactionInterceptor() {
        super();
    }

    /**
     * @param ptm
     * @param attributes
     */
    public ReadWriteDataSourceTransactionInterceptor(PlatformTransactionManager ptm, Properties attributes) {
        super(ptm, attributes);
    }

    /**
     * @param ptm
     * @param tas
     */
    public ReadWriteDataSourceTransactionInterceptor(PlatformTransactionManager ptm, TransactionAttributeSource tas) {
        super(ptm, tas);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        Method method = invocation.getMethod();
        
        final RuleBasedTransactionAttribute txAttr = (RuleBasedTransactionAttribute)
                getTransactionAttributeSource().getTransactionAttribute(method, targetClass);

        if(txAttr.isReadOnly()) {
            Boolean isForceChoiceRead = Boolean.FALSE;
            if(forceChoiceReadWhenWrite) {
                //不管之前操作是写，默认强制从读库读 （设置为NOT_SUPPORTED即可）
                //NOT_SUPPORTED会挂起之前的事务
                txAttr.setPropagationBehavior(Propagation.NOT_SUPPORTED.value());
                isForceChoiceRead = Boolean.TRUE;
            } else {
                //否则 设置为SUPPORTS（这样可以参与到写事务）
                txAttr.setPropagationBehavior(Propagation.SUPPORTS.value());
            }
            log.debug("read/write transaction process  method:{} force read:{}", method.getName(), isForceChoiceRead);
        }
        
        boolean choice = false;
        if (ReadWriteDataSourceDecision.isChoiceNone()) {
            // 表示第一个标记，事物可能嵌套，所有只有第一个标记的才才能进行清除
            choice = true;
        }
        
        System.out.println();
        System.out.println(method.getName() + " isRead " + isChoiceReadDB(txAttr));
        System.out.println();
        
        if (isChoiceReadDB(txAttr)) {
            ReadWriteDataSourceDecision.markRead();
        } else {
            ReadWriteDataSourceDecision.markWrite();
        }
        
        
        try {
            return super.invoke(invocation);
        } finally {
            if (choice) ReadWriteDataSourceDecision.reset();
        }
    }
    
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    protected Object invokeWithinTransaction(Method method, Class<?> targetClass, final InvocationCallback invocation)
//            throws Throwable {
////        final TransactionAttribute txAttr = getTransactionAttributeSource().getTransactionAttribute(method, targetClass);
//        final RuleBasedTransactionAttribute txAttr = (RuleBasedTransactionAttribute)
//                getTransactionAttributeSource().getTransactionAttribute(method, targetClass);
//
//        if(txAttr.isReadOnly()) {
//            Boolean isForceChoiceRead = Boolean.FALSE;
//            if(forceChoiceReadWhenWrite) {
//                //不管之前操作是写，默认强制从读库读 （设置为NOT_SUPPORTED即可）
//                //NOT_SUPPORTED会挂起之前的事务
//                txAttr.setPropagationBehavior(Propagation.NOT_SUPPORTED.value());
//                isForceChoiceRead = Boolean.TRUE;
//            } else {
//                //否则 设置为SUPPORTS（这样可以参与到写事务）
//                txAttr.setPropagationBehavior(Propagation.SUPPORTS.value());
//            }
//            log.debug("read/write transaction process  method:{} force read:{}", method.getName(), isForceChoiceRead);
//        }
//        
//        boolean choice = false;
//        if (ReadWriteDataSourceDecision.isChoiceNone()) {
//            // 表示第一个标记，事物可能嵌套，所有只有第一个标记的才才能进行清除
//            choice = true;
//        }
//        
//        System.out.println();
//        System.out.println(method.getName() + " isRead " + isChoiceReadDB(txAttr));
//        System.out.println();
//        
//        if (isChoiceReadDB(txAttr)) {
//            ReadWriteDataSourceDecision.markRead();
//        } else {
//            ReadWriteDataSourceDecision.markWrite();
//        }
//   
//        try {
//            return super.invokeWithinTransaction(method, targetClass, invocation);
//        } finally {
//            if (choice) ReadWriteDataSourceDecision.reset();
//        }
//        
//        
//    }
    
    
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

}
