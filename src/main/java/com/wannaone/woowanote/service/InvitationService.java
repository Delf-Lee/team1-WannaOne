package com.wannaone.woowanote.service;

import com.wannaone.woowanote.domain.Invitation;
import com.wannaone.woowanote.domain.User;
import com.wannaone.woowanote.dto.InvitationAnswerDto;
import com.wannaone.woowanote.exception.RecordNotFoundException;
import com.wannaone.woowanote.repository.InvitationRepository;
import com.wannaone.woowanote.support.ErrorMessage;
import com.wannaone.woowanote.support.InvitationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvitationService {
    private static final Logger log = LoggerFactory.getLogger(InvitationService.class);

    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private NoteBookService noteBookservice;
    @Autowired
    private MessageSourceAccessor msa;

    @Transactional
    public Invitation processInvitationAnswer(User loginUser, InvitationAnswerDto responseDto) {
        Invitation invitation = getInvitationById(responseDto.getInvitationId());
        if (responseDto.getResponse() == InvitationStatus.ACCEPTED) {
            acceptInvitation(loginUser, invitation.getNoteBook().getId());
        }
        //TODO:다른 status도 처리
        invitation.setStatus(responseDto.getResponse());
        return invitation;
    }
    public Invitation getInvitationById(Long id) {
        return invitationRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(msa.getMessage(ErrorMessage.INVITATION_NOT_FOUND.getMessageKey())));
    }

    public void acceptInvitation(User loginUser, Long noteBookId) {
        userService.addSharedNoteBook(loginUser, noteBookId);
    }


}