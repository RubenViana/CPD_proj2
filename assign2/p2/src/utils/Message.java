package utils;


import java.io.Serializable;

public class Message implements Serializable {
    MessageType messageType;
    String messageBody;
    String token;

    public Message(MessageType messageType, String messageBody, String token) {
        this.messageType = messageType;
        this.messageBody = messageBody;
        this.token = token;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "token=" + token +
                ", messageType=" + messageType +
                ", messageBody=" + messageBody +
                '}';
    }
}
