package com.ecommerce.webapp.service;

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

  private final WebOrderDAO webOrderDAO;

  private AddressDAO addressDAO;

  public OrderService(WebOrderDAO webOrderDAO) {
    this.webOrderDAO = webOrderDAO;
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
    WebOrder order = new WebOrder();
    order.setUser(user);

    Optional<WebOrder> address = webOrderDAO.findById(user.getId());
    order.setAddress(address.get().getAddress());

    // Additional initialization for the order if needed
    return order;
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
