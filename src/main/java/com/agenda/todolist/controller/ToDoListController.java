package com.agenda.todolist.controller;

import com.agenda.todolist.entity.ToDoList;
import com.agenda.todolist.service.ToDoListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/to-do-lists")
public class ToDoListController {

    @Autowired
    ToDoListService toDoListService;

    @GetMapping
    public List<ToDoList> getAllToDoLists() {
        return toDoListService.getAllToDoLists();
    }

    @GetMapping(value = "/{id}")
    public ToDoList getToDoListById(@PathVariable(value = "id") Integer id) {
        return toDoListService.getToDoListById(id);
    }

    @PostMapping
    public ToDoList insertToDoList(@RequestBody ToDoList toDoList) {
        return toDoListService.insertToDoList(toDoList);
    }

    @PutMapping(value = "/{id}")
    public void updateToDoList(@RequestBody ToDoList toDoList, @PathVariable(value = "id") Integer id) {
        toDoListService.updateToDoList(toDoList, id);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteToDoList(@PathVariable(value = "id") Integer id) {
        toDoListService.deleteToDoList(id);
    }

}
