package com.mucfc.auto.vo;

import lombok.Data;

import java.util.List;

@Data
public class MkCase {

    private int batchProcessStatus;
    private int casePoolId;
    private String marketListId;
    private String caseStatus;
    private String  withdrawLifeCycleValue;
    private List<String> sensitiveName ;
    private String batchProcessFailCode;
    private String primaryMarketPurposeCode;
    private Long lastConnectedTime ;
    private int remainDays ;
    private Long batchProcessSuccessTime ;
    private int deleteFlag ;
    private int marketProjectId ;
    private List<String> interest;
    private String lastOperateCode ;
    private Long  custId ;
    private String secondaryMarketPurposeCode ;
    private String  profitNameList ;
    private int  id ;
    private Long  operatorId ;
    private String  caseEndType ;
    private String weChatUserId ;
    private Long reservationTime ;
    private String marketListName ;
    private String updateTime ;
    private String taskOperateResult ;
    private String lastOperateName ;
    // 号码
    private String mobileNo ;
    // 名字
    private String custName ;
    private Long userId ;
    private String taskDesc ;
    private int unprocessTaskCount ;
    private Long expireTime ;
    private String greedyNameList ;
    private Boolean canMakeCall ;
    private Long  createTime ;
    private Long taskCreateTime ;
    private String  marketPurposeList ;
    private String  projectName ;
    private Long  caseEndTime ;
    private Long  taskId ;
    private String   breakpointNameList ;
}
