import java.util.*;

//  Product class
class Product {
    private String id;
    private String name;
    private String description;
    private String category;
    private double price;

    // Constructor
    public Product(String id, String name, String description, String category, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }
}

// Product Database
class ProductDatabase {
    private Map<String, Product> productsById;
    private Map<String, List<Product>> productsByCategory;

    public ProductDatabase() {
        productsById = new HashMap<>();
        productsByCategory = new HashMap<>();
    }

    // Add product to database
    public void addProduct(Product product) {
        productsById.put(product.getId(), product);
        productsByCategory.computeIfAbsent(product.getCategory(), k -> new ArrayList<>()).add(product);
    }

    // Search products by category
    public List<Product> getProductsByCategory(String category) {
        return productsByCategory.getOrDefault(category, Collections.emptyList());
    }

    // Search products by keyword
    public List<Product> searchProducts(String keyword) {
        String processedKeyword = preprocess(keyword);
        List<Product> result = new ArrayList<>();
        for (Product product : productsById.values()) {
            String processedName = preprocess(product.getName());
            String processedDescription = preprocess(product.getDescription());
            if (processedName.contains(processedKeyword) || processedDescription.contains(processedKeyword)) {
                result.add(product);
            }
        }
        return result;
    }

    // Preprocess keyword
    private String preprocess(String keyword) {
        return keyword.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().replaceAll("\\s+", "");
    }
}

public class Main {
    public static void main(String[] args) {
        // Create product database
        ProductDatabase database = new ProductDatabase();

        // Add some products
        database.addProduct(new Product("001", "Laptop", "Best laptop", "Electronics", 999.99));
        database.addProduct(new Product("002", "Smartphone", "Latest smartphone model", "Electronics", 699.99));
        database.addProduct(new Product("003", "Headphones", "Best Bass headphones", "Electronics", 199.99));
        database.addProduct(new Product("004", "T-shirt", "Cotton T-shirt", "Clothing", 19.99));
        database.addProduct(new Product("005", "Jeans", "Mid fit jeans", "Clothing", 39.99));

        // Search products by category
        List<Product> electronicsProducts = database.getProductsByCategory("Electronics");
        System.out.println("Electronics Products:");
        for (Product product : electronicsProducts) {
            System.out.println(product.getName() + " - $" + product.getPrice());
        }

        // Search products by keyword
        List<Product> searchResults = database.searchProducts("shirt");
        System.out.println("\nSearch Results:");
        for (Product product : searchResults) {
            System.out.println(product.getName() + " - $" + product.getPrice());
        }
    }
}
