package org.example.pattern.behavioural;

import java.sql.PreparedStatement;

public class SQLParamsString implements SQLParamsChainOfResponsibility {
    private SQLParamsChainOfResponsibility nextInChain;

    @Override
    public void setNext(SQLParamsChainOfResponsibility nextInChain) {
        this.nextInChain = nextInChain;
    }

    @Override
    public void handle(PreparedStatement preparedStatement, int index, Object value) {
        if (value instanceof String) {
            try {
                preparedStatement.setString(index, (String) value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (nextInChain != null) {
            nextInChain.handle(preparedStatement, index, value);
        }
    }
}
