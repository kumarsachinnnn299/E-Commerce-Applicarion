package com.ecommerce.project.controllers;


import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.payload.CategoryDTO;
import com.ecommerce.project.model.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")//this part will be common in all endpoints
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //Test endpoint for understanding how to use @RequestParan

    @RequestMapping(value = "/public/echo",method = RequestMethod.GET)
    public  ResponseEntity<String> echoMessage(@RequestParam(name="message", defaultValue = "Hello World!!!")String message )
    {
        return new ResponseEntity<>("Echoed Message: "+message,HttpStatus.OK);
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber")Integer pageNumber,
            @RequestParam(name = "pageSize")Integer pageSize
    )
    {
        CategoryResponse categoryResponse =categoryService.getAllCategories(pageNumber,pageSize);
        return new ResponseEntity<>(categoryResponse,HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO)//@Valid ensures Requestbody contains valid data
    {
        CategoryDTO savedCategoryDTO=categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
            CategoryDTO deletedCategoryDTO=categoryService.deleteCategory(categoryId);
            //3 ways to write
            return new ResponseEntity<>(deletedCategoryDTO, HttpStatus.OK);

    }


//    @PutMapping("/api/admin/categories/{categoryId}") this or RequestMapping are equivalent
    @RequestMapping(value = "/admin/categories/{categoryId}",method = RequestMethod.PUT)
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable Long categoryId)
    {
                CategoryDTO updatedCategoryDTO=categoryService.updateCategory(categoryDTO,categoryId);
                return new ResponseEntity<>(updatedCategoryDTO, HttpStatus.OK);
    }
}
