package com.campustrash.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String icon;
    private Boolean needReview = true;
    private Integer sortOrder = 0;
}
