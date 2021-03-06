package com.wannaone.woowanote.dto;

import com.wannaone.woowanote.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvitationGuestDto {

    private Long id;
    private String email;
    private String photoUrl;
    private String name;

    public InvitationGuestDto() {

    }

    public InvitationGuestDto(User guest) {
        this(guest.getId(), guest.getEmail(), guest.getPhotoUrl(), guest.getName());
    }
}
