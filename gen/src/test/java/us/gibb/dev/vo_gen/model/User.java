package us.gibb.dev.vo_gen.model;

import java.util.Date;

import us.gibb.dev.vo_gen.SomeAnnotation;

@SomeAnnotation
@SuppressWarnings("unused")
public class User {

    @SomeAnnotation
    private String username;
    private Date birthday;
    private int i,j;

    public Date getBirthday() {
        return birthday;
    }

    @SomeAnnotation
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    @Override
    public String toString() {
        return username;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public void setJ(int j) {
        this.j = j;
    }
    
    public void methodToIgnore() {
        
    }
    
    public class Inner {}
}
