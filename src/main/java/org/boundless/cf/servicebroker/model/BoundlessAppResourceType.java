package org.boundless.cf.servicebroker.model;

public class BoundlessAppResourceType {
	
	public static final String GEO_SERVER_TYPE = "geoserver";
	public static final String GEO_WEB_CACHE_TYPE = "geowebcache";

	private static final String[] BOUNDLESS_APP_RESOURCE_TYPES = { GEO_SERVER_TYPE, GEO_WEB_CACHE_TYPE };

	public static String[] getTypes() {
		return BOUNDLESS_APP_RESOURCE_TYPES;
	}
	
	public static boolean isOfType(String key, String type) {
		return key.startsWith(type);
	}
};
