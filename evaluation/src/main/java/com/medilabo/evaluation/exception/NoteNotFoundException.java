package com.medilabo.evaluation.exception;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(String message) {
        super("Note not found: " + message);
    }
}
