package org.sigoiugeorge.energy.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TicketShort {
    private Long id;
    private String clientUsername;
    private String adminUsername;
    private Integer unreadMessagesNumber;
}
