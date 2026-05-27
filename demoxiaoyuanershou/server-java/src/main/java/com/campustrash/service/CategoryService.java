package com.campustrash.service;

import com.campustrash.dto.CategoryRequest;
import com.campustrash.entity.TaskCategory;
import com.campustrash.repository.TaskCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final TaskCategoryRepository categoryRepository;

    public List<TaskCategory> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    public TaskCategory createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("分类名称已存在");
        }

        TaskCategory category = new TaskCategory();
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setNeedReview(request.getNeedReview());
        category.setSortOrder(request.getSortOrder());
        category.setIsCustom(true);

        return categoryRepository.save(category);
    }

    public TaskCategory updateCategory(String id, CategoryRequest request) {
        TaskCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setNeedReview(request.getNeedReview());
        category.setSortOrder(request.getSortOrder());

        return categoryRepository.save(category);
    }

    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }
}
