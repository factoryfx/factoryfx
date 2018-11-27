# Permissions (attribute based)

Factory supports setting permission on attribute to restrict the write access for user.

The permission check is executed on the server and therefore safe but the setup is also complicated because you need a user management.
Alternatively you can deactivate editing in the gui.(not checked on the server)
```java
...
    public final StringAttribute attribute = new StringAttribute().userReadOnly();
...
```

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/de/factoryfx/docu/permission)