package com.ecommerce.webapp.service.impl;

import com.ecommerce.webapp.exception.AddressNotFoundException;
import com.ecommerce.webapp.exception.ContactNotFoundException;
import com.ecommerce.webapp.model.Address;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.WebOrder;
import com.ecommerce.webapp.repository.AddressDAO;
import com.ecommerce.webapp.repository.WebOrderDAO;
import com.ecommerce.webapp.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for handling order actions.
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    public static final String CONTACT_NOT_FOUND = "Please update your profile details with an contact Number and verify OTP to proceed.";
    public static final String ADDRESS_NOT_FOUND = "Please update your details with an address to place an order.";
    private final WebOrderDAO webOrderDAO;

    private final AddressDAO addressDAO;

    public OrderServiceImpl(WebOrderDAO webOrderDAO, AddressDAO addressDAO) {
        this.webOrderDAO = webOrderDAO;
        this.addressDAO = addressDAO;
    }

    /**
     * Gets the list of orders for a given user.
     *
     * @param user The user to search for.
     * @return The list of orders.
     */
    public List<WebOrder> getOrders(LocalUser user) {

        log.info("Getting orders for user " + user.getUsername());
        return webOrderDAO.findByUser(user);
    }

    /**
     * Create a new order for the given user.
     *
     * @param user The user for whom the order is created.
     * @return The created order.
     */
    @Transactional
    public WebOrder createOrder(LocalUser user) {

        log.info("Creating order for current user with requested product details");

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty() || !user.getPhoneNumberVerified()) {
            log.error("Contact Not Registered for user");
            throw new ContactNotFoundException(CONTACT_NOT_FOUND);
        }
        List<Address> addressOptional = addressDAO.findByUser_Id(user.getId());

        if (!addressOptional.isEmpty()) {
            WebOrder order = new WebOrder();
            order.setUser(user);
            order.setAddress(addressOptional.get(0));
            return order;
        } else {
            log.error("Address Not Found for user");
            throw new AddressNotFoundException(ADDRESS_NOT_FOUND);
        }

    }

    /**
     * Save an order to the database.
     *
     * @param order The order to be saved.
     */
    @Transactional
    public void saveOrder(WebOrder order) {

        log.info("Saving order details");
        webOrderDAO.save(order);
    }
}
