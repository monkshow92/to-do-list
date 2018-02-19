package com.agenda.todolist.controller;

import com.agenda.todolist.entity.Task;
import com.agenda.todolist.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/to-do-lists/{id}/tasks")
public class TaskController {

    @Autowired
    TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks(@PathVariable(value = "id") Integer toDoListId) {
        return taskService.getAllTasksByToDoListId(toDoListId);
    }

    @GetMapping(value = "/{taskId}")
    public Task getTaskById(@PathVariable(value = "taskId") Integer taskId,
                            @PathVariable(value = "id") Integer toDoListId) {
        return taskService.getTaskById(taskId, toDoListId);
    }

    @PostMapping
    public Task insertTask(@RequestBody Task task, @PathVariable(value = "id") Integer toDoListId) {
        return taskService.insertTask(task, toDoListId);
    }

    @PutMapping(value = "/{taskId}")
    public void updateTask(@PathVariable(value = "taskId") Integer taskId,
                           @PathVariable(value = "id") Integer toDoListId,
                           @RequestBody Task task) {
        taskService.updateTask(task, taskId, toDoListId);
    }

    @DeleteMapping(value = "/{taskId}")
    public void deleteTask(@PathVariable(value = "taskId") Integer taskId,
                           @PathVariable(value = "id") Integer toDoListId) {
        taskService.deleteTask(taskId, toDoListId);
    }

}
