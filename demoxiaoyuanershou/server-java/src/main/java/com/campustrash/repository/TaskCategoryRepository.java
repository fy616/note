package com.campustrash.repository;

import com.campustrash.entity.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TaskCategoryRepository extends JpaRepository<TaskCategory, String> {
    List<TaskCategory> findAllByOrderBySortOrderAsc();
    boolean existsByName(String name);
    Optional<TaskCategory> findByName(String name);
}
