package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.buckdrop.App;
import com.buckdrop.jpa.EntityManagerUtil;
import com.buckdrop.model.Account;
import com.buckdrop.model.Holder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestRestAPI {
	
    private static Server server;
    private static String servletAddress;

    @BeforeClass
    public static void startJetty() throws Exception
    {
        // Create Server
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // auto-bind to available port
        server.addConnector(connector);

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
    	servletContextHandler.setContextPath("/");
    	server.setHandler(servletContextHandler);    	

    	ServletHolder jerseyServlet = servletContextHandler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
    	jerseyServlet.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.buckdrop");
    	jerseyServlet.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, "org.glassfish.jersey.jackson.JacksonFeature");
        
    	// Generate database content
        App.generateInitialContent();

        // Start Server
        server.start();

        // Determine Base URI for Server
        String host = connector.getHost();
        if (host == null)
        {
            host = "localhost";
        }
        int port = connector.getLocalPort();
        servletAddress = String.format("http://%s:%d/",host,port);        
    }

    @AfterClass
    public static void stopJetty()
    {
        try
        {
            server.stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    
    /** 
	Test : JSON payload
	**/
    @Test
    public void testJSONPayload() throws Exception
    {
    	
    	String id = "2";        
        HttpUriRequest request = new HttpGet(servletAddress + "holder/" + id);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertTrue(httpResponse.getStatusLine().getStatusCode() == 200);
    
        String json = EntityUtils.toString(httpResponse.getEntity());
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Holder holder = mapper.readValue(json, Holder.class);
        
        assertEquals(holder.getName(), "Nikolay");
        assertEquals(holder.getLastname(), "Storonsky");
    }
    
 
    /** AccountService
	Test GET: Inquire account balance by account ID
	**/
    @Test
    public void testGetBalanceByID() throws Exception
    {
    	
    	String id = "2";        
        HttpUriRequest request = new HttpGet(servletAddress + "account/" + id + "/balance");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);    
        assertTrue(httpResponse.getStatusLine().getStatusCode() == 200);
        
        String balance = EntityUtils.toString(httpResponse.getEntity());
	  	assertEquals(balance, "2000.0");

    }
    
    /** AccountService
	Test GET: Inquired account doesn't exist
	**/
    @Test
    public void testInquiredAccountDoesNotExist() throws Exception {
      
        HttpUriRequest request = new HttpGet( servletAddress + "account/8");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertTrue(httpResponse.getStatusLine().getStatusCode() == 404);
    }
    
	
    /** AccountService
	Test PUT: Transfer amount from account to account (insufficient funds)
	**/
    @Test
    public void testTransferFromAccountToAccount_InsufficientFunds() throws Exception {
    	
    	String IDOrigin = "1";   
    	String IDBenef = "3";  
    	double amount = 14567.89;
    	
    	EntityManager entityManager = EntityManagerUtil.getEntityManager();
        Account accountOrigin = entityManager.getReference(Account.class, new Long(IDOrigin));
    	double initialBalanceOrigin = accountOrigin.getBalance();
    	Account accountBenef = entityManager.getReference(Account.class, new Long(IDBenef));
    	double initialBalanceBenef = accountBenef.getBalance();    	
    	
        HttpUriRequest request = new HttpPut(servletAddress + "account/" + IDOrigin + "/transfer/" + IDBenef + "/" + Double.toString(amount));
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request); 
        
        assertTrue(accountOrigin.getBalance() == initialBalanceOrigin);
        assertTrue(accountBenef.getBalance() == initialBalanceBenef);
    }
    
    
    /** AccountService
	Test PUT: Transfer amount from account to account (successful)
	**/
    @Test
    public void testTransferFromAccountToAccount_Successful() throws Exception {
    	
    	String IDOrigin = "1";   
    	String IDBenef = "3";  
    	double amount = 156.89;
    	
    	EntityManager entityManager1 = EntityManagerUtil.getEntityManager();
        Account accountOrigin = entityManager1.getReference(Account.class, new Long(IDOrigin));
    	double initialBalanceOrigin = accountOrigin.getBalance();
    	Account accountBenef = entityManager1.getReference(Account.class, new Long(IDBenef));
    	double initialBalanceBenef = accountBenef.getBalance(); 
    	    	
        HttpUriRequest request = new HttpPut(servletAddress + "account/" + IDOrigin + "/transfer/" + IDBenef + "/" + Double.toString(amount));
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);  
        
        assertTrue(accountOrigin.getBalance() == initialBalanceOrigin - amount);
        assertTrue(accountBenef.getBalance() == initialBalanceBenef + amount);
    }
	

}
