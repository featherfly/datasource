package cn.featherfly.jdbc.datasource;

import org.springframework.core.NestedRuntimeException;

public class ReadWriteDataSourceTransactionException extends NestedRuntimeException {

    private static final long serialVersionUID = -263482866844988907L;

    ReadWriteDataSourceTransactionException(String message) {
        super(message);
    }
    
    ReadWriteDataSourceTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}