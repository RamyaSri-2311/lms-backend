package com.learnvault.notalert.repository;

import com.learnvault.notalert.entities.Notification;
import com.learnvault.notalert.entity.enums.NotificationCategory;
import com.learnvault.notalert.entity.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserId(Integer userId);
    List<Notification> findByUserIdAndStatus(Integer userId, NotificationStatus status);
    List<Notification> findByUserIdAndCategory(Integer userId, NotificationCategory category);
    long countByUserIdAndStatus(Integer userId, NotificationStatus status);
}