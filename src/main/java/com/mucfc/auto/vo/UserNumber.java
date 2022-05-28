package com.mucfc.auto.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserNumber {

     private List<String> cardList;
     private Boolean setTop;
     private String custSource;
     private Long appointTime;
     private int isStar;
     private Long completedTime;
     private String name;
     private int id ;
}
