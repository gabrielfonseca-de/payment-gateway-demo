package com.paymentgateway.repository;

import com.paymentgateway.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // Busca por transactionId (método padrão)
    Optional<Payment> findByTransactionId(String transactionId);

    // Consulta personalizada: pagamentos aprovados de um merchant
    @Query("SELECT p FROM Payment p WHERE p.merchant.id = :merchantId AND p.status = 'APPROVED'")
    List<Payment> findApprovedPaymentsByMerchant(@Param("merchantId") Long merchantId);

    // Consulta nativa: pagamentos acima de um valor
    @Query(value = "SELECT * FROM payments WHERE amount > :minAmount", nativeQuery = true)
    List<Payment> findPaymentsAboveAmount(@Param("minAmount") Double minAmount);
}
