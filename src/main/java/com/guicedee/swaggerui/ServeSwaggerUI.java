package com.guicedee.swaggerui;

import com.guicedee.guicedservlets.*;
import com.guicedee.guicedservlets.services.*;
import jakarta.servlet.http.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;

public class ServeSwaggerUI implements IGuiceSiteBinder<GuiceSiteInjectorModule>
{
	@Override
	public Integer sortOrder()
	{
		return -100;
	}
	
	@Override
	public void onBind(GuiceSiteInjectorModule module)
	{
		module
				.serveSite("/swagger-ui")
				.with(new StaticResourceServlet()
				{
					@Override
					protected StaticResource getStaticResource(HttpServletRequest request) throws IllegalArgumentException
					{
						String pathInfo = request.getPathInfo();
						
						if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo))
						{
							pathInfo = "/index.html";
						}
						
						String name = null;
						try
						{
							name = URLDecoder.decode(pathInfo.substring(1), StandardCharsets.UTF_8.name());
						}
						catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
						InputStream resourceAsStream = getClass().getResourceAsStream(name);
						String finalName = name;
						return new StaticResource()
						{
							@Override
							public long getLastModified()
							{
								return 0l;
							}
							
							@Override
							public InputStream getInputStream() throws IOException
							{
								return resourceAsStream;
							}
							
							@Override
							public String getFileName()
							{
								return finalName;
							}
							
							@Override
							public long getContentLength()
							{
								return 0l;
							}
						};
					}
				});
	}
}
