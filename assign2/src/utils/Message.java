package utils;


import java.io.Serializable;

public class Message implements Serializable {
    MessageType messageType;
    String messageBody;
    String token_username;

    public Message(MessageType messageType, String messageBody, String token_username) {
        this.messageType = messageType;
        this.messageBody = messageBody;
        this.token_username = token_username;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getTokenUsername() {
        return token_username;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "token_username=" + token_username +
                ", messageType=" + messageType +
                ", messageBody=" + messageBody +
                '}';
    }
}
