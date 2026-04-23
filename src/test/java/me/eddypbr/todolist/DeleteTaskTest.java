package me.eddypbr.todolist;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import me.eddypbr.todolist.task.*;
import me.eddypbr.todolist.user.UserModel;

public class DeleteTaskTest {

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
    @ValueSource(strings = {
        "123e4567-e89b-12d3-a456-426614174000",
        "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    })
    public void testDeleteTodoSuccess(String taskIdStr) throws Exception {
        UUID id = UUID.fromString(taskIdStr);
        TaskModel task = mockTask();
        task.setId(id);
        when(request.getAttribute("user")).thenReturn(mockUser());
        when(repo.findById(id)).thenReturn(Optional.of(task));
        doNothing().when(repo).deleteById(id);
        ResponseEntity response = controller.delete(request, id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(repo, times(1)).deleteById(id);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "00000000-0000-0000-0000-000000000001",
        "ffffffff-ffff-ffff-ffff-ffffffffffff"
    })
    public void testDeleteTodoNotFound(String idStr) throws Exception {
        UUID id = UUID.fromString(idStr);
        when(repo.findById(id)).thenReturn(Optional.empty());
        ResponseEntity response = controller.delete(request, id);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(repo, never()).deleteById(any());
    }
}