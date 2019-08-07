package com.buckdrop.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Holder {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView(View.Base.class)
	long id;
	
	@JsonView(View.Base.class)
	String name;
	
	@JsonView(View.Base.class)
	String lastname;
	
	@OneToMany(mappedBy="holder", cascade=CascadeType.ALL)
	@JsonView(View.HolderExtended.class)
	List<Account> accounts = new ArrayList<Account>();

	public Holder() {}
	
	public Holder(String name, String lastname) {
		super();
		this.name = name;
		this.lastname = lastname;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	
	
	
}
