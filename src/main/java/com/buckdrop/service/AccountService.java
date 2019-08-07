package com.buckdrop.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.buckdrop.jpa.EntityManagerUtil;
import com.buckdrop.model.Account;
import com.buckdrop.model.GenericResponse;
import com.buckdrop.model.View;
import com.fasterxml.jackson.annotation.JsonView;

@Path("/account")
public class AccountService {
	
	protected final Logger logger = LogManager.getLogger(this.getClass());

	/**
	* Get all accounts
	*/
	@GET
	@Produces(value=MediaType.APPLICATION_JSON)
	@JsonView(View.Base.class)
	public Response getAllAccounts(){
		logger.info("Get the list of accounts");

		List<Account> accounts = EntityManagerUtil.getEntityManager().createQuery("Select b from Account b", Account.class).getResultList();
		
		if(accounts!=null && accounts.size()>0){
			return Response.ok(accounts).build();
		}
		
		return Response.status(Status.NOT_FOUND).entity(new GenericResponse("Account list is empty")).build();
	}
	
	/**
	* Get account by id
	* @param id
	*/
	@GET
	@Path("/{id}")
	@Produces(value=MediaType.APPLICATION_JSON)
	@JsonView(View.AccountExtended.class)
	public Response getAccount(@PathParam("id") long id){
		logger.info("Get the account with id "+id);
		Account account = EntityManagerUtil.getEntityManager().find(Account.class, id);
		
		if(account!=null){
			return Response.ok(account).build();
		}
		
		return Response.status(Status.NOT_FOUND).entity(new GenericResponse("Account " + id + " does not exist")).build();
	}
	
	
	/**
	* Get balance by account Id
	* @param id
	* @return balance
	*/
	@GET
	@Path("/{id}/balance")
	public double getBalance(@PathParam("id") long id){
	    final Account account = EntityManagerUtil.getEntityManager().find(Account.class, id);
	
	    if(account == null){
	    	Response.status(Status.NOT_FOUND).entity(new GenericResponse("Account "+id+" does not exist")).build();
	    }
	    return account.getBalance();
	}
	
	
	/**
     * Transfer amount from account to account
     * @param id_originator
     * @param id_beneficiery
     * @param amount
     */
    @PUT
    @Path("/{id_originator}/transfer/{id_beneficiery}/{amount}")
    public Response depositAmount(@PathParam("id_originator") long id_originator, 
					    		 @PathParam("id_beneficiery") long id_beneficiery, 
					    		 @PathParam("amount") double amount){
    	
    	Account accountOrigin = EntityManagerUtil.getEntityManager().find(Account.class, id_originator);
    	Account accountBenef = EntityManagerUtil.getEntityManager().find(Account.class, id_beneficiery);
        logger.info("Transfer service: " + (amount) + " from account # " + accountOrigin.getAccountNumber() 
        			+ " to account # " + accountBenef.getAccountNumber());
    	
        if (amount <= 0.0){
        	logger.info("Invalid transfer amount");
        	return Response.status(Status.BAD_REQUEST).entity(new GenericResponse("Invalid transfer amount")).build();
        }   
        double newBalanceOrigin = accountOrigin.getBalance() - amount;
        if (newBalanceOrigin <= 0.0){
        	logger.info("Not sufficient balance on the originator account");
        	return Response.status(Status.BAD_REQUEST).entity(new GenericResponse("Not sufficient balance on the account")).build();
        } 
        
        accountOrigin.setBalance(accountOrigin.getBalance() - amount);
        accountBenef.setBalance(accountBenef.getBalance() + amount);
        
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
		entityManager.getTransaction().begin();		
		entityManager.merge(accountOrigin);	
		entityManager.merge(accountBenef);			
		entityManager.getTransaction().commit();
		
		Response.ok(accountOrigin).build();
		Response.ok(accountBenef).build();
				
		logger.info("Transfer successful");        
        return getAllAccounts();
    }
	
	
    /**
     * Deposit amount to account
     * @param id
     * @param amount
     */
    @PUT
    @Path("/{id}/deposit/{amount}")
    public Account depositAmount(@PathParam("id") long id,@PathParam("amount") double amount){
    	
    	Account account = EntityManagerUtil.getEntityManager().find(Account.class, id);
        logger.info("Deposit service: " + amount + " to account # " + account.getAccountNumber());
    	
        if (amount <= 0.0){
        	Response.status(Status.BAD_REQUEST).entity(new GenericResponse("Invalid deposit amount")).build();
        	return account;
        }        
        
        account.setBalance(account.getBalance() + amount);
        
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
		entityManager.getTransaction().begin();		
		entityManager.merge(account);		
		entityManager.getTransaction().commit();
		
		Response.ok(account).build();
		logger.info("Deposit successful");        
        return account;
    }
    

    /**
     * Withdraw amount from account
     * @param id
     * @param amount
     */
    @PUT
    @Path("/{id}/withdraw/{amount}")
    public Account withdrawAmount(@PathParam("id") long id,@PathParam("amount") double amount) {
    	
    	Account account = EntityManagerUtil.getEntityManager().find(Account.class, id);
    	logger.info("Withdraw service: " + (-amount) + " from account # " + account.getAccountNumber());
    	
    	if (amount <= 0){
        	Response.status(Status.BAD_REQUEST).entity(new GenericResponse("Invalid withdrawal amount")).build();
        	return account;
        }    	
    	
    	double newBalance = account.getBalance() - amount;
    	if (newBalance < 0.0) {
    		Response.status(Status.BAD_REQUEST).entity(new GenericResponse("Not sufficient balance on the account")).build();
    		return account;
    	}
        account.setBalance(newBalance);
        
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
		entityManager.getTransaction().begin();		
		entityManager.merge(account);		
		entityManager.getTransaction().commit();
		Response.ok(account).build();
		logger.info("Withdraw successful");
		return account;
    }
	
		
}
