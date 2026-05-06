package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.CreateNewsRequest;
import com.example.InnerCityBackend.model.dto.request.UpdateNewsRequest;
import com.example.InnerCityBackend.model.dto.response.NewsResponse;
import com.example.InnerCityBackend.model.dto.response.SuccessResponse;
import com.example.InnerCityBackend.service.NewsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("news")
@RequiredArgsConstructor
@Tag(name = "News Controller", description = "Admin C,R,U,D and Users view")

public class NewsController {

    private final NewsService newsService;


    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsResponse> createNews(
            Principal principal,
            @RequestPart("data") @Valid CreateNewsRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return ResponseEntity.ok(newsService.createNews(request, image, principal.getName()));
    }


    @PatchMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsResponse> updateNews(
            @PathVariable String id,
            @RequestPart("data") UpdateNewsRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return ResponseEntity.ok(newsService.updateNews(id, request, image));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> deleteNews(@PathVariable String id) {
        newsService.deleteNews(id);
        return ResponseEntity.ok(new SuccessResponse("News deleted successfully"));
    }


    @GetMapping
    public ResponseEntity<List<NewsResponse>> getAllNews() {
        return ResponseEntity.ok(newsService.getAllNews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable String id) {
        return ResponseEntity.ok(newsService.getNewsById(id));
    }
}