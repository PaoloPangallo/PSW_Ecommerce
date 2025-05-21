package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;
    private final LevenshteinDistance distance = new LevenshteinDistance();

    // Lista di stopwords italiane comuni
    private static final Set<String> STOPWORDS = Set.of(
            "il", "la", "lo", "i", "gli", "le",
            "un", "una", "uno", "di", "a", "da", "in", "con", "su", "per", "tra", "fra", "e", "o"
    );

    public List<Product> fuzzySearch(String query) {
        if (query == null || query.isBlank()) return List.of();

        // Normalizzazione e tokenizzazione
        List<String> keywords = Arrays.stream(query.toLowerCase().split("\\s+"))
                .filter(token -> !STOPWORDS.contains(token))
                .collect(Collectors.toList());

        if (keywords.isEmpty()) return List.of();

        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .map(product -> new ScoredProduct(product, computeScore(keywords, product)))
                .filter(sp -> sp.score > 0.3) // Soglia fuzzy
                .sorted(Comparator.comparingDouble(sp -> -sp.score))
                .limit(10)
                .map(sp -> sp.product)
                .collect(Collectors.toList());
    }

    private double computeScore(List<String> keywords, Product product) {
        String name = product.getName() != null ? product.getName().toLowerCase() : "";
        String description = product.getDescription() != null ? product.getDescription().toLowerCase() : "";

        double totalScore = 0.0;

        for (String keyword : keywords) {
            double nameScore = computeFuzzyScore(keyword, name);
            double descScore = computeFuzzyScore(keyword, description);

            // Boost del nome
            double combined = 0.7 * nameScore + 0.3 * descScore;
            totalScore += combined;
        }

        // Media sul numero di keyword
        return totalScore / keywords.size();
    }

    private double computeFuzzyScore(String query, String text) {
        if (text.contains(query)) {
            return 1.0;
        }

        int dist = distance.apply(query, text);
        int maxLen = Math.max(query.length(), text.length());

        if (maxLen == 0) return 0.0;

        return 1.0 - ((double) dist / maxLen);
    }

    private record ScoredProduct(Product product, double score) {}
}
