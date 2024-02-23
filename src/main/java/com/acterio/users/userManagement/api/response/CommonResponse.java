package com.acterio.users.userManagement.api.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class CommonResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String message;
    private boolean success;
    private T object;
    
    public CommonResponse(){
        this.success = false;
        this.message = "";
    }

    public T getObject() {
        return object != null ? object : (T) "";
    }
}
