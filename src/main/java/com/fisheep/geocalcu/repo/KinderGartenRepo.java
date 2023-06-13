package com.fisheep.geocalcu.repo;

import com.fisheep.geocalcu.bean.KinderGarten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface KinderGartenRepo extends JpaRepository<KinderGarten, Integer> {

    @Query(value = "SELECT id, name,address,administrative_code ,longitude,latitude,ST_Distance_Sphere ( POINT ( longitude, latitude ), POINT ( ?1, ?2 ) ) AS distance FROM kindergarten HAVING distance < ?3",nativeQuery = true)
    List<KinderGarten> findByDistance(BigDecimal longitude, BigDecimal latitude, Double distance);

}
