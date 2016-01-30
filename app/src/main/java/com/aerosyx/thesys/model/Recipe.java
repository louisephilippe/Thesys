package com.aerosyx.thesys.model;

import java.io.Serializable;

public class Recipe implements Serializable{
    public Long id;
    public String name;
    public String instruction;
    public Integer duration;
    public String image;
    public Long category;
    public String category_name;
    public Long date_create;
}
