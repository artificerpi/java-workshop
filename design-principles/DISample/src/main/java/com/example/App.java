package com.example;

import com.example.billing.BillingService;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Code snippets are from the google guice official tutorial:
 * https://github.com/google/guice/wiki/GettingStarted
 */
public class App {

  public static void main(String[] args) {
    /*
     * Guice.createInjector() takes your Modules, and returns a new Injector
     * instance. Most applications will call this method exactly once, in their
     * main() method.
     */
    Injector injector = Guice.createInjector(new BillingModule());

    /*
     * Now that we've got the injector, we can build objects.
     */
    BillingService billingService = injector.getInstance(BillingService.class);
    
  }
}
