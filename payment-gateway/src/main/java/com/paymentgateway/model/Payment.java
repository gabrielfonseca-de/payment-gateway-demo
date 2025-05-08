package com.paymentgateway.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.processing.Pattern;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;  // Usando UUID para evitar expor IDs sequenciais

    @Column(nullable = false, unique = true)
    private String transactionId;  // ID único para rastreamento externo

    @Column(nullable = false)
    @Positive(message = "O valor deve ser positivo")
    private Double amount;

    @Column(nullable = false, length = 3)
    @Pattern(regexp = "^[A-Z]{3}$", message = "Moeda deve ser um código ISO 4217 (ex: BRL, USD)")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime processedAt;  // Timestamp quando o banco processou

    // Dados do pagador (simplificado)
    @Column(nullable = false)
    private String payerName;

    @Column(nullable = false)
    //@Pattern(regexp = "^\\d{16}$", message = "Número do cartão inválido")
    private String cardNumber;

    @Column(nullable = false)
   // @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Data deve estar no formato MM/YY")
    private String cardExpiry;

    @Column(nullable = false)
   // @Pattern(regexp = "^\\d{3,4}$", message = "CVV inválido")
    private String cardCvv;

    // Relacionamento com "Merchant" (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    // Método auxiliar para gerar transactionId
    @PrePersist
    private void generateTransactionId() {
        if (this.transactionId == null) {
            this.transactionId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpiry() {
        return cardExpiry;
    }

    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

    public String getCardCvv() {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv) {
        this.cardCvv = cardCvv;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }
}

