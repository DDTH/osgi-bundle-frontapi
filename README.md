osgi-bundle-frontapi
====================

Develop your APIs the OSGi way.

Project home:
[https://github.com/DDTH/osgi-bundle-frontapi](https://github.com/DDTH/osgi-bundle-frontapi)


## License ##

See LICENSE.txt for details. Copyright (c) 2014 Thanh Ba Nguyen.

Third party libraries are distributed under their own licenses.


## Installation #

Latest release version: `0.1.3`. See [RELEASE-NOTES.md](RELEASE-NOTES.md).

Maven dependency:

```xml
<dependency>
	<groupId>com.github.ddth</groupId>
	<artifactId>osgi-bundle-frontapi</artifactId>
	<version>0.1.3</version>
	<scope>provided</scope>
</dependency>
```


## Develop and Call APIs ##

APIs are grouped into modules. Each API has a name and belongs to a module. Module can be viewed as API's namespace.

Each module has a unique name. While API names are unique within the module.

Notes:

- Using bundle's name as module name would be a good way.
- Two APIs in two different modules can have a same name.

Steps to develop an API:

1. Develop API by implementing interface `com.github.ddth.frontapi`.
2. Lookup the `com.github.ddth.frontapi.IApiRegistry` instance and register the API.
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
3. Unregister APIs when finish.
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
