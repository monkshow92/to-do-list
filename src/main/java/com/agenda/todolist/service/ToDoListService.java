package com.agenda.todolist.service;

import com.agenda.todolist.entity.ToDoList;
import com.agenda.todolist.exception.ToDoListNotFoundException;
import com.agenda.todolist.repository.ToDoListDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
public class ToDoListService {

    private Logger logger = LoggerFactory.getLogger(ToDoListService.class);

    @Autowired
    ToDoListDao toDoListDao;

    public List<ToDoList> getAllToDoLists() {
        logger.debug("Getting all to-do lists");
        return toDoListDao.findAll();
    }

    public ToDoList getToDoListById(Integer id) {
        logger.debug("Getting to-do list with id: " + id);
        return toDoListDao.findById(id)
                .orElseThrow(() -> new ToDoListNotFoundException(id));
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    public ToDoList insertToDoList(ToDoList toDoList) {
        toDoList = toDoListDao.save(toDoList);
        logger.debug("Inserted to-do list with id: " + toDoList.getId());
        return toDoList;
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateToDoList(ToDoList toDoList, Integer id) {
        ToDoList tempToDoList = toDoListDao.findById(id)
                .orElseThrow(() -> new ToDoListNotFoundException(id));
        tempToDoList.setName(toDoList.getName());
        tempToDoList.setDescription(toDoList.getDescription());
        tempToDoList.setDone(toDoList.isDone());
        toDoListDao.save(tempToDoList);
        logger.debug("Updated to-do list with id: " + id);
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteToDoList(Integer id) {
        if (!toDoListDao.exists(id)) {
            throw new ToDoListNotFoundException(id);
        }
        toDoListDao.delete(id);
        logger.debug("Deleted to-do list with id: " + id);
    }

}
