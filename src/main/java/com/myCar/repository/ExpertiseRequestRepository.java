package com.myCar.repository;

import com.myCar.domain.ExpertiseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Repository
public interface ExpertiseRequestRepository extends JpaRepository<ExpertiseRequest, Long> {
    Logger logger = LoggerFactory.getLogger(ExpertiseRequestRepository.class);

    @Query("SELECT DISTINCT er FROM ExpertiseRequest er " +
           "LEFT JOIN FETCH er.expert e " +
           "LEFT JOIN FETCH er.user u " +
           "LEFT JOIN FETCH er.car c " +
           "LEFT JOIN FETCH er.report r " +
           "WHERE er.expert.id = :expertId " +
           "ORDER BY er.requestDate DESC")
    List<ExpertiseRequest> findByExpertId(Long expertId);

    @Query("SELECT DISTINCT er FROM ExpertiseRequest er " +
           "LEFT JOIN FETCH er.expert e " +
           "LEFT JOIN FETCH er.user u " +
           "LEFT JOIN FETCH er.car c " +
           "LEFT JOIN FETCH er.report r " +
           "WHERE er.user.id = :userId " +
           "ORDER BY er.requestDate DESC")
    default List<ExpertiseRequest> findByUserId(@Param("userId") Long userId) {
        logger.info("Executing findByUserId query for userId: {}", userId);
        List<ExpertiseRequest> results = findByUserIdInternal(userId);
        logger.info("Found {} requests for userId: {}", results.size(), userId);
        return results;
    }

    @Query("SELECT DISTINCT er FROM ExpertiseRequest er " +
           "LEFT JOIN FETCH er.expert e " +
           "LEFT JOIN FETCH er.user u " +
           "LEFT JOIN FETCH er.car c " +
           "LEFT JOIN FETCH er.report r " +
           "WHERE er.user.id = :userId " +
           "ORDER BY er.requestDate DESC")
    List<ExpertiseRequest> findByUserIdInternal(@Param("userId") Long userId);

    List<ExpertiseRequest> findByCarId(Long carId);

    @Query("SELECT COUNT(e) FROM ExpertiseRequest e WHERE e.status = :status")
    default Long countByStatus(@Param("status") ExpertiseRequest.RequestStatus status) {
        logger.info("Executing countByStatus query for status: {}", status);
        Long count = countByStatusInternal(status);
        logger.info("Count for status {}: {}", status, count);
        return count;
    }

    @Query("SELECT COUNT(e) FROM ExpertiseRequest e WHERE e.status = :status")
    Long countByStatusInternal(@Param("status") ExpertiseRequest.RequestStatus status);

    @Query("SELECT COUNT(e) FROM ExpertiseRequest e WHERE e.user.id = :userId")
    default Long countByUserId(@Param("userId") Long userId) {
        logger.info("Executing countByUserId query for userId: {}", userId);
        Long count = countByUserIdInternal(userId);
        logger.info("Count for userId {}: {}", userId, count);
        return count;
    }

    @Query("SELECT COUNT(e) FROM ExpertiseRequest e WHERE e.user.id = :userId")
    Long countByUserIdInternal(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) FROM ExpertiseRequest e WHERE e.user.id = :userId AND e.status = :status")
    default Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ExpertiseRequest.RequestStatus status) {
        logger.info("Executing countByUserIdAndStatus query for userId: {} and status: {}", userId, status);
        Long count = countByUserIdAndStatusInternal(userId, status);
        logger.info("Count for userId {} and status {}: {}", userId, status, count);
        return count;
    }

    @Query("SELECT COUNT(e) FROM ExpertiseRequest e WHERE e.user.id = :userId AND e.status = :status")
    Long countByUserIdAndStatusInternal(@Param("userId") Long userId, @Param("status") ExpertiseRequest.RequestStatus status);

    @Query("SELECT e.status, COUNT(e) FROM ExpertiseRequest e WHERE e.user.id = :userId GROUP BY e.status")
    default List<Object[]> getStatusCountsByUserId(@Param("userId") Long userId) {
        logger.info("Executing getStatusCountsByUserId query for userId: {}", userId);
        List<Object[]> results = getStatusCountsByUserIdInternal(userId);
        logger.info("Found {} status counts for userId: {}", results.size(), userId);
        return results;
    }

    @Query("SELECT e.status, COUNT(e) FROM ExpertiseRequest e WHERE e.user.id = :userId GROUP BY e.status")
    List<Object[]> getStatusCountsByUserIdInternal(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM expertise_request WHERE user_id = :userId", nativeQuery = true)
    List<Object[]> findRawRequestsByUserId(@Param("userId") Long userId);

    @Query("SELECT er FROM ExpertiseRequest er WHERE er.user.id = :userId")
    List<ExpertiseRequest> findAllRequestsForUserDebug(@Param("userId") Long userId);

    @Query("SELECT COUNT(er) FROM ExpertiseRequest er WHERE er.expert.id = :expertId")
    Long countByExpertId(Long expertId);
} 