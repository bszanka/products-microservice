package io.bszanka.products.service;

import io.bszanka.products.rest.CreateProductRestModel;

import java.util.concurrent.ExecutionException;

public interface ProductService {

    String createProduct(CreateProductRestModel productRestModel) throws ExecutionException, InterruptedException;

}
