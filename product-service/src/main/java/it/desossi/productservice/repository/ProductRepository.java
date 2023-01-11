package it.desossi.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.desossi.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {

}
