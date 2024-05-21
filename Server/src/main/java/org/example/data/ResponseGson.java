package org.example.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseGson<T> extends GsonData {
    private boolean status;
    private String message;
    private T data;

    public ResponseGson(boolean status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }
}
