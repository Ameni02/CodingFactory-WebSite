package com.esprit.microservice.pfespace.Services;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlagiarismDetectionService {

    private static final int FINGERPRINT_WINDOW = 5;
    private static final int MIN_HASH_LENGTH = 50;
    private static final int MIN_WORD_COUNT = 100;

    // Enhanced English phrase database
    private static final Map<String, List<String>> PHRASE_BANKS = Map.of(
            "academic", List.of(
                    "literature review", "research gap", "methodology",
                    "research objectives", "findings suggest", "theoretical framework",
                    "data analysis", "conclusion", "hypothesis", "limitations",
                    "results indicate", "study demonstrates", "paper examines"
            ),
            "technical", List.of(
                    "spring boot", "docker", "rest api", "microservices",
                    "database schema", "authentication", "jpa repository",
                    "frontend", "backend", "integration testing"
            ),
            "common", List.of(
                    "in this paper", "as shown in", "in conclusion",
                    "the results show", "we propose", "it can be seen",
                    "this study aims", "the main goal", "as previously mentioned"
            )
    );

    public Map<String, Object> analyzeDocument(MultipartFile file) throws IOException {
        String content = extractText(file);
        content = normalizeText(content);

        Map<String, Object> results = new LinkedHashMap<>();
        results.put("filename", file.getOriginalFilename());
        results.put("phraseMatches", detectPhraseMatches(content));
        results.put("fingerprintSample", generateFingerprints(content));
        results.put("semanticSimilarity", calculateSemanticSimilarity(content));
        results.put("webMatches", checkWebForPlagiarism(content));
        results.put("metrics", calculateTextMetrics(content));
        results.put("isShortDocument", content.split("\\s+").length < MIN_WORD_COUNT);

        return results;
    }

    private String extractText(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename().toLowerCase();

        if (filename.endsWith(".pdf")) {
            try (PDDocument doc = PDDocument.load(file.getInputStream())) {
                return new PDFTextStripper().getText(doc);
            }
        } else if (filename.endsWith(".docx")) {
            try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
                return new XWPFWordExtractor(doc).getText();
            }
        }
        throw new IllegalArgumentException("Only PDF and DOCX files are supported");
    }

    private String normalizeText(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }
        return content.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private Map<String, List<String>> detectPhraseMatches(String content) {
        Map<String, List<String>> matches = new HashMap<>();
        PHRASE_BANKS.forEach((category, phrases) -> {
            List<String> found = phrases.stream()
                    .filter(phrase -> content.contains(phrase))
                    .collect(Collectors.toList());
            if (!found.isEmpty()) matches.put(category, found);
        });
        return matches;
    }

    private List<String> generateFingerprints(String content) {
        List<String> fingerprints = new ArrayList<>();
        String[] words = content.split("\\s+");

        if (words.length < FINGERPRINT_WINDOW) {
            return List.of("Document too short for fingerprinting (needs ≥ " + FINGERPRINT_WINDOW + " words)");
        }

        for (int i = 0; i <= words.length - FINGERPRINT_WINDOW; i++) {
            String window = String.join(" ", Arrays.copyOfRange(words, i, i + FINGERPRINT_WINDOW));
            if (window.length() >= MIN_HASH_LENGTH) {
                fingerprints.add(String.valueOf(window.hashCode()));
            }
            if (fingerprints.size() >= 5) break;
        }
        return fingerprints;
    }

    private double calculateSemanticSimilarity(String content) {
        List<String> sampleTexts = List.of(
                "This thesis examines the impact of technology on modern education systems",
                "The methodology section describes the experimental design and data collection process",
                "Our findings demonstrate significant improvements in student engagement",
                "The literature review covers recent advancements in educational technology",
                "The research objectives are to evaluate three key performance metrics",
                "Results indicate a strong correlation between implementation quality and outcomes",
                "This paper presents a novel framework for assessing educational tools",
                "Data analysis reveals two distinct patterns of user behavior"
        );

        return sampleTexts.stream()
                .mapToDouble(sample -> computeJaccardSimilarity(content, sample))
                .max()
                .orElse(0.0);
    }

    private double computeJaccardSimilarity(String text1, String text2) {
        Set<String> words1 = new HashSet<>(Arrays.asList(text1.split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(text2.split("\\s+")));

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private List<String> checkWebForPlagiarism(String content) {
        List<String> matches = new ArrayList<>();

        try {
            // Find the most distinctive phrase
            Optional<String> bestPhrase = PHRASE_BANKS.values().stream()
                    .flatMap(List::stream)
                    .filter(phrase -> content.contains(phrase) && phrase.length() > 15)
                    .findFirst();

            String query = bestPhrase.orElseGet(() -> {
                String[] sentences = content.split("(?<=\\.|\\?|!)\\s+(?=[A-Z])");
                return sentences.length > 0 ? sentences[0] : "";
            });

            if (query.isEmpty() || query.length() < 20) {
                return List.of("No searchable phrases found");
            }

            String url = "https://www.google.com/search?q=" +
                    URLEncoder.encode(query, StandardCharsets.UTF_8);

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();

            doc.select("div.g").stream()
                    .limit(3)
                    .map(result -> result.select("div.IsZvec").text())
                    .filter(snippet -> !snippet.isEmpty())
                    .forEach(matches::add);

        } catch (IOException e) {
            matches.add("Web check unavailable (rate limited or connection error)");
        }

        return matches.isEmpty() ? List.of("No similar content found online") : matches;
    }

    private Map<String, Object> calculateTextMetrics(String content) {
        Map<String, Object> metrics = new HashMap<>();
        String[] sentences = content.split("(?<=\\.|\\?|!)\\s+(?=[A-Z])");
        String[] words = content.split("\\s+");

        metrics.put("wordCount", words.length);
        metrics.put("avgSentenceLength", sentences.length > 0 ?
                (double) words.length / sentences.length : 0);
        metrics.put("uniqueWordRatio", calculateUniqueWordRatio(words));

        return metrics;
    }

    private double calculateUniqueWordRatio(String[] words) {
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(words));
        return ((double) uniqueWords.size() / words.length) * 100;
    }
}