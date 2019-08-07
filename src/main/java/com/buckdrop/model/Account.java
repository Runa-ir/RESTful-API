package com.buckdrop.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Account {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView(View.Base.class)
	long id;
	
	@JsonView(View.Base.class)
	long accountNumber;
	
	@JsonView(View.Base.class)
	AccountType accountType;
	
	@JsonView(View.Base.class)
	double balance;
	
	@ManyToOne
	@JsonView(View.AccountExtended.class)
	Holder holder;
	
	public Account() {}
	
	public Account(long accountNumber, double balance, AccountType accountType) {
		super();
		this.accountNumber = accountNumber;
		this.balance = balance;		
		this.accountType = accountType;
	}

	public long getId() {
		return id;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public Holder getHolder() {
		return holder;
	}

	public void setHolder(Holder holder) {
		this.holder = holder;
	}

}
