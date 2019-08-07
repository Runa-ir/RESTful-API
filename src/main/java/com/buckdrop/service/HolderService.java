package com.buckdrop.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.buckdrop.jpa.EntityManagerUtil;
import com.buckdrop.model.Holder;
import com.buckdrop.model.GenericResponse;
import com.buckdrop.model.View;
import com.fasterxml.jackson.annotation.JsonView;

@Path("/holder")
public class HolderService {

	protected final Logger logger = LogManager.getLogger(this.getClass());
	
	@GET
	@Produces(value=MediaType.APPLICATION_JSON)
	@JsonView(View.Base.class)
	public Response getHolders(){
		logger.info("Get the list of holders");

		List<Holder> holders = EntityManagerUtil.getEntityManager().createQuery("Select a from Holder a", Holder.class).getResultList();
		
		if(holders!=null && holders.size()>0){
			return Response.ok(holders).build();
		}
		
		return Response.status(Status.NOT_FOUND).entity(new GenericResponse("Holder list is empty")).build();
	}
	

	@GET
	@Path("/{id}")
	@Produces(value=MediaType.APPLICATION_JSON)
	@JsonView(View.HolderExtended.class)
	public Response getHolder(@PathParam("id") long id){
		logger.info("Get the holder whose id is "+id);
		Holder holder = EntityManagerUtil.getEntityManager().find(Holder.class, id);
		
		if(holder!=null){
			return Response.ok(holder).build();
		}
		
		return Response.status(Status.NOT_FOUND).entity(new GenericResponse("Holder " + id + " does not exist")).build();
	}
	
}
