package com.ecommerce.webapp.service;

import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.WebOrder;
import com.ecommerce.webapp.model.dao.WebOrderDAO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling order actions.
 */
@Service
public class OrderService {

  private WebOrderDAO webOrderDAO;


  public OrderService(WebOrderDAO webOrderDAO) {
    this.webOrderDAO = webOrderDAO;
  }

  /**
   * Gets the list of orders for a given user.
   * @param user The user to search for.
   * @return The list of orders.
   */
  public List<WebOrder> getOrders(LocalUser user) {
    return webOrderDAO.findByUser(user);
  }

}
