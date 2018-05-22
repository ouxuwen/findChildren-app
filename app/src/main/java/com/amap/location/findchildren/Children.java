package com.amap.location.findchildren;

/**
 * Created by kevin.zhang on 2018/4/17.
 */
import java.io.Serializable;
public class Children implements Serializable {
    private String email;
    private String detail;
    private static final long serialVersionUID = 1L;
    public Children(String s, String s1) {
        this.email =s;
        this.detail =s1;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDetail() {
        return detail;
    }

    public String getEmail() {
        return email;
    }
}
