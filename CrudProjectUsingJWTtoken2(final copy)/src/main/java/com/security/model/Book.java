package com.security.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Book 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String author;
    private double price;
    private String imageName;
    
    public int getId()
    {
        return id;
    }
    public void setId(int id) 
    {
        this.id = id;
    }
    public String getTitle() 
    {
        return title;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }
    public String getAuthor() 
    {
        return author;
    }
    public void setAuthor(String author) 
    {
        this.author = author;
    }
    public double getPrice() 
    {
        return price;
    }
    public void setPrice(double price)
    {
        this.price = price;
    }

    public String getImageName() 
    {
		return imageName;
	}
	public void setImageName(String imageName) 
	{
		this.imageName = imageName;
	}
	
	@Override
    public String toString() 
    {
        return "Book [id=" + id + ", title=" + title + ", author=" + author + ", price=" + price + ", imageName=" + imageName + "]";
    }
}



