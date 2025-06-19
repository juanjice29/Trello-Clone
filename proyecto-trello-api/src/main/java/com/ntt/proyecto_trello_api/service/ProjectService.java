package com.ntt.proyecto_trello_api.service;


import com.ntt.proyecto_trello_api.model.Project;
import com.ntt.proyecto_trello_api.model.User;
import com.ntt.proyecto_trello_api.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    public List<Project> findAll() {
        return projectRepository.findAll();
    }
    
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }
    
    public List<Project> findByOwner(User owner) {
        return projectRepository.findByOwner(owner);
    }
    
    public List<Project> findByUserInvolvement(User user) {
        return projectRepository.findByUserInvolvement(user);
    }
    
    public List<Project> findByStatusAndUserInvolvement(Project.ProjectStatus status, User user) {
        return projectRepository.findByStatusAndUserInvolvement(status, user);
    }
    
    public Optional<Project> findByIdAndUserInvolvement(Long id, User user) {
        return projectRepository.findByIdAndUserInvolvement(id, user);
    }
    
    public Project save(Project project) {
        return projectRepository.save(project);
    }
    
    public Project update(Project project) {
        return projectRepository.save(project);
    }
    
    public void deleteById(Long id) {
        projectRepository.deleteById(id);
    }
    
    public List<Project> searchProjects(String searchTerm, User user) {
        return projectRepository.findBySearchTermAndUserInvolvement(searchTerm, user);
    }
    
    public boolean hasUserAccess(Long projectId, User user) {
        return projectRepository.findByIdAndUserInvolvement(projectId, user).isPresent();
    }
    
    public boolean isOwner(Long projectId, User user) {
        return projectRepository.findByIdAndOwner(projectId, user).isPresent();
    }
    
    public Project addMember(Project project, User user) {
        project.getMembers().add(user);
        return projectRepository.save(project);
    }
    
    public Project removeMember(Project project, User user) {
        project.getMembers().remove(user);
        return projectRepository.save(project);
    }
}
