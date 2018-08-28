package com.wannaone.woowanote.dto;

import com.wannaone.woowanote.domain.Invitation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@AllArgsConstructor
public class NotificationMessageDto {
    private static final Logger log = LoggerFactory.getLogger(NotificationMessageDto.class);
    private Long id;
    private String message;

    public NotificationMessageDto(Invitation invitation) {
        log.info("meessag, {} ", invitation.getId());
        this.id = invitation.getId();
        this.message = invitation.getHost().getName() +"님의 " + invitation.getNoteBook().getTitle() + " 공유 초대";
    }

    public NotificationMessageDto() {
    }
}