package com.campustrash.config;

import com.campustrash.entity.TaskCategory;
import com.campustrash.repository.TaskCategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategorySeeder implements CommandLineRunner {

    private final TaskCategoryRepository categoryRepository;

    public CategorySeeder(TaskCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) return;

        createCategory("代扔垃圾", "🗑️", true, 1);
        createCategory("代拿快递", "📦", false, 2);
        createCategory("代打饭", "🍜", false, 3);
        createCategory("代占座", "💺", false, 4);
        createCategory("代打印", "🖨️", false, 5);
        createCategory("搬家帮忙", "🏠", true, 6);
        createCategory("其他", "📌", true, 7);
    }

    private void createCategory(String name, String icon, boolean needReview, int sortOrder) {
        TaskCategory category = new TaskCategory();
        category.setId(String.valueOf(System.currentTimeMillis() + sortOrder));
        category.setName(name);
        category.setIcon(icon);
        category.setNeedReview(needReview);
        category.setSortOrder(sortOrder);
        category.setIsCustom(false);
        categoryRepository.save(category);
    }
}
