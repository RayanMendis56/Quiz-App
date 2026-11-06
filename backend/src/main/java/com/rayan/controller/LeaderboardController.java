package com.rayan.controller;

import com.rayan.model.LeaderboardEntry;
import com.rayan.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("leaderboard")
public class LeaderboardController {

    @Autowired
    LeaderboardService leaderboardService;

    @GetMapping("top")
    public ResponseEntity<List<LeaderboardEntry>> getTopScores() {
        return leaderboardService.getTopScores();
    }

    @GetMapping("top/{category}")
    public ResponseEntity<List<LeaderboardEntry>> getTopScoresByCategory(@PathVariable String category) {
        return leaderboardService.getTopScoresByCategory(category);
    }
}