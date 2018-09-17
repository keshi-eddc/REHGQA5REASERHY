package com.edmi.site.dianping.http;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.cookie.DianpingShopDetailCookie;
import com.edmi.site.dianping.entity.IpTest;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.RequestType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.bean.HttpResponse;
import fun.jerry.httpclient.core.HttpClientSupport;
import fun.jerry.proxy.StaticProxySupport;
import fun.jerry.proxy.entity.Proxy;

public class DianPingCommonRequest extends HttpClientSupport {

	private static Logger log = LogSupport.getDianpinglog();
	
	private static Logger log_test = LogSupport.getJdlog();
	
	public final static BlockingQueue<String> COOKIES_SHOPLIST = new ArrayBlockingQueue<>(100);
	
	static {
		COOKIES_SHOPLIST.add("_lxsdk=165a7661e5dc7-051d17ac9926ad-37664109-144000-165a7661e5ec8; _lxsdk_cuid=165a7661e5dc7-051d17ac9926ad-37664109-144000-165a7661e5ec8; _hc.v=5180b841-aa5d-f564-90f7-6f835e0158e6.1536111812; _lxsdk_s=165a7661e5f-eac-e24-e65%7C%7C20; s_ViewType=10;");
		COOKIES_SHOPLIST.add("_lxsdk=165a76870edc8-068e1cf1afaf81-37664109-144000-165a76870edc8; _lxsdk_cuid=165a76870edc8-068e1cf1afaf81-37664109-144000-165a76870edc8; _hc.v=fe09739c-2dae-9974-70ff-82f5eae6647c.1536111964; _lxsdk_s=165a76870ef-9e2-337-738%7C%7C20; s_ViewType=10;");
		COOKIES_SHOPLIST.add("_lxsdk=165a7690e35c8-0c6a1fcb896159-37664109-144000-165a7690e36c8; _lxsdk_cuid=165a7690e35c8-0c6a1fcb896159-37664109-144000-165a7690e36c8; _hc.v=37fc46a1-eec8-5b52-e2a1-8ebf3f8208a6.1536112005; _lxsdk_s=165a7690e38-c22-dbc-c52%7C%7C20; s_ViewType=10;");
		COOKIES_SHOPLIST.add("_lxsdk=165a7699dd40-017287503da158-37664109-144000-165a7699dd5c8; _lxsdk_cuid=165a7699dd40-017287503da158-37664109-144000-165a7699dd5c8; _hc.v=861e2cd8-20ca-e6dd-9bb4-4ff1e49cd133.1536112041; _lxsdk_s=165a7699dd7-1b9-765-53a%7C%7C20; s_ViewType=10;");
		COOKIES_SHOPLIST.add("_lxsdk=165a79995ddc8-096ca5d1df70b2-37664109-144000-165a79995dec8; _lxsdk_cuid=165a79995ddc8-096ca5d1df70b2-37664109-144000-165a79995dec8; _hc.v=10711107-b667-6433-8445-25d25f8108ae.1536115185; _lxsdk_s=165a79995df-c62-fab-a89%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79a3085c8-0dbb78b4203f7-37664109-144000-165a79a3085c8; _lxsdk_cuid=165a79a3085c8-0dbb78b4203f7-37664109-144000-165a79a3085c8; _hc.v=20730387-160b-fc45-bb8a-58403a42f75b.1536115225; _lxsdk_s=165a79a3086-f96-b79-8d%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79a90b9c8-0465a1e4e3de82-37664109-144000-165a79a90bac8; _lxsdk_cuid=165a79a90b9c8-0465a1e4e3de82-37664109-144000-165a79a90bac8; _hc.v=296d0ed6-b5d4-1e37-4969-26072ab5af03.1536115249; _lxsdk_s=165a79a90bb-5e4-f87-51a%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79a9a07c8-06a2faf5ddc003-37664109-144000-165a79a9a08c8; _lxsdk_cuid=165a79a9a07c8-06a2faf5ddc003-37664109-144000-165a79a9a08c8; _hc.v=22b6cef4-495e-cfa4-7959-97e7b77226c2.1536115252; _lxsdk_s=165a79a9a09-4c0-179-620%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79b5071c8-0f59f7ca57c081-37664109-144000-165a79b5071c8; _lxsdk_cuid=165a79b5071c8-0f59f7ca57c081-37664109-144000-165a79b5071c8; _hc.v=7135d08c-06b4-f4f8-1c64-790b009edbe1.1536115299; _lxsdk_s=165a79b5073-4c5-f02-714%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79b96f1c8-0e87c4e110d418-37664109-144000-165a79b96f2c8; _lxsdk_cuid=165a79b96f1c8-0e87c4e110d418-37664109-144000-165a79b96f2c8; _hc.v=3c5bb8b3-dc0f-04fc-bc98-af6796354489.1536115317; _lxsdk_s=165a79b96f3-df0-b30-e24%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79c8b37c8-03fcfb6fc9ff0e-37664109-144000-165a79c8b37c8; _lxsdk_cuid=165a79c8b37c8-03fcfb6fc9ff0e-37664109-144000-165a79c8b37c8; _hc.v=b3d59e80-d21c-b69f-25dc-903f47384dcc.1536115379; _lxsdk_s=165a79c8b39-c2d-22-46f%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79d26ebc8-0be0355dff521b-37664109-144000-165a79d26ebc8; _lxsdk_cuid=165a79d26ebc8-0be0355dff521b-37664109-144000-165a79d26ebc8; _hc.v=5358afd1-5fce-6074-cc0b-b5e0c95f644d.1536115419; _lxsdk_s=165a79d26ed-bdb-0a2-3%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79d675fc8-001690b6388b24-37664109-144000-165a79d676296; _lxsdk_cuid=165a79d675fc8-001690b6388b24-37664109-144000-165a79d676296; _hc.v=168b0252-ad93-935b-49ad-e4aa13567c90.1536115436; _lxsdk_s=165a79d6766-943-9f-3ec%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79d80f7c8-01efa6cf478cae-37664109-144000-165a79d80f8c8; _lxsdk_cuid=165a79d80f7c8-01efa6cf478cae-37664109-144000-165a79d80f8c8; _hc.v=e7dd3e26-05c1-6d7b-3b33-00031ca6e88e.1536115442; _lxsdk_s=165a79d80f9-a38-978-506%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79dbc706-03012798ab998c-37664109-144000-165a79dbc711f; _lxsdk_cuid=165a79dbc706-03012798ab998c-37664109-144000-165a79dbc711f; _hc.v=58ba491c-0d53-c32c-ede7-4034b19100c5.1536115457; _lxsdk_s=165a79dbc73-d94-22e-a41%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79de9dfc8-0a2ca301143bea-37664109-144000-165a79de9e0c8; _lxsdk_cuid=165a79de9dfc8-0a2ca301143bea-37664109-144000-165a79de9e0c8; _hc.v=80540bf4-ce41-486c-e768-7cde300972c6.1536115469; _lxsdk_s=165a79de9e1-280-0ce-3c%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79e7548c8-0f59149aff9056-37664109-144000-165a79e7549c8; _lxsdk_cuid=165a79e7548c8-0f59149aff9056-37664109-144000-165a79e7549c8; _hc.v=ecd4dbbe-6625-5a1c-bbf6-1a7f1cb31d43.1536115505; _lxsdk_s=165a79e754b-992-cd2-642%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79e6b47c8-0224a2be1103de-37664109-144000-165a79e6b49c8; _lxsdk_cuid=165a79e6b47c8-0224a2be1103de-37664109-144000-165a79e6b49c8; _hc.v=b101e1d6-e881-3430-8696-d5e8987b767f.1536115502; _lxsdk_s=165a79e6b4a-699-d0c-205%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79f3f1bc8-08449ab2a639bb-37664109-144000-165a79f3f1dc8; _lxsdk_cuid=165a79f3f1bc8-08449ab2a639bb-37664109-144000-165a79f3f1dc8; _hc.v=b675ba29-d6ab-e727-e91b-dcc921e5295b.1536115556; _lxsdk_s=165a79f3f1f-622-09a-6fe%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79f6907c8-0ab6e5a7f1fda7-37664109-144000-165a79f6908c8; _lxsdk_cuid=165a79f6907c8-0ab6e5a7f1fda7-37664109-144000-165a79f6908c8; _hc.v=1bebc74c-6580-f84e-e08b-8885bc1c4104.1536115567; _lxsdk_s=165a79f690b-667-e76-d7f%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a79fb2f455-0ce7aaf875be4d-37664109-144000-165a79fb2f5c8; _lxsdk_cuid=165a79fb2f455-0ce7aaf875be4d-37664109-144000-165a79fb2f5c8; _hc.v=a9a991f7-0243-a557-c861-5ff525b4f82a.1536115586; _lxsdk_s=165a79fb2f6-813-d70-2ff%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a03a08c0-0f1e4a899a44be-37664109-144000-165a7a03a0ac8; _lxsdk_cuid=165a7a03a08c0-0f1e4a899a44be-37664109-144000-165a7a03a0ac8; _hc.v=62e0d027-9243-5feb-f7c3-5ddd5e6a80af.1536115620; _lxsdk_s=165a7a03a0d-966-ccd-ef2%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a05153c8-09c11ea74bab02-37664109-144000-165a7a05154c8; _lxsdk_cuid=165a7a05153c8-09c11ea74bab02-37664109-144000-165a7a05154c8; _hc.v=ff4bf78f-f23e-3abb-cecc-c8a15190d96f.1536115626; _lxsdk_s=165a7a05155-062-69f-6a8%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a0c823c8-0427ce768ed2ef-37664109-144000-165a7a0c824c8; _lxsdk_cuid=165a7a0c823c8-0427ce768ed2ef-37664109-144000-165a7a0c824c8; _hc.v=131830bb-2f27-0a6f-6d81-748e9e84fa47.1536115657; _lxsdk_s=165a7a0c826-2e6-d5e-c3a%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a1aa6ec8-0789228c8115c8-37664109-144000-165a7a1aa6fc8; _lxsdk_cuid=165a7a1aa6ec8-0789228c8115c8-37664109-144000-165a7a1aa6fc8; _hc.v=02f99e7c-5aee-ca5e-8d26-8db18877bd59.1536115715; _lxsdk_s=165a7a1aa70-a59-e5a-074%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a1d92dc8-07105794216cd8-37664109-144000-165a7a1d92fc8; _lxsdk_cuid=165a7a1d92dc8-07105794216cd8-37664109-144000-165a7a1d92fc8; _hc.v=510612d1-0618-2e37-9b37-af72572271c3.1536115727; _lxsdk_s=165a7a1d935-aed-bd0-d2a%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a708a9c8-03025be220dfc7-37664109-144000-165a7a708abc8; _lxsdk_cuid=165a7a708a9c8-03025be220dfc7-37664109-144000-165a7a708abc8; _hc.v=e2138a8d-85e3-827b-91c1-ac28e45ca2b2.1536116067; _lxsdk_s=165a7a708ae-5b2-d99-97e%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a70c0dc8-033ac51c59bdc9-37664109-144000-165a7a70c0ec8; _lxsdk_cuid=165a7a70c0dc8-033ac51c59bdc9-37664109-144000-165a7a70c0ec8; _hc.v=77fa4070-85a5-2c8d-8c03-1f245f0cd2fd.1536116067; _lxsdk_s=165a7a70c13-2ed-384-0c%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a74c93c8-094a04775f6d3a-37664109-144000-165a7a74c9471; _lxsdk_cuid=165a7a74c93c8-094a04775f6d3a-37664109-144000-165a7a74c9471; _hc.v=c3af9f91-2d55-cfcb-0d4a-e914736539a1.1536116084; _lxsdk_s=165a7a74c97-490-d78-2a2%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a791e6c8-063b5a4155fdf-37664109-144000-165a7a791e7c8; _lxsdk_cuid=165a7a791e6c8-063b5a4155fdf-37664109-144000-165a7a791e7c8; _hc.v=3459b0d3-9717-e0b5-31e7-999a74367f8b.1536116102; _lxsdk_s=165a7a791ec-f23-4df-33%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a7a6e9c8-0d403fd3576fec-37664109-144000-165a7a7a6ebae; _lxsdk_cuid=165a7a7a6e9c8-0d403fd3576fec-37664109-144000-165a7a7a6ebae; _hc.v=8ad40562-dbe2-b0bb-ba22-a8bd865bab5b.1536116107; _lxsdk_s=165a7a7a6ed-e98-70d-260%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a7d178c8-0e831f2fca6ac1-37664109-144000-165a7a7d178c8; _lxsdk_cuid=165a7a7d178c8-0e831f2fca6ac1-37664109-144000-165a7a7d178c8; _hc.v=a51e4a41-fa64-01b8-f4b2-119ee3cf0349.1536116118; _lxsdk_s=165a7a7d17a-cf9-b2b-bcc%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a89d330-0fc5956b56bdf5-37664109-144000-165a7a89d35c8; _lxsdk_cuid=165a7a89d330-0fc5956b56bdf5-37664109-144000-165a7a89d35c8; _hc.v=118678fd-acd6-6c10-8075-346f5da01de2.1536116170; _lxsdk_s=165a7a89d39-c63-229-ead%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a8f715c8-0baab6ae412259-37664109-144000-165a7a8f716c8; _lxsdk_cuid=165a7a8f715c8-0baab6ae412259-37664109-144000-165a7a8f716c8; _hc.v=290947d4-337a-d019-7615-e251af8464c3.1536116193; _lxsdk_s=165a7a8f718-8da-df7-9e7%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a958d73d-033a4027a8042c-37664109-144000-165a7a958d9c8; _lxsdk_cuid=165a7a958d73d-033a4027a8042c-37664109-144000-165a7a958d9c8; _hc.v=a1d8da4b-194f-298d-665c-1684b7b69e96.1536116218; _lxsdk_s=165a7a958da-fb6-b70-074%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7a9b2d7c8-00b990a004b2d3-37664109-144000-165a7a9b2d71e; _lxsdk_cuid=165a7a9b2d7c8-00b990a004b2d3-37664109-144000-165a7a9b2d71e; _hc.v=7a9544b9-9f56-fb77-89c0-07bd99b627cc.1536116241; _lxsdk_s=165a7a9b2dd-e57-a84-90b%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7ab5146bc-041712f163e012-37664109-144000-165a7ab5148c8; _lxsdk_cuid=165a7ab5146bc-041712f163e012-37664109-144000-165a7ab5148c8; _hc.v=1b7c5a9a-9645-6af8-abb4-2d801e07e282.1536116347; _lxsdk_s=165a7ab514c-817-bef-875%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7abf707c8-02931e072747ed-37664109-144000-165a7abf709c8; _lxsdk_cuid=165a7abf707c8-02931e072747ed-37664109-144000-165a7abf709c8; _hc.v=6e5756b2-8963-9ccb-2580-a7b4c3860049.1536116390; _lxsdk_s=165a7abf70a-50b-071-0c4%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7abf40fc8-05b72672764abe-37664109-144000-165a7abf411c8; _lxsdk_cuid=165a7abf40fc8-05b72672764abe-37664109-144000-165a7abf411c8; _hc.v=e8dd75c8-51e7-3fa6-bdec-fc4c47878819.1536116389; _lxsdk_s=165a7abf413-225-10a-3d%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7acb4bcc8-08edc41c713d59-37664109-144000-165a7acb4bdc8; _lxsdk_cuid=165a7acb4bcc8-08edc41c713d59-37664109-144000-165a7acb4bdc8; _hc.v=1f284bda-391d-32a2-5443-7af2885ead2e.1536116438; _lxsdk_s=165a7acb4be-8cc-804-53%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7acbaebc8-0ea0a91b64fa06-37664109-144000-165a7acbaedc8; _lxsdk_cuid=165a7acbaebc8-0ea0a91b64fa06-37664109-144000-165a7acbaedc8; _hc.v=1a6d2d1d-8224-a27e-53e7-5edda2dc738a.1536116441; _lxsdk_s=165a7acbaf0-44e-255-500%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7ada62816-0ee179939c0aa-37664109-144000-165a7ada629c8; _lxsdk_cuid=165a7ada62816-0ee179939c0aa-37664109-144000-165a7ada629c8; _hc.v=86a3bfc2-3174-7435-5bc7-a4c2b978e594.1536116500; _lxsdk_s=165a7ada62d-c8a-b12-be1%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7adc822c8-0e25a521064321-37664109-144000-165a7adc82338; _lxsdk_cuid=165a7adc822c8-0e25a521064321-37664109-144000-165a7adc82338; _hc.v=50a01b8d-4278-8916-4b99-6ff4436c1b5d.1536116509; _lxsdk_s=165a7adc826-beb-303-326%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7aed686c8-07f1e9ecf3d856-37664109-144000-165a7aed686c8; _lxsdk_cuid=165a7aed686c8-07f1e9ecf3d856-37664109-144000-165a7aed686c8; _hc.v=bd545b43-1b34-0f1d-8a43-7082bc55c5a8.1536116578; _lxsdk_s=165a7aed688-dd8-b38-253%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7aed6e9c8-0330b564e92dc9-37664109-144000-165a7aed6eac8; _lxsdk_cuid=165a7aed6e9c8-0330b564e92dc9-37664109-144000-165a7aed6eac8; _hc.v=300213d2-20ab-0dec-adcb-d4db2a4d149d.1536116578; _lxsdk_s=165a7aed6ec-f5-c3b-06d%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b0f3abc8-009107034e595-37664109-144000-165a7b0f3abc8; _lxsdk_cuid=165a7b0f3abc8-009107034e595-37664109-144000-165a7b0f3abc8; _hc.v=1dc47d9b-44f6-6716-4855-1b9331123c96.1536116716; _lxsdk_s=165a7b0f3ad-216-c13-52f%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b0fce4c8-06a1da1223ed14-37664109-144000-165a7b0fce4ba; _lxsdk_cuid=165a7b0fce4c8-06a1da1223ed14-37664109-144000-165a7b0fce4ba; _hc.v=5b64e724-f171-2524-f662-829fe862c937.1536116719; _lxsdk_s=165a7b0fce7-39e-6f-c83%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b171125f-078c7cd2216bd6-37664109-144000-165a7b17114c8; _lxsdk_cuid=165a7b171125f-078c7cd2216bd6-37664109-144000-165a7b17114c8; _hc.v=c5b753f4-d0b2-4bca-7390-e7e676e136db.1536116749; _lxsdk_s=165a7b17114-68a-047-0e2%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b19a08c8-0f16af6bd02d84-37664109-144000-165a7b19a09c8; _lxsdk_cuid=165a7b19a08c8-0f16af6bd02d84-37664109-144000-165a7b19a09c8; _hc.v=c0df1e6b-227d-3f77-a4dd-24dc75a4796f.1536116759; _lxsdk_s=165a7b19a0b-967-1f3-1d2%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b252c4c8-045fe4bd47ae8b-37664109-144000-165a7b252c5c8; _lxsdk_cuid=165a7b252c4c8-045fe4bd47ae8b-37664109-144000-165a7b252c5c8; _hc.v=46e1c51d-f704-2cc1-f345-fe3d42aa84fa.1536116806; _lxsdk_s=165a7b252c7-474-db5-8d5%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b2dc9ec8-04c1616a684201-37664109-144000-165a7b2dc9ed; _lxsdk_cuid=165a7b2dc9ec8-04c1616a684201-37664109-144000-165a7b2dc9ed; _hc.v=479f9b6d-e76a-b221-f56e-c52ad9cedeb1.1536116842; _lxsdk_s=165a7b2dca0-026-cc-4ce%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b49dc3c8-0e364ba82a0af8-37664109-144000-165a7b49dc8c8; _lxsdk_cuid=165a7b49dc3c8-0e364ba82a0af8-37664109-144000-165a7b49dc8c8; _hc.v=d2f175cf-e7bb-092c-1bd3-e09d47a2422a.1536116957; _lxsdk_s=165a7b49dcc-2ec-2c6-0cb%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b45a142b-0b9bbb6b6b0c02-37664109-144000-165a7b45a16c8; _lxsdk_cuid=165a7b45a142b-0b9bbb6b6b0c02-37664109-144000-165a7b45a16c8; _hc.v=3b285537-27ea-dab5-dbeb-c9f1af140814.1536116939; _lxsdk_s=165a7b45a18-b77-f1e-a5e%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b4f09fc8-0ed6ecbe9fee06-37664109-144000-165a7b4f0a047; _lxsdk_cuid=165a7b4f09fc8-0ed6ecbe9fee06-37664109-144000-165a7b4f0a047; _hc.v=b7f20065-6005-7ea9-2f70-00fe03b3782e.1536116978; _lxsdk_s=165a7b4f0a3-674-18c-d35%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b4f11ac8-01aaa06181f48c-37664109-144000-165a7b4f11bb9; _lxsdk_cuid=165a7b4f11ac8-01aaa06181f48c-37664109-144000-165a7b4f11bb9; _hc.v=49cf73b7-1f62-a2d0-def7-2823ec000ffa.1536116978; _lxsdk_s=165a7b4f11d-ab8-00c-500%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b5d890c8-08482adeef4018-37664109-144000-165a7b5d892c8; _lxsdk_cuid=165a7b5d890c8-08482adeef4018-37664109-144000-165a7b5d892c8; _hc.v=280533f1-0372-5d1c-e507-fb4981def331.1536117038; _lxsdk_s=165a7b5d894-96e-f4f-152%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b5df0cc8-074a4c876fbc7b-37664109-144000-165a7b5df0ec8; _lxsdk_cuid=165a7b5df0cc8-074a4c876fbc7b-37664109-144000-165a7b5df0ec8; _hc.v=201cced3-2535-1c41-bdde-631b39ac9004.1536117039; _lxsdk_s=165a7b5df11-485-d51-e67%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b67eb4c8-03b5676cd401e4-37664109-144000-165a7b67eb6c8; _lxsdk_cuid=165a7b67eb4c8-03b5676cd401e4-37664109-144000-165a7b67eb6c8; _hc.v=ea5830c8-4043-3388-8c01-752064a07721.1536117080; _lxsdk_s=165a7b67eb8-6fb-0d0-34e%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b710bdc8-08f29d8669c2a-37664109-144000-165a7b710bfc8; _lxsdk_cuid=165a7b710bdc8-08f29d8669c2a-37664109-144000-165a7b710bfc8; _hc.v=f292aae6-d918-929d-0a6b-f010c76b2785.1536117117; _lxsdk_s=165a7b710c3-d1e-735-96%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b6ffb0c8-0d201a99712891-37664109-144000-165a7b6ffb1c8; _lxsdk_cuid=165a7b6ffb0c8-0d201a99712891-37664109-144000-165a7b6ffb1c8; _hc.v=95151007-632b-bc68-ffc8-f4950a1280de.1536117113; _lxsdk_s=165a7b6ffb4-083-799-749%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b775e1c8-0e8b68f8570a6b-37664109-144000-165a7b775e1c8; _lxsdk_cuid=165a7b775e1c8-0e8b68f8570a6b-37664109-144000-165a7b775e1c8; _hc.v=e4a6cd8d-017b-c96d-9bee-688de2cece86.1536117143; _lxsdk_s=165a7b775e2-05e-019-fbb%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b78fa3c8-0f42f52853a352-37664109-144000-165a7b78fa3c8; _lxsdk_cuid=165a7b78fa3c8-0f42f52853a352-37664109-144000-165a7b78fa3c8; _hc.v=96c90bf5-c89f-3e10-bbb2-ae45ab8cc431.1536117150; _lxsdk_s=165a7b78fa4-077-595-e52%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b7b8b1c8-018aa98acfe1b9-37664109-144000-165a7b7b8b2c8; _lxsdk_cuid=165a7b7b8b1c8-018aa98acfe1b9-37664109-144000-165a7b7b8b2c8; _hc.v=d607e399-f6fa-830e-d26d-7b21f5715523.1536117160; _lxsdk_s=165a7b7b8b3-d30-8af-096%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b824f8c8-02acc0c975e943-37664109-144000-165a7b824f8c8; _lxsdk_cuid=165a7b824f8c8-02acc0c975e943-37664109-144000-165a7b824f8c8; _hc.v=e0d185b9-0713-57e7-e96e-aba62b86b3c8.1536117188; _lxsdk_s=165a7b824fa-64f-39e-9d8%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b87dc3c8-0ce6da4873abea-37664109-144000-165a7b87dc4c8; _lxsdk_cuid=165a7b87dc3c8-0ce6da4873abea-37664109-144000-165a7b87dc4c8; _hc.v=d3ba095e-b66b-7ca3-9a7e-fd5a4d489c01.1536117211; _lxsdk_s=165a7b87dc5-d30-4e4-041%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b83a95c8-0beb05239f6f88-37664109-144000-165a7b83a95c8; _lxsdk_cuid=165a7b83a95c8-0beb05239f6f88-37664109-144000-165a7b83a95c8; _hc.v=137ec60b-9eaa-482c-f97a-4d6c171acb42.1536117193; _lxsdk_s=165a7b83a99-62b-3b3-640%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b8c939c8-09956d24adb566-37664109-144000-165a7b8c93ac8; _lxsdk_cuid=165a7b8c939c8-09956d24adb566-37664109-144000-165a7b8c93ac8; _hc.v=03697ba0-f262-4d84-0f08-4b82d09e59ea.1536117230; _lxsdk_s=165a7b8c93b-f65-28-944%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b8e37fc8-0964d3d940d8e9-37664109-144000-165a7b8e380c8; _lxsdk_cuid=165a7b8e37fc8-0964d3d940d8e9-37664109-144000-165a7b8e380c8; _hc.v=91238b3e-1573-1925-4a79-fe67e2b82683.1536117237; _lxsdk_s=165a7b8e384-21f-ecd-f5a%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b8af6fc8-0865cf090fba31-37664109-144000-165a7b8af71c8; _lxsdk_cuid=165a7b8af6fc8-0865cf090fba31-37664109-144000-165a7b8af71c8; _hc.v=fd57301d-1c36-19bf-5872-60dbb3e55410.1536117223; _lxsdk_s=165a7b8af72-8f5-528-75c%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b90a7fc8-02067141708da2-37664109-144000-165a7b90a80c8; _lxsdk_cuid=165a7b90a7fc8-02067141708da2-37664109-144000-165a7b90a80c8; _hc.v=64c115f7-40ea-c802-9e33-80b37cf290ad.1536117247; _lxsdk_s=165a7b90a82-c30-af-e70%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b92395c8-00a5543edd1d-37664109-144000-165a7b92397c8; _lxsdk_cuid=165a7b92395c8-00a5543edd1d-37664109-144000-165a7b92397c8; _hc.v=3b2e6af6-4fa4-6b81-dae4-868fd58c8757.1536117253; _lxsdk_s=165a7b92399-537-00f-d52%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b98637c8-0e2c9539b0979a-37664109-144000-165a7b98639c8; _lxsdk_cuid=165a7b98637c8-0e2c9539b0979a-37664109-144000-165a7b98639c8; _hc.v=5c228d93-39c4-2661-79ca-519312834116.1536117279; _lxsdk_s=165a7b9863c-4ed-a77-40e%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b99570c8-0ec65f358c9019-37664109-144000-165a7b99572c8; _lxsdk_cuid=165a7b99570c8-0ec65f358c9019-37664109-144000-165a7b99572c8; _hc.v=bae63db1-b2ba-5723-875c-825951631658.1536117282; _lxsdk_s=165a7b99573-54-261-dec%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7b9b8cfc8-0096d8bcb0da9-37664109-144000-165a7b9b8d1c8; _lxsdk_cuid=165a7b9b8cfc8-0096d8bcb0da9-37664109-144000-165a7b9b8d1c8; _hc.v=aca7958c-a1b8-4531-e503-e68acfda9f62.1536117292; _lxsdk_s=165a7b9b8d5-59b-ef6-70d%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7ba279e22-006de9ea4eddeb-37664109-144000-165a7ba27a2c8; _lxsdk_cuid=165a7ba279e22-006de9ea4eddeb-37664109-144000-165a7ba27a2c8; _hc.v=d1fc6c8c-626c-0b62-45fc-f0022b150806.1536117320; _lxsdk_s=165a7ba27a5-e5c-676-152%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7ba4db1c8-072b06b19d7808-37664109-144000-165a7ba4db4c8; _lxsdk_cuid=165a7ba4db1c8-072b06b19d7808-37664109-144000-165a7ba4db4c8; _hc.v=33c74c50-495c-4744-85f3-6e9848fb7f1f.1536117330; _lxsdk_s=165a7ba4db7-beb-da9-e15%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7ba7aa2c8-0de20db2d0081a-37664109-144000-165a7ba7aa3c8; _lxsdk_cuid=165a7ba7aa2c8-0de20db2d0081a-37664109-144000-165a7ba7aa3c8; _hc.v=dfad410e-b247-3c50-4a61-7bc3dc9e37c5.1536117341; _lxsdk_s=165a7ba7aa8-a4f-a7d-707%7C%7C20; s_ViewType=10; ");
		COOKIES_SHOPLIST.add("_lxsdk=165a7bb0a8bc8-029ad9c0f17c8b-37664109-144000-165a7bb0a8c5f; _lxsdk_cuid=165a7bb0a8bc8-029ad9c0f17c8b-37664109-144000-165a7bb0a8c5f; _hc.v=f51f3a57-737b-3ef7-fc5a-029c73f7f817.1536117378; _lxsdk_s=165a7bb0a8e-a63-d33-bf%7C%7C20; s_ViewType=10; ");

	}

