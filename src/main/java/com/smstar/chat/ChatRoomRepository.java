package com.smstar.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT r FROM ChatRoom r JOIN r.members m WHERE m.id = :userId")
    List<ChatRoom> findByMemberId(@Param("userId") Long userId);
}
