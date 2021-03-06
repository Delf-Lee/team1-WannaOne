package com.wannaone.woowanote.web;

import com.wannaone.woowanote.domain.Comment;
import com.wannaone.woowanote.domain.Note;
import com.wannaone.woowanote.domain.NoteBook;
import com.wannaone.woowanote.dto.CommentDto;
import com.wannaone.woowanote.dto.NoteBookTitleDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static org.assertj.core.api.Assertions.assertThat;


public class AuditingEntityAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(AuditingEntityAcceptanceTest.class);

    @Test
    public void createCommentRegisterDatetimeExistTest() {
        String noteBookName = "내가 쓴 첫번 째 노트북";
        NoteBookTitleDto noteBookDto = new NoteBookTitleDto(noteBookName);
        ResponseEntity<NoteBook> createNoteBookResponse = basicAuthTemplate().postForEntity("/api/notebooks", noteBookDto, NoteBook.class);
        Long noteBookId = createNoteBookResponse.getBody().getId();

        ResponseEntity<Note> createNoteResponse = basicAuthTemplate().postForEntity("/api/notes/notebook/" + noteBookId, null, Note.class);
        Long noteId = createNoteResponse.getBody().getId();

        String commentContent = "댓글 내용";
        CommentDto commentDto = new CommentDto(commentContent);
        ResponseEntity<CommentDto> commentCreateResponse = basicAuthTemplate().postForEntity("/api/notes/" + noteId + "/comments", commentDto, CommentDto.class);
        CommentDto comment = commentCreateResponse.getBody();

        assertThat(comment.getRegisterDatetime()).isNotNull();
        assertThat(comment.getUpdateDatetime()).isNotNull();
    }

    @Test
    public void createNoteRegisterDatetimeExistTest() {
        String noteBookName = "내가 쓴 첫번 째 노트북";
        NoteBookTitleDto noteBookDto = new NoteBookTitleDto(noteBookName);
        ResponseEntity<NoteBook> createNoteBookResponse = basicAuthTemplate().postForEntity("/api/notebooks", noteBookDto, NoteBook.class);
        Long noteBookId = createNoteBookResponse.getBody().getId();

        ResponseEntity<Note> createNoteResponse = basicAuthTemplate().postForEntity("/api/notes/notebook/" + noteBookId, null, Note.class);
        Long noteId = createNoteResponse.getBody().getId();
        assertThat(createNoteResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createNoteResponse.getBody().getTitle()).isEqualTo("나의 우아한 노트");
        assertThat(noteId).isNotNull();
        Note note = createNoteResponse.getBody();

        assertThat(note.getRegisterDatetime()).isNotNull();
        assertThat(note.getUpdateDatetime()).isNotNull();
    }
}