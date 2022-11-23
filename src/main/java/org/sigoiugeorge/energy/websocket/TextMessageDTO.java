package org.sigoiugeorge.energy.websocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class TextMessageDTO {

    private final String message;

}