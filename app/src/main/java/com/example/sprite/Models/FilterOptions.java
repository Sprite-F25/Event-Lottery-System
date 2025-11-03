package com.example.sprite.Models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilterOptions implements Serializable {
    public List<String> categories = new ArrayList<>();
    public String quickTime = "";
    public Long   chosenDate = null;
    public String location = "";
    public float  priceMin = 20f;
    public float  priceMax = 120f;
    public boolean freeOnly = false;
}
