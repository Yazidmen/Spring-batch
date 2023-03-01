package com.javatechie.batchprocessingdemo.config;

import com.javatechie.batchprocessingdemo.entity.Customer;
import org.springframework.batch.item.ItemProcessor;


//la classe CustomerProcessor est utilisée pour filtrer les objets Customer qui ne sont pas associés aux États-Unis. Elle ne fait rien avec les autres objets Customer et les ignore.
public class CustomerProcessor implements ItemProcessor<Customer,Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {
        if(customer.getCountry().equals("United States")) {
            return customer;
        }else{
            return null;
        }
    }
}
