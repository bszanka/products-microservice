package io.bszanka.products.service;

import io.bszanka.products.rest.CreateProductRestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ProductServiceImpl implements ProductService {

    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductRestModel productRestModel) throws ExecutionException, InterruptedException {

        String productId = UUID.randomUUID().toString();

        // TODO: Persist Product Details into database table before publishing an Event

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId,
                productRestModel.getTitle(), productRestModel.getPrice(),
                productRestModel.getQuantity());

        // Asynchronous way of sending a message
//        CompletableFuture<SendResult<String, ProductCreatedEvent>> future =
//                kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent);
//
//        future.whenComplete((result, exception) -> {
//            if(exception != null) {
//                log.error("Great Failure!\n {} ", exception.getMessage());
//            } else {
//                log.info("Event Published Successfully!\n {}", result.getRecordMetadata());
//            }
//        });

        // Synchronous way of sending a message
        SendResult<String, ProductCreatedEvent> result =
                kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent)
                        .get();

        log.info("Partition Number: {}", result.getRecordMetadata().partition());
        log.info("Offset Number: {}", result.getRecordMetadata().offset());
        log.info("Topic: {}", result.getRecordMetadata().topic());
        log.info("Returning Product Id: {}", productId);

        return productId;
    }

}
