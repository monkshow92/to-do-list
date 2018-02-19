package com.agenda.todolist.service;

import com.agenda.todolist.entity.Task;
import com.agenda.todolist.entity.ToDoList;
import com.agenda.todolist.exception.TaskNotFoundException;
import com.agenda.todolist.exception.ToDoListNotFoundException;
import com.agenda.todolist.repository.TaskDao;
import com.agenda.todolist.repository.ToDoListDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.function.Predicate;

@Service
public class TaskService {

    private Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    TaskDao taskDao;

    @Autowired
    ToDoListDao toDoListDao;

    public List<Task> getAllTasksByToDoListId(Integer toDoListId) {
        logger.debug("Getting all tasks from to-do list with id: " + toDoListId);
        if (!toDoListDao.exists(toDoListId)) {
            throw new ToDoListNotFoundException(toDoListId);
        }
        return taskDao.findByToDoListId(toDoListId);
    }

    public Task getTaskById(Integer taskId, Integer toDoListId) {
        logger.debug("Getting task with id: " + taskId);
        return taskDao.findByIdAndToDoListId(taskId, toDoListId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    public Task insertTask(Task task, Integer toDoListId) {
        ToDoList toDoList = toDoListDao.findById(toDoListId)
                .orElseThrow(() -> new ToDoListNotFoundException(toDoListId));
        task.setToDoList(toDoList);
        if (task.getPriority() == null) {
            task.setPriority((short) 1);
        }
        if (task.getStatus() == null) {
            task.setStatus("TO DO");
        }
        if (task.getDone() == null) {
            task.setDone(Boolean.FALSE);
        }
        task = taskDao.save(task);
        logger.debug("Inserted task with id: " + task.getId());

        updateToDoListDone(toDoListId);
        return task;
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateTask(Task task, Integer taskId, Integer toDoListId) {
        Task tempTask = taskDao.findByIdAndToDoListId(taskId, toDoListId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        tempTask.setDescription(task.getDescription());
        tempTask.setDone(task.getDone());
        tempTask.setObservations(task.getObservations());
        tempTask.setPriority(task.getPriority());
        tempTask.setStatus(task.getStatus());
        taskDao.save(tempTask);
        logger.debug("Updated task with id: " + taskId);

        updateToDoListDone(toDoListId);
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteTask(Integer taskId, Integer toDoListId) {
        if (!taskDao.existsByIdAndToDoListId(taskId, toDoListId)) {
            throw new TaskNotFoundException(taskId);
        }
        taskDao.delete(taskId);
        logger.debug("Deleted task with id: " + taskId);

        updateToDoListDone(toDoListId);
    }

    private void updateToDoListDone(Integer toDoListId) {
        ToDoList toDoList = toDoListDao.findById(toDoListId)
                .orElseThrow(() -> new ToDoListNotFoundException(toDoListId));
        List<Task> tasks = taskDao.findByToDoListId(toDoListId);
        Predicate<Task> isDoneTask = Task::getDone;
        Predicate<Task> statusEqualsDone = (task) -> task.getStatus().equals("DONE");
        boolean isDoneToDoList = tasks.stream()
                .allMatch(isDoneTask.and(statusEqualsDone));
        toDoList.setDone(isDoneToDoList);
        toDoListDao.save(toDoList);
        logger.debug("Updated status of to-do list with id: " + toDoList + " to: " + isDoneToDoList);
    }

}
