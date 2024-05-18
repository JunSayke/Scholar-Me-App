package org.example.data;

import lombok.Getter;

@Getter
public class ErrorGson extends ResponseGson {
    public ErrorGson(boolean status, String message) {
        super(status, message);
    }
}
