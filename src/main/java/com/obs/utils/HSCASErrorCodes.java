package com.obs.utils;


import java.util.Hashtable;

@SuppressWarnings("unchecked")
public class HSCASErrorCodes
{
  public static Hashtable errorCodes = new Hashtable();
  //[1, 3, 0, 1, 48]

  static
  {
    errorCodes.put(2, " ConnectionStatus Response Packet");
    errorCodes.put(49, "The length of OSD is wrong. When the value of Data_Len of OSD display data packet exceeds 200 or when the length is 0, returns this error.");
    errorCodes.put(224, "Command type is not supported.");
    errorCodes.put(225, "The length of the data packet is wrong.");
    errorCodes.put(-1, "The length of the data packet is wrong.");

 }
  
  public static String getErrorDesc(String  errorCode)
  {
    return (String)errorCodes.get(errorCode);
  }
 

}
