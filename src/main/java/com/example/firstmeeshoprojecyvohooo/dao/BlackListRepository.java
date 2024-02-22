package com.example.firstmeeshoprojecyvohooo.dao;

import com.example.firstmeeshoprojecyvohooo.model.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList,Integer> {

}
