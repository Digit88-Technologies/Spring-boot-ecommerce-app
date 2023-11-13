package com.ecommerce.webapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

/**
 * A product available for purchasing.
 */
@Entity
@Table(name = "store")
public class Store {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @Column(name = "name", nullable = false, unique = true)
  private String name;
  @Column(name = "location_address_description", nullable = false)
  private String locationAddressDescription;

  @ManyToOne
  @JoinColumn(name = "location_id", nullable = false, unique = false)
  private Location location;

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

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }


}