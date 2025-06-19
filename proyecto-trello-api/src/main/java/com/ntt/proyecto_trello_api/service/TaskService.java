package com.ntt.proyecto_trello_api.service;

import com.ntt.proyecto_trello_api.model.Label;
import com.ntt.proyecto_trello_api.model.Project;
import com.ntt.proyecto_trello_api.model.Task;
import com.ntt.proyecto_trello_api.model.User;
import com.ntt.proyecto_trello_api.repository.LabelRepository;
import com.ntt.proyecto_trello_api.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private LabelRepository labelRepository;
    
    public List<Task> findAll() {
        return taskRepository.findAll();
    }
    
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }
    
    public List<Task> findByProject(Project project) {
        return taskRepository.findByProject(project);
    }
    
    public List<Task> findByProjectOrderByPosition(Project project) {
        return taskRepository.findByProjectOrderByPosition(project);
    }
    
    public List<Task> findByProjectAndStatus(Project project, Task.TaskStatus status) {
        return taskRepository.findByProjectAndStatus(project, status);
    }
    
    public List<Task> findByProjectAndStatusOrderByPosition(Project project, Task.TaskStatus status) {
        return taskRepository.findByProjectAndStatusOrderByPosition(project, status);
    }
    
    public List<Task> findByAssignee(User assignee) {
        return taskRepository.findByAssignee(assignee);
    }
    
    public List<Task> findByCreator(User creator) {
        return taskRepository.findByCreator(creator);
    }
    
    public List<Task> findByProjectIdAndUserAccess(Long projectId, User user) {
        return taskRepository.findByProjectIdAndUserAccess(projectId, user);
    }
    
    public Task save(Task task) {
        // Set position if not provided
        if (task.getPosition() == null || task.getPosition() == 0) {
            Integer maxPosition = taskRepository.findMaxPositionByProjectAndStatus(task.getProject(), task.getStatus());
            task.setPosition(maxPosition != null ? maxPosition + 1 : 1);
        }
        return taskRepository.save(task);
    }
    
    public Task update(Task task) {
        return taskRepository.save(task);
    }
    
    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }
    
    public List<Task> searchTasks(String searchTerm, Project project) {
        return taskRepository.findBySearchTermAndProject(searchTerm, project);
    }
    
    @Transactional
    public Task moveTask(Long taskId, Task.TaskStatus newStatus, Integer newPosition) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            Task.TaskStatus oldStatus = task.getStatus();
            
            task.setStatus(newStatus);
            task.setPosition(newPosition);
            
            return taskRepository.save(task);
        }
        throw new RuntimeException("Task not found");
    }
    
    @Transactional
    public Task updateTaskLabels(Long taskId, Set<Long> labelIds) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.getLabels().clear();
            
            if (labelIds != null && !labelIds.isEmpty()) {
                List<Label> labels = labelRepository.findAllById(labelIds);
                task.getLabels().addAll(labels);
            }
            
            return taskRepository.save(task);
        }
        throw new RuntimeException("Task not found");
    }
}
