package com.arthiq.repository;

import com.arthiq.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    List<Template> findByUserId(Long userId);
}
