package com.rayan.service;

import com.rayan.model.LeaderboardEntry;
import com.rayan.repository.LeaderboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardService {

    @Autowired
    LeaderboardRepository leaderboardRepository;

    public ResponseEntity<List<LeaderboardEntry>> getTopScores() {
        try {
            return new ResponseEntity<>(
                    leaderboardRepository.findTop10ByOrderByScoreDescCreatedAtAsc(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<List<LeaderboardEntry>> getTopScoresByCategory(String category) {
        try {
            return new ResponseEntity<>(
                    leaderboardRepository.findTop10ByCategoryOrderByScoreDescCreatedAtAsc(category), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}