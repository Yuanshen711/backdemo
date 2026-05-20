package com.orangesoftware.back.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result{
    private int code;
    private String message;
    private Object data;


    public static Result success(Object data) {
        return new Result(200, "success", data);
    }
    
    public static Result success(Object data, String message) {
        return new Result(200, message, data);
    }

    public static Result error(String message) {
        return new Result(500, message, null);
    }

}
