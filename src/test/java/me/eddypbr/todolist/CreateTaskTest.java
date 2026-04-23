package me.eddypbr.todolist;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import me.eddypbr.todolist.task.*;
import me.eddypbr.todolist.user.UserModel;

public class CreateTaskTest {

    @InjectMocks
    private TaskController controller;

    @Mock
    private ITaskRepository repo;

    @Mock
    private HttpServletRequest request;

    private UUID userId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
    }
    
    private UserModel mockUser() {
        UserModel user = new UserModel();
        user.setId(userId);
        return user;
    }

    private TaskModel mockTask() {
        TaskModel task = new TaskModel();
        task.setIdUser(userId);
        return task;
    }

    @ParameterizedTest
    @CsvSource({
        "Buy groceries, Milk and eggs, 1, 2",
        "Study Java, Complete assignment, 2, 5",
    })
    public void testCreateTodoSuccess(String title, String description, int startDays, int endDays) throws Exception {
        when(request.getAttribute("user")).thenReturn(mockUser());
        TaskModel newTask = new TaskModel();
        newTask.setTitle(title);
        newTask.setDescription(description);
        newTask.setStartAt(LocalDateTime.now().plusDays(startDays));
        newTask.setEndAt(LocalDateTime.now().plusDays(endDays));
        when(repo.save(any())).thenReturn(mockTask());
        ResponseEntity response = controller.create(newTask, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(repo, times(1)).save(any());
    }

    @ParameterizedTest
    @CsvSource({
        "5, 1",   
        "10, 3", 
    })
    public void testCreateTodoWithInvalidDates_StartAfterEnd(int startDays, int endDays) throws Exception {
        when(request.getAttribute("user")).thenReturn(mockUser());
        TaskModel newTask = new TaskModel();
        newTask.setTitle("Test");
        newTask.setStartAt(LocalDateTime.now().plusDays(startDays));
        newTask.setEndAt(LocalDateTime.now().plusDays(endDays));
        ResponseEntity response = controller.create(newTask, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(repo, never()).save(any());
    }
}