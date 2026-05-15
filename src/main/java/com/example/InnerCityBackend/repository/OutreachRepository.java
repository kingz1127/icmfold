package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.Outreach;
import org.springframework.data.domain.Page; // Ensure this is imported
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutreachRepository extends JpaRepository<Outreach, String>, JpaSpecificationExecutor<Outreach> {

    // These are standard query methods
    boolean existsBySubcategoryId(String subcategoryId);
    long countBySubcategoryId(String subcategoryId);
    List<Outreach> findBySubcategoryId(String subcategoryId);

    // Nearby: uses Haversine formula to find outreaches within X km
    // Note: Ensure table name matches your @Table annotation (usually "outreaches")
    @Query(value = """
        SELECT * FROM outreaches
        WHERE (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(latitude)) *
                cos(radians(longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(latitude))
            )
        ) <= :radiusKm
        AND approval_status = 'APPROVED'
        ORDER BY (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(latitude)) *
                cos(radians(longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(latitude))
            )
        ) ASC
        """, nativeQuery = true)
    List<Outreach> findNearby(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusKm") double radiusKm
    );

    // Map bounding box (Useful for the React Native map view)
    @Query(value = """
        SELECT * FROM outreaches
        WHERE latitude BETWEEN :southLat AND :northLat
        AND longitude BETWEEN :westLng AND :eastLng
        AND approval_status = 'APPROVED'
        """, nativeQuery = true)
    List<Outreach> findWithinBoundingBox(
            @Param("southLat") double southLat,
            @Param("northLat") double northLat,
            @Param("westLng") double westLng,
            @Param("eastLng") double eastLng
    );

    // DELETE the manual findAll method that was here.
    // JpaSpecificationExecutor already provides:
    // Page<Outreach> findAll(Specification<Outreach> spec, Pageable pageable);
}