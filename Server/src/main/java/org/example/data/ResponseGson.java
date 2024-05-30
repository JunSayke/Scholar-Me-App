package org.example.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class ResponseGson<T> extends GsonData {
    private boolean status;
    private String message;
    private Integer code;
    private T data;

    public ResponseGson(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseGson(boolean status, String message, T data) {
        this(status, message, null, data);
    }
}
