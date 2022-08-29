package com.springframework.jpabook.model.entity;

import java.util.Date;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity {

	private Date createdDate;		//등록일
	private Date lastModifiedDate;	//수정일
	
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}
