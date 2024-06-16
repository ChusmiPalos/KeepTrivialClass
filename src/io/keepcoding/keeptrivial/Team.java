package io.keepcoding.keeptrivial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Team {
    private String name;
    private Map<String, Boolean> topics;

    public Team(String name, List<String> topicNames) {
        this.name = name;
        this.topics = new HashMap<>();
        for (String topicName : topicNames) {
            this.topics.put(topicName, false);
        }
    }

    public String getName() {
        return name;
    }

    public Map<String, Boolean> getTopics() {
        return topics;
    }

    public void setTopicCompleted(String topicName) {
        if (topics.containsKey(topicName)) {
            topics.put(topicName, true);
        }
    }

    public boolean hasWon() {
        for (Boolean completed : topics.values()) {
            if (!completed) {
                return false;
            }
        }
        return true;
    }
    
    public int getPoints() {
        int points = 0;
        for (Boolean completed : topics.values()) {
            if (completed) {
                points++;
            }
        }
        return points;
    }
}