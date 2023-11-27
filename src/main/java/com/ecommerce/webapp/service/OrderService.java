package com.ecommerce.webapp.service;

import com.ecommerce.webapp.exception.AddressNotFoundException;
import com.ecommerce.webapp.exception.ContactNotFoundException;
import com.ecommerce.webapp.model.Address;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.WebOrder;
import com.ecommerce.webapp.model.dao.AddressDAO;
import com.ecommerce.webapp.model.dao.WebOrderDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for handling order actions.
 */
@Service
public class OrderService {

    public static final String CONTACT_NOT_FOUND = "Please update your profile details with an contact Number and verify OTP to proceed.";
    public static final String ADDRESS_NOT_FOUND = "Please update your details with an address to place an order.";
    private final WebOrderDAO webOrderDAO;

    private final AddressDAO addressDAO;

    public OrderService(WebOrderDAO webOrderDAO, AddressDAO addressDAO) {
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

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty() || !user.getPhoneNumberVerified()) {
            throw new ContactNotFoundException(CONTACT_NOT_FOUND);
        }
        List<Address> addressOptional = addressDAO.findByUser_Id(user.getId());

        if (!addressOptional.isEmpty()) {
            WebOrder order = new WebOrder();
            order.setUser(user);
            order.setAddress(addressOptional.get(0));
            return order;
        } else {
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
        webOrderDAO.save(order);
    }
}
