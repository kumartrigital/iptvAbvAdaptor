package com.obs.data;

import org.json.JSONObject;

public class ProcessRequestData {

	private String command;
	private String requestType;
	private Long id;
	private Long serviceId;
	private Long taskId;
	public JSONObject jsonObject;
	
	public ProcessRequestData(String requestType1, Long orderId,
			Long serviceId, Long task, JSONObject jsonObj) {
		this.requestType = requestType1;
		this.id = orderId;
		this.serviceId = serviceId;
		this.taskId = task;
		this.jsonObject = jsonObj;
	}

	public Long getId() {
		return id;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getRequestType() {
		return requestType;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long gettaskId() {
		return taskId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	



}
