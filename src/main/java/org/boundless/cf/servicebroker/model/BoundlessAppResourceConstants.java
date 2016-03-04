package org.boundless.cf.servicebroker.model;

public class BoundlessAppResourceConstants {
	
	public static final String GWC_TYPE = "geocache";
	public static final String GEOSERVER_TYPE = "geoserver";

	public static final String GEOSERVER_HOST = "GEOSERVER_HOST";
	public static final String GEOSERVER_PORT = "GEOSERVER_PORT";
 	
	public static final String GEOSERVER_ADMIN_ID = "GEOSERVER_ADMIN_USERNAME";
	public static final String GEOSERVER_ADMIN_PASSWD = "GEOSERVER_ADMIN_PASSWORD";
	
	public static final String GWC_ADMIN_ID = "GWC_ADMIN_USERNAME";
	public static final String GWC_ADMIN_PASSWD = "GWC_ADMIN_PASSWORD";

	public static final String CONSUL_HOST = "CONSUL_HOST";
	public static final String CONSUL_PORT = "CONSUL_PORT";

	public static final String CONTACT_ORGANIZATION_KEY = "CONTACT_ORGANIZATION";
	
	public static final String SERVICE_INSTANCE_NAME = "SERVICE_INSTANCE";
	
	private static final String[] BOUNDLESS_APP_RESOURCE_TYPES = { GEOSERVER_TYPE, GWC_TYPE };

	public static String[] getTypes() {
		return BOUNDLESS_APP_RESOURCE_TYPES;
	}
	
	public static boolean isOfType(String key, String type) {
		return key.startsWith(type);
	}
	
	public static String getAdminIdToken(String type) {
		if (type.equals(GEOSERVER_TYPE))
			return GEOSERVER_ADMIN_ID;
		else 
			return GWC_ADMIN_ID;
	}
	
	public static String getAdminPasswordToken(String type) {
		if (type.equals(GEOSERVER_TYPE))
			return GEOSERVER_ADMIN_PASSWD;
		else 
			return GWC_ADMIN_PASSWD;
	}
};
