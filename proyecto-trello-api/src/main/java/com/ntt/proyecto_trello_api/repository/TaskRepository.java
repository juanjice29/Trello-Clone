package com.ntt.proyecto_trello_api.repository;

import com.ntt.proyecto_trello_api.model.Project;
import com.ntt.proyecto_trello_api.model.Task;
import com.ntt.proyecto_trello_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByProject(Project project);
    
    List<Task> findByProjectAndStatus(Project project, Task.TaskStatus status);
    
    List<Task> findByAssignee(User assignee);
    
    List<Task> findByCreator(User creator);
    
    @Query("SELECT t FROM Task t WHERE t.project = :project ORDER BY t.position ASC")
    List<Task> findByProjectOrderByPosition(@Param("project") Project project);
    
    @Query("SELECT t FROM Task t WHERE t.project = :project AND t.status = :status ORDER BY t.position ASC")
    List<Task> findByProjectAndStatusOrderByPosition(@Param("project") Project project, @Param("status") Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE (t.title LIKE %:search% OR t.description LIKE %:search%) AND t.project = :project")
    List<Task> findBySearchTermAndProject(@Param("search") String search, @Param("project") Project project);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findByDueDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND (t.project.owner = :user OR :user MEMBER OF t.project.members)")
    List<Task> findByProjectIdAndUserAccess(@Param("projectId") Long projectId, @Param("user") User user);
    
    Optional<Task> findByIdAndProject(Long id, Project project);
    
    @Query("SELECT MAX(t.position) FROM Task t WHERE t.project = :project AND t.status = :status")
    Integer findMaxPositionByProjectAndStatus(@Param("project") Project project, @Param("status") Task.TaskStatus status);
}
