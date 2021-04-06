package com.tyron.layouteditor.editor.exceptions;

public class DrawableNotFoundException extends Exception {

    public DrawableNotFoundException() {

    }

    public DrawableNotFoundException(String message){
        super(message);
    }

    public DrawableNotFoundException(String message, Throwable throwable){
        super(message, throwable);
    }

    public DrawableNotFoundException(Throwable cause){
        super(cause);
    }
}
