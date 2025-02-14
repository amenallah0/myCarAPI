package com.myCar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myCar.domain.Expert;
import com.myCar.repository.ExpertRepository;
import com.myCar.exception.ResourceNotFoundException;
import java.util.List;

@Service
@Transactional
public class ExpertService {
    
    private final ExpertRepository expertRepository;
    
    @Autowired
    public ExpertService(ExpertRepository expertRepository) {
        this.expertRepository = expertRepository;
    }
    
    public Expert createExpert(Expert expert) {
        return expertRepository.save(expert);
    }
    
    public Expert updateExpert(Long id, Expert expert) {
        Expert existingExpert = expertRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Expert non trouvé avec l'id : " + id));
            
        existingExpert.setNom(expert.getNom());
        existingExpert.setPrenom(expert.getPrenom());
        existingExpert.setSpecialite(expert.getSpecialite());
        existingExpert.setEmail(expert.getEmail());
        existingExpert.setTelephone(expert.getTelephone());
        
        return expertRepository.save(existingExpert);
    }
    
    public void deleteExpert(Long id) {
        Expert expert = expertRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Expert non trouvé avec l'id : " + id));
        expertRepository.delete(expert);
    }
    
    public Expert getExpert(Long id) {
        return expertRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Expert non trouvé avec l'id : " + id));
    }
    
    public List<Expert> getAllExperts() {
        return expertRepository.findAll();
    }
    
    public List<Expert> getExpertsBySpecialite(String specialite) {
        return expertRepository.findBySpecialite(specialite);
    }
} 