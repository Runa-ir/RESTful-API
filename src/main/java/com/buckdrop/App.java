package com.buckdrop;

import java.io.IOException;
import java.net.Socket;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;

import com.buckdrop.jpa.EntityManagerUtil;
import com.buckdrop.model.Account;
import com.buckdrop.model.AccountType;
import com.buckdrop.model.Holder;

public class App {
	
	protected final static Logger logger = LogManager.getLogger("App");
	
	static final int port = 7070;
	static final String h2WebPort = "7071";
	
    public static void main( String[] args ) throws Exception{

    	
		if (isPortAvailable(Integer.parseInt(h2WebPort))) {

	    	org.h2.tools.Server webInterface = org.h2.tools.Server.createWebServer(new String[]
																	{"-web","-webAllowOthers","-webPort",h2WebPort});
	    	webInterface.start();   
	    	
	    	ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
	    	servletContextHandler.setContextPath("/");
	
	    	Server jettyServer = new Server(port);
	    	jettyServer.setHandler(servletContextHandler);    	
	
	    	ServletHolder jerseyServlet = servletContextHandler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
	    	jerseyServlet.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.buckdrop");
	    	jerseyServlet.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, "org.glassfish.jersey.jackson.JacksonFeature");
	
	    	generateInitialContent();
		    	
			try {
				jettyServer.start();
				jettyServer.join();
			} finally {
				jettyServer.destroy();
			}
		} else {
			logger.info("Port " + h2WebPort + " is not available. Please, free it or choose another one.");
		}
    }
    
    public static void generateInitialContent(){
    	
    	EntityManager entityManager = EntityManagerUtil.getEntityManager();
    	entityManager.getTransaction().begin();
    	
    	// first holder
    	Holder holder1 = new Holder("Valentin", "Romanov");
    	entityManager.persist(holder1);
    	Account account1 = new Account(123456789, 1000.0, AccountType.CHECKING);
    	account1.setHolder(holder1);    	
    	entityManager.persist(account1);
    	Account account2 = new Account(987654321, 2000.0, AccountType.SAVING);
    	account2.setHolder(holder1);
    	entityManager.persist(account2);    	
    	
    	holder1.getAccounts().add(account1);
    	holder1.getAccounts().add(account2);
    	entityManager.merge(holder1);
    	
    	// second holder
    	Holder author2 = new Holder("Nikolay","Storonsky");
    	entityManager.persist(author2);    	
    	Account account3 = new Account(100010001, 999999.0, AccountType.CHECKING);
    	account3.setHolder(author2);
    	entityManager.persist(account3);
    	
    	author2.getAccounts().add(account3);
    	entityManager.merge(author2);    	
    	
    	entityManager.getTransaction().commit();   	
    }
    
  
    private static boolean isPortAvailable(int port) {
        Socket s = null;
        try {
            s = new Socket("localhost", port);
            return false;
        } catch (IOException e) {
            return true;
        } finally {
            if( s != null){
                try {
                    s.close();
                } catch (IOException e) {
                    throw new RuntimeException("You should handle this error." , e);
                }
            }
        }
    }
        
}
