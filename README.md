osgi-bundle-frontapi
====================

Develop your APIs the OSGi way.

Project home:
[https://github.com/DDTH/osgi-bundle-frontapi](https://github.com/DDTH/osgi-bundle-frontapi)


## License ##

See LICENSE.txt for details. Copyright (c) 2014 Thanh Ba Nguyen.

Third party libraries are distributed under their own licenses.


## Installation #

Latest release version: `0.1.3.1`. See [RELEASE-NOTES.md](RELEASE-NOTES.md).

Maven dependency:

```xml
<dependency>
	<groupId>com.github.ddth</groupId>
	<artifactId>osgi-bundle-frontapi</artifactId>
	<version>0.1.3.1</version>
	<scope>provided</scope>
</dependency>
```


## Develop and Call APIs ##

APIs are grouped into modules. Each API has a name and belongs to a module. Module can be viewed as API's namespace.

Each module has a unique name. While API names are unique within the module.

Notes:

- Using bundle's name as module name would be a good way.
- Two APIs in two different modules can have a same name.

Steps to develop APIs:

1.Develop APIs by implementing interface `com.github.ddth.frontapi`.

2.Lookup the `com.github.ddth.frontapi.IApiRegistry` instance and register APIs when the bundle starts.
```java
@Override
public void start(BundleContext context) throws Exception {
    ...
    IApi api = ...; //obtain the API instance
    ...
    ServiceReference<IApiRegistry> serviceRef = bundleContext.getServiceReference(IApiRegistry.class);
    IApiRegistry apiRegistry = bundleContext.getService(serviceRef);
    apiRegistry.register("module-name", "api-name", api);
    bundleContext.ungetService(serviceRef);
    ....
}
```

From now on, APIs can be called via REST, [Thrift](http://thrift.apache.org) or using `IApiClient`.

a) Call APIs via REST, assuming osgi-bundle-frontapi is deployed on [OSGi Server](https://github.com/DDTH/osgiserver):
> `GET http://host:port/api/<auth-key>/<module-name>/<api-name>?param1=value1&param2=value2&...`

or
> `POST http://host:port/api/<auth-key>/<module-name>/<api-name>`
> 
> API's input parameters are encapsulated in the POST request's body, *encoded as a JSON string*.

b) Call APIs via Thrift (Thrift server's default port is `9090`):
> Generate Thrift client stub from [FrontApi.thrift](FrontApi.thrift).

c) Call APIs using `IApiClient`:
> ```java
> import com.github.ddth.frontapi.ApiResult;
> import com.github.ddth.frontapi.client.*;
> ...
> 
> //call APIs via REST client
> IApiClient apiClient = new RestApiClient("http://host:port/api");
> Object apiInputs = ...;
> ApiResult result = apiClient.call("auth-key", "module-name", "api-name", apiInputs);
> ...
>
> //call APIs via Thrift client
> IApiClient apiClient = new ThriftApiClient("host", port);
> Object apiInputs = ...;
> ApiResult result = apiClient.call("auth-key", "module-name", "api-name", apiInputs);
> ...
> ```

3.Unregister APIs when the bundle stops.
```java
@Override
public void start(BundleContext context) throws Exception {
    ...
    ServiceReference<IApiRegistry> serviceRef = bundleContext.getServiceReference(IApiRegistry.class);
    IApiRegistry apiRegistry = bundleContext.getService(serviceRef);
    apiRegistry.unregister("module-name");
    bundleContext.ungetService(serviceRef);
    ...
}
```

