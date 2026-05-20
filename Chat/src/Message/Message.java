package Message;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String type;
    private final String content;
    private final String sender;
    private final LocalDateTime timestamp;
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Message(String type, String content, String sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", timestamp.format(TIME_FORMATTER), sender, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }

        Message other = (Message) o;
        return timestamp.equals(other.timestamp) &&
                sender.equals(other.sender) &&
                content.equals(other.content) &&
                type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, sender, content, type);
    }
}
