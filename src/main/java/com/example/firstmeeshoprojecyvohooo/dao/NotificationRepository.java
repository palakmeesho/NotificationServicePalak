package com.example.firstmeeshoprojecyvohooo.dao;

import com.example.firstmeeshoprojecyvohooo.model.SmsRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<SmsRequest,Integer> {

}
