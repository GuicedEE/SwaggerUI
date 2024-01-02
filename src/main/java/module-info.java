import com.guicedee.guicedservlets.openapi.services.IGuicedSwaggerConfiguration;
import com.guicedee.swaggerui.OpenAPIConfiguration;

open module com.guicedee.guicedservlets.swaggerui {
	requires com.guicedee.guicedservlets.openapi;
	
	provides IGuicedSwaggerConfiguration with OpenAPIConfiguration;
	
}
