package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.CreateNewsRequest;
import com.example.InnerCityBackend.model.dto.request.UpdateNewsRequest;
import com.example.InnerCityBackend.model.dto.response.NewsResponse;
import com.example.InnerCityBackend.model.entity.News;
import com.example.InnerCityBackend.model.entity.User;
import com.example.InnerCityBackend.repository.NewsRepository;
import com.example.InnerCityBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Transactional
    public NewsResponse createNews(CreateNewsRequest request, MultipartFile image, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new BusinessException("Admin user not found"));

        boolean isGlobal = false;
        if (request.getIsGlobal() != null) {
            isGlobal = request.getIsGlobal();
        }

        News news = News.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .categoryId(request.getCategoryId())
                .continent(request.getContinent())
                .country(request.getCountry())
                .isGlobal(isGlobal)
                .createdBy(admin.getId())
                .build();

        // Handle the Image Upload
        if (image != null && !image.isEmpty()) {
            news.setImage_url(processImage(image));
        }



        return mapToResponse(newsRepository.save(news));
    }

    @Transactional
    public NewsResponse updateNews(String id, UpdateNewsRequest request, MultipartFile image) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new BusinessException("News item not found"));

        if (request.getTitle() != null) news.setTitle(request.getTitle());
        if (request.getContent() != null) news.setContent(request.getContent());
        if (request.getCategoryId() != null) news.setCategoryId(request.getCategoryId());
        if (request.getContinent() != null) news.setContinent(request.getContinent());
        if (request.getCountry() != null) news.setCountry(request.getCountry());

        if (request.getIsGlobal() != null) {
            news.setGlobal(request.getIsGlobal());
        } else {
            news.setGlobal(false); // default value
        }

        // Update image only if a new one is uploaded
        if (image != null && !image.isEmpty()) {
            news.setImage_url(processImage(image));
        }

        return mapToResponse(newsRepository.save(news));
    }

    private String processImage(MultipartFile file) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            return "data:" + file.getContentType() + ";base64," + base64Image;
        } catch (IOException e) {
            throw new BusinessException("Failed to upload image file");
        }
    }

    public List<NewsResponse> getAllNews() {
        return newsRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public NewsResponse getNewsById(String id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new BusinessException("News not found"));
        return mapToResponse(news);
    }

    @Transactional
    public void deleteNews(String id) {
        if (!newsRepository.existsById(id)) {
            throw new BusinessException("Cannot delete: News not found");
        }
        newsRepository.deleteById(id);
    }

    private NewsResponse mapToResponse(News news) {
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .image_url(news.getImage_url())
                .categoryId(news.getCategoryId())
                .continent(news.getContinent())
                .country(news.getCountry())
                .isGlobal(news.isGlobal())
                .createdBy(news.getCreatedBy())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .build();
    }
}

