package com.wannaone.woowanote.service;

import com.wannaone.woowanote.domain.NoteBook;
import com.wannaone.woowanote.domain.User;
import com.wannaone.woowanote.dto.NoteBookTitleDto;
import com.wannaone.woowanote.dto.NoteBookDto;
import com.wannaone.woowanote.exception.RecordNotFoundException;
import com.wannaone.woowanote.exception.UnAuthorizedException;
import com.wannaone.woowanote.repository.NoteBookRepository;
import com.wannaone.woowanote.support.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NoteBookService {
    @Autowired
    private NoteBookRepository noteBookRepository;
    @Autowired
    private MessageSourceAccessor msa;
    @Autowired
    private UserService userService;

    public List<NoteBook> getNoteBooksByOwnerId(Long ownerId) {
        return userService.findUserById(ownerId).getNoteBooks();
    }

    public List<NoteBook> getNoteBooksByPeerId(Long ownerId) {
        return userService.findUserById(ownerId).getSharedNotebooks();
    }

    @Transactional
    public List<NoteBookDto> getNoteBookDtosByOwnerId(User loginUser) {
        return getNoteBooksByOwnerId(loginUser.getId()).stream().map((noteBook) -> new NoteBookDto(noteBook, loginUser)).collect(Collectors.toList());
    }

    @Transactional
    public List<NoteBookDto> getSharedNoteBookDtosByPeerId(User loginUser) {
        return getNoteBooksByPeerId(loginUser.getId()).stream().map((noteBook) -> new NoteBookDto(noteBook, loginUser)).collect(Collectors.toList());
    }


    @Transactional
    public List<NoteBookDto> getNoteBookAndSharedNoteBook(User loginUser) {
        return Stream.concat(getNoteBookDtosByOwnerId(loginUser)
                .stream().sorted(Comparator.comparingInt(NoteBookDto::getPeersSize)), getSharedNoteBookDtosByPeerId(loginUser).stream()).distinct().collect(Collectors.toList());
    }

    @Transactional
    public NoteBook save(NoteBookTitleDto noteBookDto, User owner) {
        NoteBook newNoteBook = noteBookDto.toEntity();
        newNoteBook.setOwner(owner);
        return noteBookRepository.save(newNoteBook);
    }

    public NoteBook getNoteBookById(Long noteBookId) {
        return noteBookRepository.findById(noteBookId).orElseThrow(() -> new RecordNotFoundException(msa.getMessage(ErrorMessage.NOTE_BOOK_NOT_FOUND.getMessageKey())));
    }

    public NoteBookDto getNoteBookDtoById(Long noteBookId, User loginUser) {
        return NoteBookDto.fromEntity(getNoteBookById(noteBookId), loginUser);
    }

    @Transactional
    public NoteBook delete(Long noteBookId, User owner) {
        NoteBook deleteNoteBook = getNoteBookById(noteBookId);
        if(!deleteNoteBook.isNoteBookOwner(owner)) {
            throw new UnAuthorizedException(msa.getMessage(ErrorMessage.UNAUTHORIZED.getMessageKey()));
        }
        deleteNoteBook.delete();
        return deleteNoteBook;
    }
}
