package com.agenda.todolist.repository;

import com.agenda.todolist.entity.ToDoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ToDoListDao extends JpaRepository<ToDoList, Integer> {

    Optional<ToDoList> findById(Integer id);

}
