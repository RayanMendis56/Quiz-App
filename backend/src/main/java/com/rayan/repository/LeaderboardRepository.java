package com.rayan.repository;

import com.rayan.model.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaderboardRepository extends JpaRepository<LeaderboardEntry, Integer> {

    List<LeaderboardEntry> findTop10ByOrderByScoreDescCreatedAtAsc();

    List<LeaderboardEntry> findTop10ByCategoryOrderByScoreDescCreatedAtAsc(String category);
}