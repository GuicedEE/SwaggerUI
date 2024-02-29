import com.guicedee.guicedinjection.interfaces.*;
import com.guicedee.swaggerui.test.*;

module guiced.rest.swaggerui.test {
	requires com.guicedee.guicedservlets.swaggerui;
	
	requires java.net.http;
	
	requires org.junit.jupiter.api;
	requires org.slf4j;
	requires org.slf4j.simple;
	
	requires jakarta.ws.rs;
	requires com.google.guice;
	requires com.guicedee.client;
	requires com.guicedee.guicedservlets.undertow;
	
	provides IGuiceModule with RestTestBinding;
	
}