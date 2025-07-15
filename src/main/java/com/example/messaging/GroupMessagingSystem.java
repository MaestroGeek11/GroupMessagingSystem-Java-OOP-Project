package com.example.messaging;

import java.util.*;

public class GroupMessagingSystem {

    private Map<String, List<String>> groupMessages = new HashMap<>();
    private Map<String, Set<User>> groupUsers = new HashMap<>();

    public void sendMessage(String groupName, String message, User sender) {
        if (!groupMessages.containsKey(groupName)) {
            groupMessages.put(groupName, new ArrayList<>());
            groupUsers.put(groupName, new HashSet<>());
        }
        groupMessages.get(groupName).add(sender.getUserName() + ":" + message);
        groupUsers.get(groupName).add(sender);
    }

    public User findMostActiveUser(String groupName) {
        if (!groupMessages.containsKey(groupName)) return null;

        Map<User, Integer> userMessageCount = new HashMap<>();
        List<String> messages = groupMessages.get(groupName);
        Set<User> users = groupUsers.get(groupName);

        for (User user : users) {
            int count = 0;
            for (String message : messages) {
                String[] parts = message.split(":", 2);
                if (parts.length > 1 && parts[0].equals(user.getUserName())) {
                    count++;
                }
            }
            userMessageCount.put(user, count);
        }

        List<User> sortedUsers = new ArrayList<>(userMessageCount.keySet());
        sortUsersByMessageCount(sortedUsers, userMessageCount);
        return sortedUsers.get(0);
    }

    private void sortUsersByMessageCount(List<User> users, Map<User, Integer> criteriaMap) {
        int n = users.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (criteriaMap.get(users.get(j)) < criteriaMap.get(users.get(j + 1))) {
                    User temp = users.get(j);
                    users.set(j, users.get(j + 1));
                    users.set(j + 1, temp);
                }
            }
        }
    }

    public UserStatistics getUserStatistics() {
        return (user, groupName) -> {
            int userMessageCount = 0;
            List<String> messages = groupMessages.getOrDefault(groupName, new ArrayList<>());
            for (String message : messages) {
                if (message.contains(user.getUserName())) userMessageCount++;
            }
            return userMessageCount;
        };
    }

    public double getAverageMessagesPerUser(String groupName) {
        if (!groupMessages.containsKey(groupName)) return 0.0;
        int totalMessages = groupMessages.get(groupName).size();
        int totalUsers = groupUsers.get(groupName).size();
        return (double) totalMessages / totalUsers;
    }

    public String getMostFrequentMessage(String groupName) {
        if (!groupMessages.containsKey(groupName)) return null;
        List<String> messages = groupMessages.get(groupName);
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String message : messages) {
            frequencyMap.put(message, frequencyMap.getOrDefault(message, 0) + 1);
        }
        List<String> uniqueMessages = new ArrayList<>(frequencyMap.keySet());
        sortMessages(uniqueMessages);
        return uniqueMessages.get(0);
    }

    private void sortMessages(List<String> messages) {
        Collections.sort(messages, Comparator.comparingInt(String::length));
    }

    public static class User {
        private final String userID;
        private final String userName;

        public User(String userID, String userName) {
            this.userID = userID;
            this.userName = userName;
        }

        public String getUserID() {
            return userID;
        }

        public String getUserName() {
            return userName;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof User)) return false;
            User other = (User) obj;
            return userID.equals(other.userID);
        }

        @Override
        public String toString() {
            return "User{" + "userID='" + userID + "', userName='" + userName + "'}";
        }

        @Override
        public int hashCode() {
            return Objects.hash(userID);
        }
    }

    public interface UserStatistics {
        int getUserMessageCount(User user, String groupName);
    }

    public static void main(String[] args) {
        GroupMessagingSystem system = new GroupMessagingSystem();
        system.sendMessage("GroupA", "Hello from Alpha", new User("U001", "Alpha"));
        system.sendMessage("GroupA", "Hi from Bravo", new User("U002", "Bravo"));
        system.sendMessage("GroupA", "How's it going?", new User("U002", "Bravo"));
        system.sendMessage("GroupA", "Another message from Alpha", new User("U001", "Alpha"));
        system.sendMessage("GroupA", "How are you, Bravo?", new User("U002", "Bravo"));
        system.sendMessage("GroupB", "Greetings from Charlie", new User("U003", "Charlie"));
        system.sendMessage("GroupB", "Good morning from Alpha", new User("U001", "Alpha"));

        User mostActiveUserA = system.findMostActiveUser("GroupA");
        System.out.println("Most active user in GroupA: " + (mostActiveUserA != null ? mostActiveUserA.getUserName() : "None"));

        UserStatistics stats = system.getUserStatistics();
        int count = stats.getUserMessageCount(new User("U001", "Alpha"), "GroupA");
        System.out.println("Messages sent by Alpha in GroupA: " + count);

        String frequent = system.getMostFrequentMessage("GroupB");
        System.out.println("Most frequent message in GroupB: " + frequent);

        double avg = system.getAverageMessagesPerUser("GroupA");
        System.out.println("Average messages per user in GroupA: " + avg);
    }
}

