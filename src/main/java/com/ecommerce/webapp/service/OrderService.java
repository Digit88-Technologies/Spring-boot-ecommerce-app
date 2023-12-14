package com.ecommerce.webapp.service;

import com.ecommerce.webapp.exception.AddressNotFoundException;
import com.ecommerce.webapp.exception.ContactNotFoundException;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.WebOrder;

import java.util.List;

/**
 * Service interface for handling order actions.
 */
public interface OrderService {

    /**
     * Gets the list of orders for a given user.
     *
     * @param user The user to search for.
     * @return The list of orders.
     */
    List<WebOrder> getOrders(LocalUser user);

    /**
     * Create a new order for the given user.
     *
     * @param user The user for whom the order is created.
     * @return The created order.
     * @throws ContactNotFoundException If the user's contact information is not found.
     * @throws AddressNotFoundException If the user's address information is not found.
     */
    WebOrder createOrder(LocalUser user) throws ContactNotFoundException, AddressNotFoundException;

    /**
     * Save an order to the database.
     *
     * @param order The order to be saved.
     */
    void saveOrder(WebOrder order);
}
