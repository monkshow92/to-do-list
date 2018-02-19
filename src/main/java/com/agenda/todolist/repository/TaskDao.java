package com.agenda.todolist.repository;

import com.agenda.todolist.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskDao extends JpaRepository<Task, Integer> {

    List<Task> findByToDoListId(Integer toDoListId);

    Optional<Task> findByIdAndToDoListId(Integer taskId, Integer toDoListId);

    boolean existsByIdAndToDoListId(Integer taskId, Integer toDoListId);

}
