package com.app.util;

public class IntegrationFactory {
	public SystemsIntegration getSystemsIntegrationClass(String systemCode) {
		
		return new SystemsIntegration(systemCode);
	}
}
