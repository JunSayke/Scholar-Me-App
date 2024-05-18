package org.example.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ResponseGson extends GsonData {
    protected boolean status;
    protected String message;
}
