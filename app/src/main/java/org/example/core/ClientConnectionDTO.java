package org.example.core;

import java.io.Serializable;

public record ClientConnectionDTO(String nickname, String roomID) implements Serializable {
}
