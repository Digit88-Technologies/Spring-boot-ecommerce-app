package com.ecommerce.webapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

/**
 * A product available for purchasing.
 */
@Entity
@Table(name = "location")
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @Column(name = "name", nullable = false, unique = true)
  private String name;
  @Column(name = "location_address_description", nullable = false)
  private String locationAddressDescription;

  @JsonIgnore
  @OneToMany(mappedBy = "location", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Store> store;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocationAddressDescription() {
    return locationAddressDescription;
  }

  public void setLocationAddressDescription(String locationAddressDescription) {
    this.locationAddressDescription = locationAddressDescription;
  }

  public List<Store> getStore() {
    return store;
  }

  public void setStore(List<Store> store) {
    this.store = store;
  }
}