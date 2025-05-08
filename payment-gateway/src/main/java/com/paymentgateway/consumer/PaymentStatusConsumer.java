package com.paymentgateway.consumer;

import com.paymentgateway.model.Payment;
import com.paymentgateway.model.PaymentStatus;
import com.paymentgateway.repository.PaymentRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentStatusConsumer {

    private final PaymentRepository paymentRepository;

    public PaymentStatusConsumer(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @KafkaListener(topics = "payment-responses", groupId = "payment-group")
    public void updatePaymentStatus(Payment paymentResponse) {
        // Atualiza o status do pagamento no banco de dados
        Payment payment = paymentRepository.findByTransactionId(paymentResponse.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        payment.setStatus(paymentResponse.getStatus());
        paymentRepository.save(payment);

        // Aqui você pode chamar um webhook para notificar o e-commerce
    }
}
