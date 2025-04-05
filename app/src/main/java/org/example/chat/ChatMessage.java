package org.example.chat;

import java.io.Serializable;

public record ChatMessage(String nickname, String content) implements Serializable {
}
