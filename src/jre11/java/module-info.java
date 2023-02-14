import com.guicedee.guicedservlets.services.*;
import com.guicedee.swaggerui.*;

open module com.guicedee.services.swaggerui {

	requires com.guicedee.guicedservlets;
	//Test
	requires static java.net.http;
	
	provides IGuiceSiteBinder with ServeSwaggerUI;
}
