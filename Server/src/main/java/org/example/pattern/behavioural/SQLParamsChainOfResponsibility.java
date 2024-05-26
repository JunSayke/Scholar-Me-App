package org.example.pattern.behavioural;

import java.sql.PreparedStatement;

public interface SQLParamsChainOfResponsibility {
    void setNext(SQLParamsChainOfResponsibility nextInChain);

    void handle(PreparedStatement preparedStatement, int index, Object value);
}

