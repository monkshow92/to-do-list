package com.agenda.todolist.exception;

public class TaskNotFoundException extends RuntimeException {

    private final Integer id;

    public TaskNotFoundException(Integer id) {
        super(String.format("Task with id: %d not found", id));
        this.id = id;
    }

    public String getId() {
        return id.toString();
    }

}
