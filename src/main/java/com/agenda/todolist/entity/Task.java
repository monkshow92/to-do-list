package com.agenda.todolist.entity;

import org.hibernate.annotations.Check;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "to_do_list_id")
    ToDoList toDoList;

    @NotNull
    @Size(max = 255)
    private String description;

    @NotNull
    @Check(constraints = "priority BETWEEN 1 and 5")
    private Short priority;

    @NotNull
    @Size(max = 15)
    @Check(constraints = "status IN ('TO DO', 'IN PROGRESS', 'DEFERRED', 'DONE')")
    private String status;

    @NotNull
    private Boolean done;

    @Size(max = 255)
    private String observations;

    public Task() {} // for JPA

    public Task(ToDoList toDoList, String description, Short priority, String status, Boolean done, String observations) {
        this.toDoList = toDoList;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.done = done;
        this.observations = observations;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ToDoList getToDoList() {
        return toDoList;
    }

    public void setToDoList(ToDoList toDoList) {
        this.toDoList = toDoList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getPriority() {
        return priority;
    }

    public void setPriority(Short priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

}
