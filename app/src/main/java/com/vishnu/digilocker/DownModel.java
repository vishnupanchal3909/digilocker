package com.vishnu.digilocker;

/**
 * Created by Vishnu Panchal on 03-10-2023.
 */
public class DownModel {

    // Declaring all the variables
    String Name,Link;


    // Creating a getter and setter for each variable
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    // Creating a constructor for the DownModel class
    public DownModel(String Name, String Link){
        this.Link=Link;
        this.Name=Name;

    }

}
