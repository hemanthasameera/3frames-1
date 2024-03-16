import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children;
    boolean isEndOfWord;
    int count;

    TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
        count = 0;
    }
}

class Trie {
    private TrieNode root;

    Trie() {
        root = new TrieNode();
    }

    TrieNode getRoot() {
        return root;
    }

    void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }
        current.isEndOfWord = true;
        current.count++;
    }

    boolean search(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return false;
            }
            current = current.children.get(ch);
        }
        return current.isEndOfWord;
    }

    int getCount(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return 0;
            }
            current = current.children.get(ch);
        }
        return current.count;
    }

    // Pair class definition
    static class Pair<K, V> {
        private final K key;
        private final V value;

        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    // Perform fuzzy search
    void fuzzySearch(String query, int maxDistance) {
        PriorityQueue<Pair<Integer, String>> pq = new PriorityQueue<>(Comparator.comparingInt(Pair::getKey));
        fuzzySearchUtil(root, "", query, 0, maxDistance, pq);
        while (!pq.isEmpty()) {
            Pair<Integer, String> pair = pq.poll();
            System.out.println(pair.getValue() + " " + getCount(pair.getValue()));
        }
    }

    private void fuzzySearchUtil(TrieNode node, String currentWord, String query, int index, int maxDistance, PriorityQueue<Pair<Integer, String>> pq) {
        if (index > query.length() || currentWord.length() > query.length() + maxDistance) {
            return;
        }
        if (index == query.length()) {
            if (node.isEndOfWord && Math.abs(currentWord.length() - query.length()) <= maxDistance) {
                pq.offer(new Pair<>(computeLevenshteinDistance(currentWord, query), currentWord));
            }
            return;
        }
        for (char ch : node.children.keySet()) {
            if (ch == query.charAt(index)) {
                fuzzySearchUtil(node.children.get(ch), currentWord + ch, query, index + 1, maxDistance, pq);
            } else {
                fuzzySearchUtil(node.children.get(ch), currentWord + ch, query, index + 1, maxDistance - 1, pq);
            }
        }
        if (index < query.length() && node.children.containsKey(query.charAt(index))) {
            fuzzySearchUtil(node.children.get(query.charAt(index)), currentWord + query.charAt(index), query, index + 1, maxDistance, pq);
        }
        fuzzySearchUtil(node, currentWord, query, index + 1, maxDistance - 1, pq);
    }

    // Compute Levenshtein distance between two strings
    private int computeLevenshteinDistance(String s, String t) {
        int m = s.length();
        int n = t.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }

        return dp[m][n];
    }
}

public class WordCountApplication {
    public static void main(String[] args) {
        String filePath = "src/large_text_file.txt";
        int topK = 10;

        Trie trie = new Trie();

        // Read file and build trie
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    word = preprocessWord(word);
                    trie.insert(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Output top k words and counts
        PriorityQueue<Trie.Pair<Integer, String>> pq = new PriorityQueue<>(Comparator.comparingInt(Trie.Pair::getKey));
        traverseTrie(trie, "", pq);
        for (int i = 0; i < topK && !pq.isEmpty(); i++) {
            Trie.Pair<Integer, String> pair = pq.poll();
            System.out.println(pair.getValue() + " " + trie.getCount(pair.getValue()));
        }

        // Perform fuzzy search
        String query = "misspelled_word";
        int maxDistance = 2;
        System.out.println("Fuzzy search results for '" + query + "':");
        trie.fuzzySearch(query, maxDistance);
    }

    private static void traverseTrie(Trie trie, String word, PriorityQueue<Trie.Pair<Integer, String>> pq) {
        for (char ch = 'a'; ch <= 'z'; ch++) {
            TrieNode nextNode = trie.getRoot().children.get(ch);
            if (nextNode != null) {
                traverseTrieUtil(nextNode, word + ch, pq);
            }
        }
    }

    private static void traverseTrieUtil(TrieNode node, String word, PriorityQueue<Trie.Pair<Integer, String>> pq) {
        if (node.isEndOfWord) {
            pq.offer(new Trie.Pair<>(node.count, word));
        }
        for (char ch = 'a'; ch <= 'z'; ch++) {
            TrieNode nextNode = node.children.get(ch);
            if (nextNode != null) {
                traverseTrieUtil(nextNode, word + ch, pq);
            }
        }
    }

    private static String preprocessWord(String word) {
        return word.toLowerCase().replaceAll("[^a-zA-Z]", "");
    }
}
