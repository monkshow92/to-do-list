package com.agenda.todolist;

import com.agenda.todolist.entity.Task;
import com.agenda.todolist.entity.ToDoList;
import com.agenda.todolist.exception.TaskNotFoundException;
import com.agenda.todolist.exception.ToDoListNotFoundException;
import com.agenda.todolist.repository.TaskDao;
import com.agenda.todolist.repository.ToDoListDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RepositoriesTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ToDoListDao toDoListDao;

    @Autowired
    private TaskDao taskDao;

    @Test
    public void whenFindToDoListById_thenReturnToDoList() {
        // given
        ToDoList toDoList = new ToDoList("name", "description", false);
        entityManager.persist(toDoList);
        entityManager.flush();

        // when
        ToDoList found = toDoListDao.findById(toDoList.getId())
                .orElseThrow(() -> new ToDoListNotFoundException(toDoList.getId()));

        // then
        assertThat(found.getName())
                .isEqualTo(toDoList.getName());
        assertThat(found.getDescription())
                .isEqualTo(toDoList.getDescription());
        assertThat(found.isDone()).isFalse();
    }

    @Test
    public void whenFindTaskById_thenReturnTask() {
        // given
        ToDoList toDoList = new ToDoList("name", "description", false);
        entityManager.persist(toDoList);
        entityManager.flush();

        Task task = new Task(toDoList, "task", (short) 1, "TO DO", false, null);
        entityManager.persist(task);
        entityManager.flush();

        // when
        boolean found = taskDao.existsByIdAndToDoListId(task.getId(), toDoList.getId());
        Task taskFound = taskDao.findByIdAndToDoListId(task.getId(), toDoList.getId())
                .orElseThrow(() -> new TaskNotFoundException(task.getId()));

        // then
        assertThat(found).isTrue();
        assertThat(taskFound.getDescription())
                .isEqualTo(task.getDescription());
        assertThat(taskFound.getToDoList().getId())
                .isEqualTo(toDoList.getId());
        assertThat(taskFound.getPriority())
                .isEqualTo(task.getPriority());
        assertThat(taskFound.getStatus())
                .isEqualTo(task.getStatus());
        assertThat(taskFound.getDone()).isFalse();
        assertThat(taskFound.getObservations()).isNull();
    }

}
