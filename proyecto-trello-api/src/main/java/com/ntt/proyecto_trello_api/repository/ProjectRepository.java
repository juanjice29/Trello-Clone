package com.ntt.proyecto_trello_api.repository;


import com.ntt.proyecto_trello_api.model.Project;
import com.ntt.proyecto_trello_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByOwner(User owner);
    
    @Query("SELECT p FROM Project p WHERE p.owner = :user OR :user MEMBER OF p.members")
    List<Project> findByUserInvolvement(@Param("user") User user);
    
    @Query("SELECT p FROM Project p WHERE p.status = :status AND (p.owner = :user OR :user MEMBER OF p.members)")
    List<Project> findByStatusAndUserInvolvement(@Param("status") Project.ProjectStatus status, @Param("user") User user);
    
    @Query("SELECT p FROM Project p WHERE (p.name LIKE %:search% OR p.description LIKE %:search%) AND (p.owner = :user OR :user MEMBER OF p.members)")
    List<Project> findBySearchTermAndUserInvolvement(@Param("search") String search, @Param("user") User user);
    
    Optional<Project> findByIdAndOwner(Long id, User owner);
    
    @Query("SELECT p FROM Project p WHERE p.id = :id AND (p.owner = :user OR :user MEMBER OF p.members)")
    Optional<Project> findByIdAndUserInvolvement(@Param("id") Long id, @Param("user") User user);
}
