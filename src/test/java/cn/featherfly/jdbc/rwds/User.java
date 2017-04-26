
package cn.featherfly.jdbc.rwds;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * User
 * 类的说明放这里
 * </p>
 * 
 * @author 钟冀
 */
@Table
public class User {
    
    @Id
    private Long id;
    
    private String username;
    
    private String password;
    
    private String mobileNo;

    /**
     * 返回id
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置id
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 返回username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置username
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 返回password
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置password
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 返回mobileNo
     * @return mobileNo
     */
    public String getMobileNo() {
        return mobileNo;
    }

    /**
     * 设置mobileNo
     * @param mobileNo mobileNo
     */
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return id + "," + username + "," + mobileNo;
    }
}
