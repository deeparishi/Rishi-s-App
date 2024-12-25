package com.jwt.JwtSecurity.exception;

public class LimitReachedException extends RuntimeException{

    public LimitReachedException(String msg){
        super(msg);
    }

}
