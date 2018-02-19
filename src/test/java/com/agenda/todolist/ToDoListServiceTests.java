package com.agenda.todolist;

import com.agenda.todolist.entity.Task;
import com.agenda.todolist.entity.ToDoList;
import com.agenda.todolist.exception.TaskNotFoundException;
import com.agenda.todolist.exception.ToDoListNotFoundException;
import com.agenda.todolist.repository.TaskDao;
import com.agenda.todolist.repository.ToDoListDao;
import com.agenda.todolist.service.TaskService;
import com.agenda.todolist.service.ToDoListService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ToDoListServiceTests {

	@TestConfiguration
	static class ToDoListServiceTestContextConfiguration {
		@Bean
		public ToDoListService toDoListService() {
			return new ToDoListService();
		}
		@Bean
		public TaskService taskService() {
			return new TaskService();
		}
	}

	@Autowired
	private ToDoListService toDoListService;

	@Autowired
	private TaskService taskService;

	@MockBean
	private TaskDao taskDao;

	@MockBean
	private ToDoListDao toDoListDao;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private ToDoList newToDoList = new ToDoList("newToDoList", "New to-do list", false);

	private Task newTask = new Task(newToDoList, "New Task", (short) 1, "TO DO", false, null);

	@Before
	public void setUpToDoList() {
		// getToDoListById
		ToDoList toDoList = new ToDoList("toDoList", "To-do List", false);
		toDoList.setId(6);
		Mockito.when(toDoListDao.findById(toDoList.getId()))
				.thenReturn(Optional.of(toDoList));

		// getAllToDoLists
		List<ToDoList> toDoLists = IntStream.range(7, 11)
				.mapToObj(n -> new ToDoList() {
					{
						setId(n);
						setName("toDoList" + n);
						setDescription("To-do List #" + n);
						setDone(false);
					}
				}).collect(Collectors.toList());
		Mockito.when(toDoListDao.findAll())
				.thenReturn(toDoLists);

		// insertToDoList
		Mockito.when(toDoListDao.save(newToDoList))
				.thenReturn(newToDoList);
		Mockito.when(toDoListDao.findById(11))
				.thenReturn(Optional.of(newToDoList));

		// deleteToDoList
		int idToDelete = 12;
		Mockito.doNothing().when(toDoListDao).delete(idToDelete);
		Mockito.when(toDoListDao.findById(idToDelete))
				.thenThrow(new ToDoListNotFoundException(idToDelete));
		Mockito.when(toDoListDao.exists(idToDelete))
				.thenReturn(true);
	}

	@Test
	public void whenValidId_thenToDoListShouldBeFound() {
		// when
		ToDoList toDoList = toDoListService.getToDoListById(6);

		// then
		assertThatToDoListIsEqual(toDoList, "toDoList", "To-do List", false);
	}

	@Test
	public void whenFindAllToDoLists_thenAllToDoListsShouldBeFound() {
		// when
		List<ToDoList> toDoLists = toDoListService.getAllToDoLists();

		// then
		assertThat(toDoLists.size())
				.isEqualTo(4);
		assertThat(toDoLists.stream()
				.allMatch(tdl -> tdl.getName().equals("toDoList" + tdl.getId()) &&
						  	tdl.getDescription().equals("To-do List #" + tdl.getId()) &&
							!tdl.isDone() && tdl.getId() >= 7 && tdl.getId() <= 10)
		).isTrue();
	}

	@Test
	public void whenInsertToDoList_thenToDoListShouldBeCreated() {
		// when
		ToDoList insertedToDoList = toDoListService.insertToDoList(newToDoList);

		// then
		assertThatToDoListsAreEqual(insertedToDoList, newToDoList);
	}

	@Test
	public void whenUpdateToDoList_thenToDoListShouldBeUpdated() {
		// given
		newToDoList.setName("updatedToDoList");
		newToDoList.setDescription("Updated to-do list");
		newToDoList.setDone(true);

		// when
		toDoListService.updateToDoList(newToDoList, 11);

		// then
		ToDoList updatedToDoList = toDoListService.getToDoListById(11);
		assertThatToDoListIsEqual(updatedToDoList, "updatedToDoList", "Updated to-do list", true);
	}

	@Test
	public void whenDeleteUpdateToDoList_thenToDoListShouldBeDeleted() throws ToDoListNotFoundException {
		// expected Exception
		exception.expect(ToDoListNotFoundException.class);
		exception.expectMessage("To-do list with id: 12 not found");

		// when
		toDoListService.deleteToDoList(12);

		// then
		ToDoList deletedToDoList = toDoListService.getToDoListById(12);
		assertThat(deletedToDoList)
				.isNull();
	}

	// private HELPER methods

	private void assertThatToDoListsAreEqual(ToDoList toDoList1, ToDoList toDoList2) {
		assertThat(toDoList1.getName()).isEqualTo(toDoList2.getName());
		assertThat(toDoList1.getDescription()).isEqualTo(toDoList2.getDescription());
		assertThat(toDoList1.isDone()).isEqualTo(toDoList2.isDone());
	}

	private void assertThatToDoListIsEqual(ToDoList toDoList, String name, String description, boolean done) {
		assertThat(toDoList.getName()).isEqualTo(name);
		assertThat(toDoList.getDescription()).isEqualTo(description);
		assertThat(toDoList.isDone()).isEqualTo(done);
	}

	// *******************************************************************************************************


	@Before
	public void setUpTask() {
		ToDoList toDoList = new ToDoList("toDoList", "To-do List", false);
		toDoList.setId(6);
		Mockito.when(toDoListDao.exists(toDoList.getId())).thenReturn(true);

		// get all tasks by to-do list id
		List<Task> tasks = IntStream.range(16, 21)
				.mapToObj(n -> new Task(){
					{
						setId(n);
						setDescription("Task #" + n);
						setPriority((short) 1);
						setStatus("TO DO");
						setDone(false);
						setToDoList(toDoList);
					}
				}).collect(Collectors.toList());
		Mockito.when(taskDao.findByToDoListId(toDoList.getId()))
				.thenReturn(tasks);

		// get task by id
		Mockito.when(taskDao.findByIdAndToDoListId(16, 6))
				.thenReturn(Optional.of(tasks.get(0)));

		// insert task
		Mockito.when(taskDao.save(newTask))
				.thenReturn(newTask);

		// deleteTask
		int idToDelete = 17;
		Mockito.doNothing().when(taskDao).delete(idToDelete);
		Mockito.when(taskDao.findByIdAndToDoListId(idToDelete, 6))
				.thenThrow(new TaskNotFoundException(idToDelete));
		Mockito.when(taskDao.exists(idToDelete))
				.thenReturn(true);
	}

	@Test
	public void whenValidId_thenFindAllTasks() {
		// when
		List<Task> tasks = taskService.getAllTasksByToDoListId(6);

		// then
		assertThat(tasks.size()).isEqualTo(5);
		assertThat(tasks.stream()
				.allMatch(t -> t.getDescription().equals("Task #" + t.getId()) &&
						t.getStatus().equals("TO DO") &&
						t.getPriority().equals((short) 1) &&
						!t.getDone()
				)
		).isTrue();
	}

	@Test
	public void whenValidTaskId_thenFindTask() {
		// when
		Task task = taskService.getTaskById(16, 6);

		// then
		assertThat(task.getId()).isEqualTo(16);
		assertThat(task.getDescription()).isEqualTo("Task #16");
		assertThat(task.getStatus()).isEqualTo("TO DO");
		assertThat(task.getPriority()).isEqualTo((short) 1);
		assertThat(task.getDone()).isFalse();
	}

	@Test
	public void whenInsertTask_thenTaskShouldBeInserted() {
		// when
		Task task = taskService.insertTask(newTask, 6);

		// then
		assertThat(task.getDescription()).isEqualTo("New Task");
	}



	@Test
	public void whenUpdateTask_thenTaskShouldBeUpdated() {
		// given
		newTask.setDescription("Updated task");
		newTask.setStatus("DONE");
		newTask.setDone(true);

		// when
		taskService.updateTask(newTask, 16, 6);

		// then
		Task updatedTask = taskService.getTaskById(16, 6);
		assertThat(updatedTask.getDescription()).isEqualTo("Updated task");
		assertThat(updatedTask.getStatus()).isEqualTo("DONE");
		assertThat(updatedTask.getDone()).isTrue();
	}

	@Test
	public void whenDeleteTask_thenTaskShouldBeDeleted() throws TaskNotFoundException {
		// expected Exception
		exception.expect(TaskNotFoundException.class);
		exception.expectMessage("Task with id: 17 not found");

		// when
		taskService.deleteTask(17, 6);

		// then
		Task deletedTask = taskService.getTaskById(17, 6);
		assertThat(deletedTask)
				.isNull();
	}

}
