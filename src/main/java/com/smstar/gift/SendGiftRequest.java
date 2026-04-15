package com.smstar.gift;

import lombok.Data;

@Data
public class SendGiftRequest {
    private Long receiverId;
    private Long giftTypeId;
    private int quantity = 1;
    private String roomId;
}
