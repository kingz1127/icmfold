//package com.example.InnerCityBackend.controller;
//
//import com.example.InnerCityBackend.model.dto.request.CreateNewsRequest;
//import com.example.InnerCityBackend.model.dto.request.UpdateNewsRequest;
//import com.example.InnerCityBackend.model.dto.response.NewsResponse;
//import com.example.InnerCityBackend.model.dto.response.SuccessResponse;
//import com.example.InnerCityBackend.service.NewsService;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.security.Principal;
//import java.util.List;
//
//@RestController
//@RequestMapping("news")
//@RequiredArgsConstructor
//@Tag(name = "News Controller", description = "Admin C,R,U,D and Users view")
//
//public class NewsController {
//
//    private final NewsService newsService;
//
//
//    @PostMapping(consumes = {"multipart/form-data"})
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<NewsResponse> createNews(
//            Principal principal,
//            @RequestParam("title") String title,
//            @RequestParam("content") String content,
//            @RequestParam("categoryId") String categoryId,
//            @RequestParam(value = "continent", required = false) String continent,
//            @RequestParam(value = "country", required = false) String country,
//            @RequestParam(value = "isGlobal", defaultValue = "false") boolean isGlobal,
//            @RequestPart(value = "image", required = false) MultipartFile image) {
//
//        CreateNewsRequest request = CreateNewsRequest.builder()
//                .title(title)
//                .content(content)
//                .categoryId(categoryId)
//                .continent(continent)
//                .country(country)
//                .isGlobal(isGlobal)
//                .build();
//
//        return ResponseEntity.ok(newsService.createNews(request, image, principal.getName()));
//    }
//
//
//    @PatchMapping(value = "/{id}", consumes = {"multipart/form-data"})
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<NewsResponse> updateNews(
//            @PathVariable String id,
//            @RequestParam(value = "title", required = false) String title,
//            @RequestParam(value = "content", required = false) String content,
//            @RequestParam(value = "categoryId", required = false) String categoryId,
//            @RequestParam(value = "continent", required = false) String continent,
//            @RequestParam(value = "country", required = false) String country,
//            @RequestParam(value = "isGlobal", required = false) Boolean isGlobal,
//            @RequestPart(value = "image", required = false) MultipartFile image) {
//
//        UpdateNewsRequest request = UpdateNewsRequest.builder()
//                .title(title)
//                .content(content)
//                .categoryId(categoryId)
//                .continent(continent)
//                .country(country)
//                .isGlobal(isGlobal)
//                .build();
//
//        return ResponseEntity.ok(newsService.updateNews(id, request, image));
//    }
//
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<SuccessResponse> deleteNews(@PathVariable String id) {
//        newsService.deleteNews(id);
//        return ResponseEntity.ok(new SuccessResponse("News deleted successfully"));
//    }
//
//
//    @GetMapping
//    public ResponseEntity<List<NewsResponse>> getAllNews() {
//        return ResponseEntity.ok(newsService.getAllNews());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<NewsResponse> getNewsById(@PathVariable String id) {
//        return ResponseEntity.ok(newsService.getNewsById(id));
//    }
//}



package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.CreateNewsRequest;
import com.example.InnerCityBackend.model.dto.request.UpdateNewsRequest;
import com.example.InnerCityBackend.model.dto.response.NewsResponse;
import com.example.InnerCityBackend.model.dto.response.SuccessResponse;
import com.example.InnerCityBackend.service.NewsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    // For JSON requests (no image)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsResponse> createNewsJson(
            Principal principal,
            @Valid @RequestBody CreateNewsRequest request) {
        return ResponseEntity.ok(newsService.createNews(request, null, principal.getName()));
    }

    // For multipart requests (with image)
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsResponse> createNewsMultipart(
            Principal principal,
            @RequestPart("data") @Valid CreateNewsRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(newsService.createNews(request, image, principal.getName()));
    }

    // For JSON updates (no image)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsResponse> updateNewsJson(
            @PathVariable String id,
            @Valid @RequestBody UpdateNewsRequest request) {
        return ResponseEntity.ok(newsService.updateNews(id, request, null));
    }

    // For multipart updates (with image)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsResponse> updateNewsMultipart(
            @PathVariable String id,
            @RequestPart("data") @Valid UpdateNewsRequest request,
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