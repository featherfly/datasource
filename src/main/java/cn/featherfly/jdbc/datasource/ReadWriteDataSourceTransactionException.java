package cn.featherfly.jdbc.datasource;

import org.springframework.core.NestedRuntimeException;

/**
 * <p>
 * ReadWriteDataSourceTransactionException
 * </p>
 * 
 * @author zhongj
 */
public class ReadWriteDataSourceTransactionException extends NestedRuntimeException {

    private static final long serialVersionUID = -263482866844988907L;
    
    /**
     * @param message message
     */
    ReadWriteDataSourceTransactionException(String message) {
        super(message);
    }
    
    /**
     * 
     * @param message message
     * @param cause Throwable
     */
    ReadWriteDataSourceTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}