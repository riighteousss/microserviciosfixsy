package com.fixsy.imagenes.repository;

import com.fixsy.imagenes.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUserId(Long userId);
    List<Image> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<Image> findByUserIdAndEntityType(Long userId, String entityType);
    void deleteByEntityTypeAndEntityId(String entityType, Long entityId);
}

