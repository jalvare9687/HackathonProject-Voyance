package com.example.demo.repository;

import com.example.demo.model.Recipe;
import com.example.demo.model.RecipeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, RecipeId>{
    //find all ingredients for given menu item
    List<Recipe> findById_MenuId(Integer menuId);
}
