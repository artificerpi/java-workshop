package com.example.billing;

import com.example.billing.model.CreditCard;
import com.example.billing.model.PizzaOrder;
import com.example.billing.model.Receipt;
import com.google.inject.Inject;

public class BillingService {
  private final CreditCardProcessor processor;
  private final TransactionLog transactionLog;

  @Inject
  BillingService(CreditCardProcessor processor, 
      TransactionLog transactionLog) {
    this.processor = processor;
    this.transactionLog = transactionLog;
  }

  public Receipt chargeOrder(PizzaOrder order, CreditCard creditCard) {
    // processor do something 
    // transactionLog log the deal
    // create new receipt
    return new Receipt();
  }

}
