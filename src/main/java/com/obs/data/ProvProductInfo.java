package com.obs.data;

import java.util.Date;
import java.util.Vector;

public class ProvProductInfo {
	public Long planId;
	public Long orderId;
	public Date orderStartDate;
	public Date orderEndDate;
	public Vector <ProvProduct> products = new Vector <ProvProduct>(10);

}
