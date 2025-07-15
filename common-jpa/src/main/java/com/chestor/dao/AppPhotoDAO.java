package com.chestor.dao;

import com.chestor.entity.AppDocument;
import com.chestor.entity.AppPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}