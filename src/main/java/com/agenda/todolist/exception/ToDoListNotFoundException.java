package com.agenda.todolist.exception;

public class ToDoListNotFoundException extends RuntimeException {

    private final Integer id;

    public ToDoListNotFoundException(Integer id) {
        super(String.format("To-do list with id: %d not found", id));
        this.id = id;
    }

    public String getId() {
        return id.toString();
    }

}
