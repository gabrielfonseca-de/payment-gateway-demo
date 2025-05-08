package com.paymentgateway.service;

import com.paymentgateway.dto.PaymentRequest;
import com.paymentgateway.model.Payment;
import com.paymentgateway.model.PaymentStatus;
import com.paymentgateway.repository.PaymentRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentService(PaymentRepository paymentRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Payment processPayment(PaymentRequest request) {
        // Salva o pagamento com status PENDING
        Payment payment = new Payment();
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Publica no Kafka para o "banco" processar
        kafkaTemplate.send("payment-requests", payment);

        return payment;
    }

    public void processPayment(PaymentRequest request) {
        Payment payment = Payment.builder()
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .payerName(request.getPayerName())
                .cardNumber(request.getCardNumber())  // Na prática, use criptografia!
                .merchant(merchantRepository.findById(request.getMerchantId()).orElseThrow())
                .build();

        paymentRepository.save(payment);

        // Publica um evento Kafka com todos os dados relevantes
        Map<String, Object> paymentEvent = Map.of(
                "paymentId", payment.getTransactionId(),
                "amount", payment.getAmount(),
                "merchantId", payment.getMerchant().getId(),
                "timestamp", LocalDateTime.now().toString()
        );
        kafkaTemplate.send("payment-requests", paymentEvent);
    }
}
