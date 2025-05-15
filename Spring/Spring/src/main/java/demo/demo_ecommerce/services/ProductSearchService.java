package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;
    private final LevenshteinDistance distance = new LevenshteinDistance();

    public List<Product> fuzzySearch(String query) {
        query = query.toLowerCase();
        List<Product> allProducts = productRepository.findAll();

        String finalQuery = query;
        return allProducts.stream()
                .map(product -> new ScoredProduct(product, score(finalQuery, product)))
                .filter(sp -> sp.score > 0.3) // ✅ soglia più permissiva
                .sorted(Comparator.comparingDouble(sp -> -sp.score))
                .limit(10)
                .map(sp -> sp.product)
                .collect(Collectors.toList());
    }

    private double score(String query, Product product) {
        String name = product.getName().toLowerCase();
        String description = product.getDescription() != null ? product.getDescription().toLowerCase() : "";

        // ✅ Match esatto nel nome o nella descrizione → massimo punteggio
        if (name.contains(query) || description.contains(query)) {
            return 1.0;
        }

        // Calcolo fuzzy
        int rawDist = distance.apply(query, name);
        int maxLen = Math.max(query.length(), name.length());

        // Fuzzy normalizzato
        return 1.0 - ((double) rawDist / maxLen);
    }

    private record ScoredProduct(Product product, double score) {}
}
