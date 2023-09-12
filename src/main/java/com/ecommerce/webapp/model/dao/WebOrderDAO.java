package com.ecommerce.webapp.model.dao;

import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * Data Access Object to access WebOrder data.
 */
public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {

  List<WebOrder> findByUser(LocalUser user);

}
