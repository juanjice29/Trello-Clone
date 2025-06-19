package com.ntt.proyecto_trello_api.repository;


import com.ntt.proyecto_trello_api.model.Label;
import com.ntt.proyecto_trello_api.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    
    List<Label> findByProject(Project project);
    
    Optional<Label> findByNameAndProject(String name, Project project);
    
    Optional<Label> findByIdAndProject(Long id, Project project);
    
    boolean existsByNameAndProject(String name, Project project);
}
