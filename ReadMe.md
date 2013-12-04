# CorsHandler Sample

This is a sample implementation of cors handler for [Netty](https://github.com/netty/netty).

Source code is based on [Xitrum](https://github.com/ngocdaothanh/xitrum)'s downstream handler.
<https://github.com/ngocdaothanh/xitrum/blob/master/src/main/scala/xitrum/handler/down/SetCORS.scala>

#Usage:

* Create policy

		TreeSet<String> allowOrigin = new TreeSet<String>();
		allowOrigin.add("*");
		Boolean allowCredentials = true;
		TreeSet<String> exposeHeaders = new TreeSet<String>();
		exposeHeaders.add("x-HeaderA");
		int maxAge = 100;
		TreeSet<String> allowMethods = new TreeSet<String>();
		allowMethods.add("GET");
		allowMethods.add("POST");
		TreeSet<String> allowHeaders = new TreeSet<String>();
		allowHeaders.add("x-HeaderB");

		CorsPolicy corsPolicy = new DefaultCorsPolicy(
			allowOrigin,
			allowCredentials,
			exposeHeaders,
			maxAge,
			allowMethods,
			allowHeaders
		);

<br>

* Mapping policy with uri(regex)

		Map<String, CorsPolicy> policyMap = new HashMap<String, CorsPolicy>();
		policyMap.put("/", corsPolicy);
		policyMap.put("/api[1-9]", corsPolicy2);

<br>

* Add pipline with handler

		pipeline.addLast("cors",     new CorsHandler(policyMap));

