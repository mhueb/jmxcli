jmxcli
======

Simple jmx command line utility


usage
-----
```
jmxcli {commands} {options}

commands:
	lo                                    List available objects
	la <object name>                      List available attributes
	get <object name> <attribute name>    Get attribute value
	
options:
	-s <server>                           JMX server
	-user <username>                      Username for authentication
	-password <password>                  Password for authentication
```

example
-------
```
	jmxcli.sh -s service:jmx:remoting-jmx://somehost:4447 lo
```	
	
deployment
----------

```
jmxcli/
	jmxcli.bat
	jmxcli.sh
	lib/
		jmxcli-x.y.z.jar
		<optional jars>
```

For use with jboss-as 7 add jboss-client.jar int lib folder.
