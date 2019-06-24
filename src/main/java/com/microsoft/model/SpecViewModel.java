package com.microsoft.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({"uid", "name", "fullName","href"})
public class SpecViewModel {

	private String Uid;

    private String Name;
		      
    private String FullName;
    
    public  String Href;
    
    public SpecViewModel(String uid, String name,String fullName) {
        this.Uid = uid;
        this.Name = getSingleObj(name); 
        this.FullName = fullName;   
    }
    
    public SpecViewModel(String name,String fullName) {
        this.Name = name; 
        this.FullName = fullName;   
    }
    
    public String getUid() {
        return Uid;
    }
    
    public String getName() {
        return Name;
    }
    
    public String getFullName() {
        return FullName;
    }
    
    public String getHref() {
        return Href;
    }
    
    String getSingleObj(String value)
    {
    	StringBuilder singleValue=new StringBuilder("");
    	    	
        Optional.ofNullable(value).ifPresent(
                   Param -> {
                	   List<String> strList =new ArrayList<>();
                	   strList = Arrays.asList(StringUtils.split(Param, "."));
                	   Collections.reverse(strList);
                	   singleValue.append(strList.get(0));
                   }
               );
    	   
        return singleValue.toString();
    }
       
}
