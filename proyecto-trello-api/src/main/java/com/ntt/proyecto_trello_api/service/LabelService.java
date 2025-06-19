package com.ntt.proyecto_trello_api.service;


import com.ntt.proyecto_trello_api.model.Label;
import com.ntt.proyecto_trello_api.model.Project;
import com.ntt.proyecto_trello_api.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LabelService {
    
    @Autowired
    private LabelRepository labelRepository;
    
    public List<Label> findAll() {
        return labelRepository.findAll();
    }
    
    public Optional<Label> findById(Long id) {
        return labelRepository.findById(id);
    }
    
    public List<Label> findByProject(Project project) {
        return labelRepository.findByProject(project);
    }
    
    public Optional<Label> findByNameAndProject(String name, Project project) {
        return labelRepository.findByNameAndProject(name, project);
    }
    
    public Optional<Label> findByIdAndProject(Long id, Project project) {
        return labelRepository.findByIdAndProject(id, project);
    }
    
    public boolean existsByNameAndProject(String name, Project project) {
        return labelRepository.existsByNameAndProject(name, project);
    }
    
    public Label save(Label label) {
        return labelRepository.save(label);
    }
    
    public Label update(Label label) {
        return labelRepository.save(label);
    }
    
    public void deleteById(Long id) {
        labelRepository.deleteById(id);
    }
}
