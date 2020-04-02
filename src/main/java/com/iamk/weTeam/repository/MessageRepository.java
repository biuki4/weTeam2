package com.iamk.weTeam.repository;


import com.iamk.weTeam.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findByReceiverId(Integer id, Pageable pageable);

    List<Message> findBySenderId(Integer id, Pageable pageable);

    Page<Message> findById(Integer id, Pageable pageable);

}
