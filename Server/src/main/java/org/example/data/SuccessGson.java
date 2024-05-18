package org.example.data;

import lombok.Getter;

@Getter
public class SuccessGson<T> extends ResponseGson {
    private T data;

    public SuccessGson(boolean status, String message, T data) {
        super(status, message);
        this.data = data;
    }
}
