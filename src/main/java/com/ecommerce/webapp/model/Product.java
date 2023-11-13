package com.ecommerce.webapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

/**
 * A product available for purchasing.
 */
@Entity
@Table(name = "product")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @Column(name = "name", nullable = false, unique = true)
  private String name;
  @Column(name = "short_description", nullable = false)
  private String shortDescription;
  @Column(name = "long_description")
  private String longDescription;
  @Column(name = "price", nullable = false)
  private Double price;
  @OneToOne(mappedBy = "product", cascade = CascadeType.REMOVE, optional = false, orphanRemoval = true)
  private Inventory inventory;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private ProductCategory category;


  public ProductCategory getCategory() {
    return category;
  }

  public void setCategory(ProductCategory category) {
    this.category = category;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }


  public Double getPrice() {
    return price;
  }


  public void setPrice(Double price) {
    this.price = price;
  }


  public String getLongDescription() {
    return longDescription;
  }


  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }


  public String getShortDescription() {
    return shortDescription;
  }


  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public Long getId() {
    return id;
  }


  public void setId(Long id) {
    this.id = id;
  }

}