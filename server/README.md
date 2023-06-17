```bash
[jboss@ejb-txn-remote-call-server-0 deployments]$ curl localhost:8080/server/commits 
["host: ejb-txn-remote-call-server-0.ejb-txn-remote-call-server-headless.default.svc.cluster.local/10.244.0.12, jboss node name: xn-remote-call-server-0","102"][jboss@ejb-txn-remote-call-server-0 deployments]$
```

The [transaction recovery manager](http://jbossts.blogspot.com/2018/01/narayana-periodic-recovery-of-xa.html) periodically retries to recover the unfinished work. When it makes the work successfully committed, the transaction is complete, and the database update will be visible. You can confirm the database update was processed by issuing REST endpoint reporting number of finished commits.

You can invoke the endpoint ``server/commits`` at both servers server2 and server3 where server application is deployed (i.e. ``http://localhost:8180/server/commits`` and ``http://localhost:8280/server/commits``). The output of this command is a tuple. It shows the node info, and the number of commits recorded. For example the output could be ["host: mydev.narayana.io/192.168.0.1, jboss node name: xn-remote-call-server-0","102"] and it says that the hostname is ``ejb-txn-remote-call-server-0.ejb-txn-remote-call-server-headless.default.svc.cluster.local``, the jboss node name is ``xn-remote-call-server-0``, and the number of commits is ``102``.