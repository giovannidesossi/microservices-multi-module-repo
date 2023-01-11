package it.desossi.productservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import it.desossi.productservice.dto.ProductRequest;
import it.desossi.productservice.dto.ProductResponse;
import it.desossi.productservice.model.Product;
import it.desossi.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

	/**
	 * Usiamo la Constructor Injection anzichè l'Autowired
	 * Richiede l'Annotation '@RequiredArgsConstructor'
	 */
	private final ProductRepository productRepository;

	public void createProduct(ProductRequest productRequest) {
		Product product = Product.builder()
				.name(productRequest.getName())
				.description(productRequest.getDescription())
				.price(productRequest.getPrice())
				.build();
		productRepository.save(product);
		log.info("Product {} is saved", product.getId());
	}

	public List<ProductResponse> getAllProducts() {
		productRepository.findAll();
		List<Product> products = productRepository.findAll();
		return products.stream().map(this::mapToProductResponse).toList();
	}

	private ProductResponse mapToProductResponse(Product product) {
		return ProductResponse.builder()
				.id(product.getId())
				.description(product.getDescription())
				.name(product.getName())
				.price(product.getPrice())
				.build();
		
	}

}
