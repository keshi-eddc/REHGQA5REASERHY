package com.edmi.site.autohome.config;

public class CarsSiteIdSupport {
	
	public static String SITE_PCAUTO = "pcauto";

	public static String SITE_PCAUTO_HIVE = "p01";

	public static String SITE_AUTOHOME = "autohome";

	public static String SITE_AUTOHOME_HIVE = "p02";

	public static String SITE_XCAR = "xcar";

	public static String SITE_XCAR_HIVE = "p03";
	
	public static String SITE_BITAUTO = "bitauto";

	public static String SITE_BITAUTO_HIVE = "p04";
	
	public static final int SITE_ID_BOUND_BRAND = 100 * 1000;
	
	public static final int SITE_ID_FACTORY_BRAND = 1000 * 1000;
	
	public static final int SITE_ID_BOUND_SUB_BRAND = 10 * 1000 * 1000;
	
	public static final int SITE_ID_BOUND_SERIES_BRAND = 100 * 1000 * 1000;
	
	public static final long SITE_ID_BOUND_MODEL_BRAND = 1000L * 1000L * 1000L;
	
	public static final long SITE_ID_BOUND_ARTICLE = 10L * 1000L * 1000L * 1000L;
	
	public static final long SITE_ID_BOUND_REPUTATION = 100L * 1000L * 1000L * 1000L;
	
	public static final long SITE_ID_BOUND_TOPIC = 1000L * 1000L * 1000L * 1000L;
	
	public static final long SITE_ID_BOUND_USER = 10L * 1000L * 1000L * 1000L * 1000L;
	
	public static final long SITE_ID_BOUND_ARTICLE_COMMENT = 10L * 1000L * 1000L * 1000L * 1000L * 1000L;
	
	public static final long SITE_ID_BOUND_REPUTATION_COMMENT = 100L * 1000L * 1000L * 1000L * 1000L * 1000L;
	
	public static final long SITE_ID_BOUND_TOPIC_CONTENT = 1000L * 1000L * 1000L * 1000L * 1000L * 1000L;
	
	public static void main(String[] args) {
		System.out.println(SITE_ID_BOUND_SERIES_BRAND);
	}

}
