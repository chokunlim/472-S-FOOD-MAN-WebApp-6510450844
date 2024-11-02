package ku.cs.restaurant.controller;

import ku.cs.restaurant.dto.ApiResponse;
import ku.cs.restaurant.dto.recipe.UpdateQtyRequest;
import ku.cs.restaurant.entity.Recipe;
import ku.cs.restaurant.entity.RecipeKey;
import ku.cs.restaurant.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // Get all recipes
    @GetMapping("/recipe")
    public ResponseEntity<ApiResponse<List<Recipe>>> getAllRecipes() {
        try {
            List<Recipe> recipes = recipeService.getAllRecipes();
            return ResponseEntity.ok(new ApiResponse<>(true, "Recipes retrieved successfully.", recipes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Failed to retrieve recipes.", null));
        }
    }

    // Get recipe by ID using request body
    @GetMapping("/recipe/id")
    public ResponseEntity<ApiResponse<Recipe>> getRecipeById(@RequestBody RecipeKey id) {
        try {
            Optional<Recipe> recipe = recipeService.getRecipeById(id);
            return recipe.map(r -> ResponseEntity.ok(new ApiResponse<>(true, "Recipe retrieved successfully.", r)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Recipe not found.", null)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Failed to retrieve recipe.", null));
        }
    }

    // Update a recipe using request body
    @PatchMapping("/recipe")
    public ResponseEntity<ApiResponse<Recipe>> updateRecipe(@RequestBody UpdateQtyRequest updateQtyRequest) {
        try {
            Optional<Recipe> updatedRecipe = recipeService.updateRecipe(updateQtyRequest.getId(), updateQtyRequest.getQty());
            return updatedRecipe.map(recipe -> ResponseEntity.ok(new ApiResponse<>(true, "Recipe updated successfully.", recipe)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Recipe not found.", null)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Failed to update recipe.", null));
        }
    }

    // Delete a recipe using request body
    @DeleteMapping("/recipe/delete")
    public ResponseEntity<ApiResponse<Void>> deleteRecipe(@RequestBody RecipeKey id) {
        try {
            recipeService.deleteRecipe(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Recipe deleted successfully.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Recipe not found.", null));
        }
    }
}