	public static String getSubCategorySubRegion(HttpRequestHeader header) {
		header.setRequestType(RequestType.HTTP_GET);
		header.setProxyType(ProxyType.PROXY_STATIC_AUTO);
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setAutoPcUa(true);
		header.setCookie("");
		header.setRequestSleepTime(5000);
		header.setMaxTryTimes(2);
		HttpResponse response = get(header);
		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
		} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
			return getSubCategorySubRegion(header);
		} else {
			return "";
		}
	}

	public static String getShopList(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
//		header.setProxyType(ProxyType.PROXY_CLOUD_ABUYUN);
//		header.setCookie("cy=1; cye=shanghai; _lxsdk_cuid=163af0776adc8-0d9507b7989639-3c3c520d-100200-163af0776adc8; _lxsdk=163af0776adc8-0d9507b7989639-3c3c520d-100200-163af0776adc8; _hc.v=dc2c85cc-a2f8-2f67-41c0-85d1e7959bcf.1527649892; _dp.ac.v=7068a142-bca3-47f9-ad44-d115c0d0ace3; s_ViewType=10; ua=%E9%AD%94%E4%BA%BA%40%E6%99%AE%E4%B9%8C; ctu=946223b20ade88cd1373a6270d8145bf62877d2bae3b09c54d78e4c29b716109; _lxsdk_s=164f43ec641-3e6-b3d-214%7C%7C17");
//		
		String cookie = COOKIES_SHOPLIST.poll();
		header.setCookie(cookie);
		COOKIES_SHOPLIST.add(cookie);
		
//		header.setUserAgent(
//				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
//		header.setUserAgent(UserAgentSupport.getPCUserAgent());
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
//		header.setAutoPcUa(true);
//		header.setAutoMobileUa(true);
		header.setRequestSleepTime(3000);
		header.setMaxTryTimes(1);
		if (header.getProject() == Project.CARGILL) {
			header.setMaxTryTimes(10);
		}
		HttpResponse response = get(header);
		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
		} else {
			return getShopList(header);
		}
	}

	public static String getShopDetail(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setPragma("no-cache");
//		header.setReferer("http://www.dianping.com/shanghai/ch10/g110");
		header.setUpgradeInsecureRequests("1");
//		header.setCookie(
//				"s_ViewType=10; _lxsdk_cuid=16380a356c9c8-0cc4004fa439b6-3c3c520d-100200-16380a356cac8; _lxsdk=16380a356c9c8-0cc4004fa439b6-3c3c520d-100200-16380a356cac8; _hc.v=c274d1e0-4c7d-f92a-dc57-a68f092822e7.1526871579; _lxsdk_s=16380a356d6-ad6-9ab-aa7%7C%7C5");
//		String random = test(new int[] {13,14,8,6,13});
//		header.setCookie(
//				"s_ViewType=10; _lxsdk_cuid=" + random.substring(0, random.length() - 1) + "; _lxsdk=" + random.substring(0, random.length() - 1) + "; cy=1; cye=shanghai; _hc.v=c274d1e0-4c7d-f92a-dc57-a68f092822e7.1526871579; _lxsdk_s=16380a356d6-ad6-9ab-aa7%7C%7C5");
//		header.setCookie(
//				"_hc.v=\"\\\"ecf7cc6e-e3ac-4e4b-a454-a8817f963380.1526881397\\\"\"; _lxsdk_cuid=163813ba56b2d-0b00460b78a70c-3b7c015b-100200-163813ba56cc8; _lxsdk=163813ba56b2d-0b00460b78a70c-3b7c015b-100200-163813ba56cc8; _lxsdk_s=163813ba56e-7b1-ecd-dae%7C%7C43");
		
		Map<String, Object> map = DianpingShopDetailCookie.COOKIES_DIANPING.poll();
		if (null != map && map.containsKey("cookie")) {
			header.setCookie(map.get("cookie").toString());
			header.setUserAgent(map.get("user_agent").toString());
			log.info("本批次使用的电话号码 " + map.get("phone").toString());
			
			DianpingShopDetailCookie.COOKIES_DIANPING.add(map);
			
		}
		
//		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.1.1 Safari/538.1");
		
//		header.setAutoPcUa(true);
//		header.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
		header.setRequestSleepTime(5000);
		header.setMaxTryTimes(1);
		HttpResponse response = get(header);
//		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
//		} else {
//			return getShopList(header);
//		}
	}

	public static String getShopComment(HttpRequestHeader header) {
		String html = "";
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9");
		header.setCacheControl("max-age=0");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
//		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setProxyType(ProxyType.NONE);
		header.setProject(Project.CARGILL);
		header.setSite(Site.DIANPING);
//		header.setProxy(new Proxy("223.202.131.27", 377));
//		Map<String, Object> map = DianpingShopDetailCookie.COOKIES_DIANPING.poll();
//		if (null != map && map.containsKey(DianpingShopDetailCookie.COOKIE_COMMENT)) {
//			header.setCookie(map.get(DianpingShopDetailCookie.COOKIE_COMMENT).toString());
//			log.info("本批次使用的电话号码 " + map.get("phone").toString());
//			DianpingShopDetailCookie.COOKIES_DIANPING.add(map);
//		}
		// e480
//		header.setCookie("dper=60e25c1799bd2229ee0e398e9e50f5d0a23363ae71374826f839a01cfcac0083e9bb1928b0d82b53e2d12c59cc3efa0e37f72b6d92f67c9e627820e3d33f5ca8; ll=7fd06e815b796be3df069dec7836c3df; ua=17151837694; ctu=f5539fc230d3b0f5512266208879744adbae5f158aedaf85b6211ecde66b4ed3; _lxsdk_cuid=165cce376c9c8-08970271ce0fe-37664109-144000-165cce376c9c8; _lxsdk=165cce376c9c8-08970271ce0fe-37664109-144000-165cce376c9c8; _hc.v=96d560ca-d5a1-28b9-0e1d-51c47e1d2745.1536740785; s_ViewType=10; cy=2; cye=beijing; _lxsdk_s=165cce376ca-e33-92e-b8a%7C%7C1539");
		// dell linux
//		header.setCookie("cy=1; cye=shanghai; dper=4555ddfb45611ff0adbb6d5c3567817e45da99273011693cc41050c2580f6103d48b2e2d3ecaebed0c279ccee8ea2d8c7dffc22368ddefb454d33d55df07971b; ll=7fd06e815b796be3df069dec7836c3df; ua=dpuser_1496623320; ctu=ebb2def69af6e872943468635a643aef0a6c71d79721e7e067b5aedb48caf209; uamo=17681888571; _lxsdk_cuid=165cc259318c8-0b10cbc6641d11-3b7c015b-100200-165cc259318c8; _lxsdk=165cc259318c8-0b10cbc6641d11-3b7c015b-100200-165cc259318c8; _hc.v=0a36417d-7abb-3945-b335-48b4dd9d9aa9.1536728340; s_ViewType=10; _lxsdk_s=165cc25931a-cb1-6af-5ad%7C%7C253");
		// dell windows
//		header.setCookie("cy=1; cye=shanghai; _lxsdk_cuid=165ccd832bec8-0a3b12d18eadaa-323b5b03-1fa400-165ccd832bec8; _lxsdk=165ccd832bec8-0a3b12d18eadaa-323b5b03-1fa400-165ccd832bec8; _hc.v=863739b2-0084-ee96-ac21-d99b77039277.1536740046; dper=60e25c1799bd2229ee0e398e9e50f5d069475602f2f12b7ba2727bec4f15ea3c0a7e73334aa10f9dbd430e8acacb7c57b85dcfbd36d0e40dd54b8a504cf7cb3170f3a7134b5bf175c7e85755af57d8a1f9693430c66cd7d7179d27a8bc3d6a46; ll=7fd06e815b796be3df069dec7836c3df; ua=17151837694; ctu=f5539fc230d3b0f5512266208879744a78ff81210da37e04eed1ddd65cb387ff; s_ViewType=10; _lxsdk_s=165ccd832bf-61-67-502%7C%7C626");
		// 72
//		header.setCookie("_lxsdk_cuid=163398adff3c8-0756071197ecf8-3c3c5905-1fa400-163398adff861; _lxsdk=163398adff3c8-0756071197ecf8-3c3c5905-1fa400-163398adff861; _hc.v=c1643cb4-2817-c0ce-df43-6c9a7f488e9a.1525678793; s_ViewType=10; ctu=27b4dd10165d51481592ec2ad457c1138c45ca81be2e5d60ac85896ce51145c1; cy=1; cye=shanghai; ua=17151837694; lgtoken=0820cda34-5e5b-4a36-8586-a858cf347501; dper=60e25c1799bd2229ee0e398e9e50f5d08090438dc7620d7bef343f6c0a0cc277b748b6b51cbb403308e97dac462cf5856acd3d1e2e92c46a3a744241fde6f307a126c65908a7bc1a6968e71568875352864372b48f3dd85e206d7eb89d6bd6c3; ll=7fd06e815b796be3df069dec7836c3df; _lxsdk_s=165ccc9ccc8-606-645-085%7C%7C237");
		// server 138
		// cy=1; cye=shanghai; _lxsdk_cuid=165d1ba1e54c8-0ae6118c6a10c2-323b5b03-1fa400-165d1ba1e54c8; _lxsdk=165d1ba1e54c8-0ae6118c6a10c2-323b5b03-1fa400-165d1ba1e54c8; _hc.v=63fcacdf-d479-ae98-1ee4-652da8ce7434.1536821961; lgtoken=00c54f0ed-a305-403b-84ce-74e771f1534c; dper=4555ddfb45611ff0adbb6d5c3567817ea3be3a6f6d4a7e6e7374facb73a6690f5379eae5ca59df088ded9b94600907b7a1b08d8b889e4d06694e2c8855c7cb564ed0ddccc49d2c0a02f522b6fcbabe67db9eaec6c89e8eaada382694c48822c4; ll=7fd06e815b796be3df069dec7836c3df; ua=dpuser_1496623320; ctu=ebb2def69af6e872943468635a643aefb131010776503d5554022f92dd08af2c; uamo=17681888571; _lxsdk_s=165d1ba1e57-220-47c-fc7%7C%7C98
//		header.setCookie("ll=7fd06e815b796be3df069dec7836c3df; uamo=17681888571; cy=1; cye=shanghai; _lxsdk_cuid=1645f0ec715c4-042547d92f300f-6a626029-ffc00-1645f0ec71668; _lxsdk_s=165d1b36b6d-b47-806-cae%7C%7C2004; _lxsdk=1645f0ec715c4-042547d92f300f-6a626029-ffc00-1645f0ec71668; _hc.v=a52e3cbe-882d-1133-b570-b59eab80e2be.1530603163; s_ViewType=10; dper=4555ddfb45611ff0adbb6d5c3567817ee0472153b139c49490b6bbd3b383ba8e92d38c2ca1642c568094184cf8aeb2b9105276dc05b842960e45d790a4f7a260100e35b303b21094cab68f919307b9d0657891ec81f431e04c2519c83d370255; ua=dpuser_1496623320; ctu=ebb2def69af6e872943468635a643aeff265546cc05160f1464c0481965a88d2");
		header.setCookie("cy=1; cye=shanghai; lgtoken=08ea380c8-1fc3-42e9-8b36-5cbce0b184d2; dper=0832ebe832341c2ad870a925f362a0e6eb8b304747943cb9d9d91a108f66d0d138aef90023baf65bc589621d59814c9eba0a319e25174225525d81711a79510f; ll=7fd06e815b796be3df069dec7836c3df; ua=%E9%AD%94%E4%BA%BA%40%E6%99%AE%E4%B9%8C; ctu=946223b20ade88cd1373a6270d8145bfcc742594a58febe5ab272854d4f41fbf; uamo=13651952625; _lxsdk_cuid=165d6d9f1c9c8-055e7c25ec9f61-323b5b03-1fa400-165d6d9f1cac8; _lxsdk=165d6d9f1c9c8-055e7c25ec9f61-323b5b03-1fa400-165d6d9f1cac8; _hc.v=2f2f88bb-9d62-22f5-65b5-7e4cf9317f6f.1536907933; s_ViewType=10; _lxsdk_s=165d6d9f1cb-7b-03d-480%7C%7C325");
		
		header.setAutoPcUa(true);
//		header.setAutoMobileUa(true);
//		header.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/535.19");
//		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
//		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
		header.setRequestSleepTime(3000);
		header.setMaxTryTimes(1);
		HttpResponse response = get(header);
		if (response.getCode() == HttpStatus.SC_OK) {
			html = response.getContent();
//			Document doc = Jsoup.parse(html);
			if (html.contains("抱歉！页面无法访问") || html.contains("很抱歉，您要访问的页面不存在")
					) {
				log.info(header.getUrl() + " 应该有评论，但是返回页面无法访问或页面不存在，重新请求");
				try {
					TimeUnit.SECONDS.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//				getShopComment(header);
			}
		} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
			log.info("页面被禁止访问，请清除缓存后重新登录，拷贝Cookie");
			try {
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else {
			html = "";
		}
		return html;
	}
	
	public static String getShopCommentNew(HttpRequestHeader header) {
		String html = "";	
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("379862802", "infopower"));
		CloseableHttpClient httpClient = HttpClientBuilder.create()
//				.setDefaultCredentialsProvider(credsProvider)
				.build();
//		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(header.getUrl());
		
//		Proxy _proxy = StaticProxySupport.getStaticProxy(ProxyType.PROXY_STATIC_DLY, Project.CARGILL, Site.DIANPING);
//		HttpHost proxy = new HttpHost(_proxy.getIp(), _proxy.getPort());
		
//		RequestConfig.Builder builder = RequestConfig.custom()
//				.setSocketTimeout(10000)
//				.setConnectTimeout(10000)
//				.setConnectionRequestTimeout(10000)
//				.setRedirectsEnabled(true)
////				.setProxy(proxy)
//				.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
//				;
		
//		get.setConfig(builder.build());
		
		get.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		get.addHeader("Accept-Encoding", "gzip, deflate");
		get.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
		get.addHeader("Cache-Control", "no-cache");
		get.addHeader("Connection", "keep-alive");
		get.addHeader("Host", "www.dianping.com");
		get.addHeader("Pragma", "no-cache");
		get.addHeader("Upgrade-Insecure-Requests", "1");
		get.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

		get.addHeader("Cookie",
				"cy=1; cye=shanghai; _lxsdk_cuid=165c14b237ec8-0511b507660f2e-37664109-144000-165c14b237ec8; _lxsdk=165c14b237ec8-0511b507660f2e-37664109-144000-165c14b237ec8; _hc.v=aac7b9a5-1195-a0a5-9b72-1ca3312b6707.1536546252; dper=60e25c1799bd2229ee0e398e9e50f5d042aff6cb7e795395ec586a903d908cdd649386c9b2872a8b8a00e938f651fd168bb62b23f35d7ea1892a169cbd8c3ec72404380a8c19a25079c60fcc14fa44db745337cb46862758e367e30532dc229b; ll=7fd06e815b796be3df069dec7836c3df; ua=17151837694; ctu=f5539fc230d3b0f5512266208879744a098963746382a2c623af671f74d25a72; s_ViewType=10; _lxsdk_s=165c14b2380-5a1-3b2-178%7C%7C516");
		// 设置登陆时要求的信息，用户名和密码
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
//		RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();
//		get.setConfig(globalConfig);

		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(get);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//            }
		// 如果响应不为null
		if (null != response) {

			// 如果请求成功
			if (null != response.getStatusLine() && (response.getStatusLine().getStatusCode() == 200)) {
				try {
					html = EntityUtils.toString(response.getEntity(), "UTF-8");
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return html;
	}
	
	public static String getShopRecommend(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setPragma("no-cache");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
//		header.setProxyType(ProxyType.NONE);
		Map<String, Object> map = DianpingShopDetailCookie.COOKIES_DIANPING.poll();
		if (null != map && map.containsKey(DianpingShopDetailCookie.COOKIE_RECOMMEND)) {
			header.setCookie(map.get(DianpingShopDetailCookie.COOKIE_RECOMMEND).toString());
			header.setUserAgent(map.get("user_agent").toString());
//			header.setAutoPcUa(true);
			log.info("本批次使用的电话号码 " + map.get("phone").toString());
			
			DianpingShopDetailCookie.COOKIES_DIANPING.add(map);
			
		}
//		header.setAutoPcUa(true);
//		header.setCookie("cy=1; cye=shanghai; _lxsdk_cuid=163af0776adc8-0d9507b7989639-3c3c520d-100200-163af0776adc8; _lxsdk=163af0776adc8-0d9507b7989639-3c3c520d-100200-163af0776adc8; _hc.v=dc2c85cc-a2f8-2f67-41c0-85d1e7959bcf.1527649892; _dp.ac.v=7068a142-bca3-47f9-ad44-d115c0d0ace3; s_ViewType=10; ua=%E9%AD%94%E4%BA%BA%40%E6%99%AE%E4%B9%8C; ctu=946223b20ade88cd1373a6270d8145bf62877d2bae3b09c54d78e4c29b716109; ctu=57f4fba19c4400d8ada2e815a0bacf8f56ccad2e0f9bc373588beeb8f5714ecc7694423828faf1dc4fe77b9617252c16; _lxsdk_s=164cb446b5c-9ac-fe2-10c%7C%7C112");
		header.setRequestSleepTime(10000);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
//				removeInvalideCookie(COOKIES_SHOPRECOMMEND, header.getCookie());
//				header.setCookie(COOKIES_SHOPRECOMMEND.element());
				html = getShopRecommend(header);
			} else {
				html = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	//header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
	/*
	public static String getUserInfo(HttpRequestHeader header) {
		header.setAcceptEncoding("gzip, deflate, br");
		header.setAcceptLanguage("zh-CN,zh;q=0.9");
		header.setCacheControl("max-age=0");
		header.setConnection("keep-alive");
		header.setHost("m.dianping.com");
		header.setUpgradeInsecureRequests("1");
		WebDriver driver = null;
		String html = "";
		try {
			WebDriverConfig config = new WebDriverConfig();
			config.setTimeOut(10);
			config.setProxyType(ProxyType.PROXY_STATIC_DLY);
			driver = WebDriverSupport.getPhantomJSDriverInstance(config);
//			driver = WebDriverSupport.getChromeDriverInstance(config);
			html = WebDriverSupport.load(driver, header.getUrl());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != driver) {
				driver.close();
				driver.quit();
			}
		}
		
		return html;
	}
	*/
	
	public static String getUserInfo(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate, br");
		header.setAcceptLanguage("zh-CN,zh;q=0.9");
		header.setCacheControl("max-age=0");
		header.setConnection("keep-alive");
		header.setHost("m.dianping.com");
		header.setUpgradeInsecureRequests("1");
		
//		Map<String, Object> map = COOKIES_USER_DETAIL.poll();
//		if (null != map && map.containsKey("cookie")) {
//			header.setCookie(map.get("cookie").toString());
//			header.setUserAgent(map.get("user_agent").toString());
//			log.info("本批次使用的电话号码 " + map.get("phone").toString());
//			
//			COOKIES_USER_DETAIL.add(map);
//			
//		}
		
//		Map<String, Object> map = DianpingUserInfoCookie.COOKIES_USER_INFO.poll();
//		if (null != map && map.containsKey("cookie")) {
//			header.setCookie(map.get("cookie").toString());
//			header.setUserAgent(map.get("user_agent").toString());
//			log.info("本批次使用的电话号码 " + map.get("phone").toString());
//			
//			DianpingShopDetailCookie.COOKIES_SHOP_DETAIL.add(map);
//			
//		}
		
//		header.setCookie("m_flash2=1; cityid=1; default_ab=index%3AA%3A1; cy=1; cye=shanghai; _lxsdk_cuid=1638ae2d38bc8-088125da2d0cbf-3c3c520d-100200-1638ae2d38bc8; _lxsdk=1638ae2d38bc8-088125da2d0cbf-3c3c520d-100200-1638ae2d38bc8; _hc.v=4582347e-2065-f8c6-141d-0b3297f319a1.1527043511; s_ViewType=10");
//		header.setAutoMobileUa(true);
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.1.1 Safari/538.1");
		header.setRequestSleepTime(500);
		header.setMaxTryTimes(1);
		HttpResponse response = get(header);
		return response.getContent();
	}
	
	public static String getUserCheckInfo(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setPragma("no-cache");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setProject(Project.CARGILL);
		header.setSite(Site.DIANPING);
//		header.setProxyType(ProxyType.NONE);
//		header.setAutoPcUa(true);
//		header.setCookie(COOKIES_USERINFO.element());
//		header.setCookie("_hc.v=\"\"1c28735c-9efb-4f85-8805-eebb74bd311d.1521009797\"\"; _lxsdk_cuid=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; _lxsdk=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; cy=1; cye=shanghai; s_ViewType=10; m_flash2=1; pvhistory=6L+U5ZuePjo8L2Vycm9yL2Vycm9yX3BhZ2U+OjwxNTIxNTA3OTI5MTk2XV9b; _lxsdk_s=16240ebf651-c5a-e75-b4d%7C%7C319");
//		header.setCookie("_hc.v=\"\"1c28735c-9efb-4f85-8805-eebb74bd311d." + ((System.currentTimeMillis() / 1000) - 6666) + "\"\"; _lxsdk_cuid=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; _lxsdk=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; cy=1; cye=shanghai; s_ViewType=10; m_flash2=1; pvhistory=6L+U5ZuePjo8L2Vycm9yL2Vycm9yX3BhZ2U+OjwxNTIxNTA3OTI5MTk2XV9b; _lxsdk_s=16240ebf651-c5a-e75-b4d%7C%7C319");
		
		Map<String, Object> map = DianpingShopDetailCookie.COOKIES_DIANPING.poll();
		if (null != map && map.containsKey("cookie")) {
			header.setCookie(map.get("cookie").toString());
			header.setUserAgent(map.get("user_agent").toString());
			log.info("本批次使用的电话号码 " + map.get("phone").toString());
			
			DianpingShopDetailCookie.COOKIES_DIANPING.add(map);
			
		}
		
//		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.1.1 Safari/538.1");
		header.setRequestSleepTime(10000);
		header.setMaxTryTimes(1);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
				html = getUserCheckInfo(header);
			} else {
				html = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	/**
	 * 实时榜
	 * @param header
	 * @return
	 */
	public static String getRealTimeRank(HttpRequestHeader header) {
//		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setProxyType(ProxyType.NONE);
		header.setProject(Project.CARGILL);
		header.setSite(Site.DIANPING);
		header.setAutoMobileUa(true);
		header.setRequestSleepTime(5000);
		header.setMaxTryTimes(5);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else {
				html = getRealTimeRank(header);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	/**
	 * 菜品榜
	 * @param header
	 * @return
	 */
	public static String getDishRank(HttpRequestHeader header) {
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setAutoPcUa(true);
		header.setRequestSleepTime(5000);
		header.setMaxTryTimes(5);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else {
				html = getDishRank(header);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	public static void main(String[] args) {
//		HttpRequestHeader header = new HttpRequestHeader();
//		header.setUrl("http://www.dianping.com/shanghai/ch10/g110r2");
//		header.setProject(Project.CARGILL);
//		header.setSite(Site.DIANPING);
//		getShopList(header);
		
//		test(new int[] {13,14,8,6,13});
		
//		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		try {
			Document doc = Jsoup.connect("http://2018.ip138.com/ic.asp").get();
			log_test.info(doc.select("center").text());
			
			String info = doc.select("center").text();
			
			IpTest ip = new IpTest();
			ip.setIp(info.substring(info.indexOf("[") + 1, info.lastIndexOf("]")));
			ip.setLocation(info.substring(info.indexOf("来自：") + 3));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String test(int[] list) {
		StringBuilder sb = new StringBuilder();
		int maxNum = 36;
		int i;
		int count = 0;
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'w', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		
		for (int num : list) {
			while (count < num) {
				i = Math.abs(r.nextInt(maxNum));
				if (i >= 0 && i < str.length) {
					pwd.append(str[i]);
					count++;
				}
			}
			sb.append(pwd).append("-");
			pwd = new StringBuffer();
			count = 0;
		}
		sb.append(pwd);
		System.out.println(sb.toString());
		return sb.toString();
	}
	
}