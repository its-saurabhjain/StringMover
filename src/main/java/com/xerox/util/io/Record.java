package com.xerox.util.io;

public class Record
{
  String S200_STG_HDR;
  String S200_STG_HDR_TYP;
  String S200_CYC_NUM;
  String S200_CYC_DATE;
  String S200_SORT_TYPE;
  String S200_BANK_NO;
  String S200_ENTRY_NO;
  String S200_SORTER_NO;
  String S200_X9_FILE_NM;
  String S200_FILLER1;
	public String toString() {
      String result = "S200_STG_HDR:" + S200_STG_HDR + "," +
    		  		  "S200_STG_HDR_TYP:" + S200_STG_HDR_TYP + "," +
    		  		  "S200_CYC_NUM:" + S200_CYC_NUM + "," +
    		  		  "S200_CYC_DATE:" + S200_CYC_DATE + "," +
    		  		  "S200_SORT_TYPE:" + S200_SORT_TYPE + "," +
    		  		  "S200_BANK_NO;" + S200_BANK_NO + "," +
    		  		  "S200_ENTRY_NO:" + S200_ENTRY_NO + "," +
    		  		  "S200_SORTER_NO:" + S200_SORTER_NO + "," +
    		  		  "S200_X9_FILE_NM:" + S200_X9_FILE_NM + "," +
    		  		  "S200_FILLER1:" + S200_FILLER1;
     
      return result;
		 
	}  
}

