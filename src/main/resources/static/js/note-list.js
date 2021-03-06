class NoteList {
    constructor() {
        this.noteListBar = $('.side-bar-middle');
        this.noteListBody = $('.side-bar-middle-body');
        this.noteListEl = $('.note-list');
        this.noteListCount = $('.note-list-count');
        this.currentNoteIndex = 0;
        this.shareNotebookPopup = $('.share-notebook-popup');
        this.shareNotebookPopup.style.display = "none";
        this.invitationInputEl = $('.share-invitation > input');
        this.invitationListEl = $('#share-invitation-list');
        $('.share-notebook-open-button').addEventListener("click", this.openShareNotebookPopupHandler.bind(this));
        document.addEventListener("click", this.closeShareNotebookPopupHandler.bind(this));

        this.initEvent();
    }

    initEvent() {
        this.noteListEl.addEventListener('dragstart', this.updateNoteOnDragStartEventHandler.bind(this));
        $('#add-comment-button').addEventListener('click', this.markNoteUpdatedFalse.bind(this));
        $('#comment-input').addEventListener('keyup', ({keyCode}) => {
            if(keyCode === 13) {
                this.markNoteUpdatedFalse();
            }
        });
        $('.comment-list').addEventListener('click', this.markNoteUpdatedFalse.bind(this));
    }

    toggleNoteListBar() {
        this.noteListBar.classList.toggle('note-list-hide');
    }

    updateNoteOnDragStartEventHandler(evt) {
        evt.dataTransfer.setData("noteId", evt.target.dataset.noteId);
    }

    fetchNoteUpdateParentNoteBook(noteId, noteBookId, successCallback, failCallback) {
        fetchManager({
            url: `/api/notes/${noteId}/notebooks/${noteBookId}`,
            method: 'PATCH',
            headers: {'content-type': 'application/json'},
            onSuccess: successCallback,
            onFailure: failCallback
        });
    }

    renderNoteItem(note) {
        this.noteListEl.insertAdjacentHTML('beforeend', getNoteItemTemplate(note));
    }

    renderNoteList(notes) {
        this.clearNoteListSection();
        this.notes = notes;
        this.noteListBody.scroll(0,0);
        this.noteListCount.innerHTML = `${notes.length}개의 노트`;
        this.notes.forEach((note) => this.renderNoteItem(note));
    }

    clearNoteListSection() {
        this.noteListEl.innerHTML = '';
    }

    fetchNoteList(noteBookId, successCallback, failureCallback) {
        fetchManager({
            url: `/api/notebooks/${noteBookId}`,
            method: 'GET',
            onSuccess: successCallback,
            onFailure: failureCallback
        });
    }

    focusNoteItem(index) {
        if (this.noteListEl.children.length > 0) {
            const existFocusNote = $('.note-item-focus');
            if(existFocusNote) {
                existFocusNote.classList.toggle('note-item-focus');
            }
            this.noteListEl.children[index].firstElementChild.classList.add('note-item-focus');
            this.currentNoteIndex = index;
            return true;
        }
        return false;
    }

    focusNoteItemById(id) {
        const noteLists = this.noteListEl.children;
        if (noteLists.length > 0) {
            const existFocusNote = $('.note-item-focus');
            if(existFocusNote) {
                existFocusNote.classList.toggle('note-item-focus');
            }
            for(let i = 0; i<noteLists.length; i++) {
                if(noteLists[i].dataset.noteId == id) {
                    noteLists[i].firstElementChild.classList.add('note-item-focus');
                    this.currentNoteIndex = i;
                }
            }
        }
    }

    getNoteId() {
        const currentNote = this.notes[this.currentNoteIndex];
        if(!currentNote) {
            return -1;
        }
        return this.notes[this.currentNoteIndex].id;
    }

    getNote() {
        return this.notes[this.currentNoteIndex];
    }

    isNewItemClicked(liElement) {
        return $('.note-item-focus') !== liElement.firstElementChild;
    }


    // 새 노트 저장
    createNewNote(noteBookId, successCallBack, failCallBack) {
        if(!noteBookId) {
            console.log('노트북이 선택되지 않았습니다.');
            return;
        }

        fetchManager({
            url: `/api/notes/notebook/${noteBookId}`,
            method: 'POST',
            headers: {'content-type': 'application/json'},
            onSuccess: successCallBack,
            onFailure: failCallBack
        })
    }

    openShareNotebookPopupHandler() {
        this.shareNotebookPopup.style.display = 'block';
        this.invitationInputEl.focus();
    }

    closeShareNotebookPopupHandler(e) {
        if ($('.share-notebook-open-button').contains(e.target) || this.shareNotebookPopup.contains(e.target)
        || e.target.classList.contains('invitation-list-item') || e.target.classList.contains('invitation-cancel-button')) {
            return;
        }
       this.clearShareNotebookPopup();
        this.shareNotebookPopup.style.display = 'none';
    }

    clearShareNotebookPopup() {
        this.invitationInputEl.innerText = "";
        this.invitationListEl.innerHTML = "";
    }

    isItemUpdated(index) {
        const isUpdated = this.noteListEl.children[index].dataset.noteUpdated;
        console.log(isUpdated);
        return isUpdated;
    }

    markNoteUpdatedFalse() {
        this.noteListEl.children[this.currentNoteIndex].dataset.noteUpdated = "false";
    }

    markNoteUpdatedTrue() {
        this.noteListEl.children[this.currentNoteIndex].dataset.noteUpdated = "true";
    }
}