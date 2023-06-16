# StartUp
```
./setting_env.sh
```

# Dashboard
```
minikube -p sandbox dashboard
```
# Grafana
```
kubectl -n monitoring port-forward svc/grafana 3001:3000
```
# Elastic APM
* Use kubectl port-forward: 
```bash
kubectl -n apm port-forward svc/kibana   5601
```
* Use Anthos Sandbox 's Web Preview
  * path: ``/app/home``
    ```
    https://5601-cs-983647568396-default.cs-asia-east1-jnrc.cloudshell.dev/app/home?authuser=0&redirectedPreviously=true
    ```
# EJB-Client  Test
* Use kubectl port-forward: 
```bash
kubectl  port-forward svc/svc-ejb-txn-remote-call-client  8080
kubectl  port-forward svc/ejb-txn-remote-call-client-loadbalancer   8080
```
## Test Approaches
* Use Anthos Sandbox 's Web Preview
  * path: ``/client/direct-stateless``
    ```
    https://8080-cs-983647568396-default.cs-asia-east1-jnrc.cloudshell.dev/client/direct-stateless?authuser=0&redirectedPreviously=true
    ```
* Use ``siege``
  ```bash 
  sudo apt install -y siege
  siege -c 100 -r 200 -d1 'http://localhost:8080/client/remote-outbound-stateless'
  siege -c 100 -r 200 -d1 'http://localhost:8080/client/remote-outbound-notx-stateless'
  siege -c 100 -r 200 -d1 'http://localhost:8080/client/direct-stateless'
  siege -c 100 -r 200 -d1 'http://localhost:8080/client/direct-stateless-http'
  siege -c 100 -r 200 -d1 'http://localhost:8080/client/remote-outbound-fail-stateless'
  siege -c 100 -r 200 -d1 'http://localhost:8080/client/direct-stateless-node-selector'
  ```
 * Use ``curl``
  ```bash  
  curl 'http://localhost:8080/client/remote-outbound-stateless' |jq .
  curl 'http://localhost:8080/client/remote-outbound-notx-stateless'  |jq .
  curl 'http://localhost:8080/client/direct-stateless'  |jq .
  curl 'http://localhost:8080/client/direct-stateless-http' |jq .
  curl 'http://localhost:8080/client/remote-outbound-fail-stateless'
  curl 'http://localhost:8080/client/direct-stateless-node-selector'  |jq .
  ```
## Using Headless Service Approaches  
* With a Headless Service, clients can connect to it’s pods by connecting to the service’s DNS name. But using headless services, DNS returns the pod’s IPs and client can connect directly to the pods instead via the service proxy.
  * https://blog.knoldus.com/what-is-headless-service-setup-a-service-in-kubernetes/
  * https://stackoverflow.com/questions/52707840/what-is-a-headless-service-what-does-it-do-accomplish-and-what-are-some-legiti
* Scenarios
  * Scenario 1: **Deployment + Headless Service**
    ```
    kubectl run temporary --image=radial/busyboxplus:curl -i --tty

    If you don't see a command prompt, try pressing enter.
    [ root@temporary:/ ]$ nslookup ejblb-headless-svc
    Server:    10.96.0.10
    Address 1: 10.96.0.10 kube-dns.kube-system.svc.cluster.local

    Name:      ejblb-headless-svc
    Address 1: 10.244.0.19 10-244-0-19.ejblb-service.default.svc.cluster.local
    Address 2: 10.244.0.18 10-244-0-18.ejblb-service.default.svc.cluster.local
    Address 3: 10.244.0.20 10-244-0-20.ejblb-service.default.svc.cluster.local
    Address 4: 10.244.0.21 10-244-0-21.ejblb-service.default.svc.cluster.local
    Address 5: 10.244.0.8 10-244-0-8.ejblb-service.default.svc.cluster.local
    [ root@temporary:/ ]$ 
    [ root@temporary:ping  ejblb-headless-svc
    PING ejblb-headless-svc (10.244.0.19): 56 data bytes
    64 bytes from 10.244.0.19: seq=0 ttl=64 time=0.255 ms
    64 bytes from 10.244.0.19: seq=1 ttl=64 time=0.079 ms
    64 bytes from 10.244.0.19: seq=2 ttl=64 time=0.092 ms
    ^C
    --- ejblb-headless-svc ping statistics ---
    3 packets transmitted, 3 packets received, 0% packet loss
    round-trip min/avg/max = 0.079/0.142/0.255 ms
    [ root@temporary:/ ]$ ping  ejblb-headless-svc
    PING ejblb-headless-svc (10.244.0.8): 56 data bytes
    64 bytes from 10.244.0.8: seq=0 ttl=64 time=0.221 ms
    64 bytes from 10.244.0.8: seq=1 ttl=64 time=0.100 ms
    64 bytes from 10.244.0.8: seq=2 ttl=64 time=0.070 ms
    ^C
    --- ejblb-headless-svc ping statistics ---
    3 packets transmitted, 3 packets received, 0% packet loss
    round-trip min/avg/max = 0.070/0.130/0.221 ms
    [ root@temporary:/ ]$ ping  ejblb-headless-svc
    PING ejblb-headless-svc (10.244.0.18): 56 data bytes
    64 bytes from 10.244.0.18: seq=0 ttl=64 time=0.259 ms
    64 bytes from 10.244.0.18: seq=1 ttl=64 time=0.097 ms
    64 bytes from 10.244.0.18: seq=2 ttl=64 time=0.083 ms
    ^C
    --- ejblb-headless-svc ping statistics ---
    3 packets transmitted, 3 packets received, 0% packet loss
    round-trip min/avg/max = 0.083/0.146/0.259 ms
    [ root@temporary:/ ]$ ping  ejblb-headless-svc
    PING ejblb-headless-svc (10.244.0.20): 56 data bytes
    64 bytes from 10.244.0.20: seq=0 ttl=64 time=0.257 ms
    64 bytes from 10.244.0.20: seq=1 ttl=64 time=0.103 ms
    64 bytes from 10.244.0.20: seq=2 ttl=64 time=0.114 ms
    64 bytes from 10.244.0.20: seq=3 ttl=64 time=0.095 ms
    ^C
    --- ejblb-headless-svc ping statistics ---
    4 packets transmitted, 4 packets received, 0% packet loss
    round-trip min/avg/max = 0.095/0.142/0.257 ms
    [ root@temporary:/ ]$ ping  ejblb-headless-svc
    PING ejblb-headless-svc (10.244.0.8): 56 data bytes
    64 bytes from 10.244.0.8: seq=0 ttl=64 time=0.069 ms
    64 bytes from 10.244.0.8: seq=1 ttl=64 time=0.082 ms
    64 bytes from 10.244.0.8: seq=2 ttl=64 time=0.085 ms
    64 bytes from 10.244.0.8: seq=3 ttl=64 time=0.083 ms
    64 bytes from 10.244.0.8: seq=4 ttl=64 time=0.083 ms
    64 bytes from 10.244.0.8: seq=5 ttl=64 time=0.113 ms
    64 bytes from 10.244.0.8: seq=6 ttl=64 time=0.098 ms
    ^C
    --- ejblb-headless-svc ping statistics ---
    7 packets transmitted, 7 packets received, 0% packet loss
    round-trip min/avg/max = 0.069/0.087/0.113 ms
    [ root@temporary:/ ]$ ping  ejblb-svc
    ping: bad address 'ejblb-svc'
    [ root@temporary:/ ]$ ping  ejblb-service
    PING ejblb-service (10.100.32.74): 56 data bytes
    ^C
    --- ejblb-service ping statistics ---
    4 packets transmitted, 0 packets received, 100% packet loss
    [ root@temporary:/ ]$ ping  ejblb-service
    PING ejblb-service (10.100.32.74): 56 data bytes
    ```
    * The DNS server returns three different IPs for the **ejblb-headless-svc.default.svc.cluster.local** FQDN.
      **Note 1:** with a headless service, clients can connect to its pods by connecting to the service’s DNS name, as they can with regular services. But with headless services, because DNS returns the pods’ IPs, clients connect directly to the pods, instead of through the service proxy.
      **Note 2:** Headless services still provide load balancing across pods but through the DNS round-robin mechanism instead of through the service proxy.
  * Scenario 2: **StatefulSet + Headless Service**
    ```
    kubectl run temporary --image=radial/busyboxplus:curl -i --tty
    [ root@temporary:/ ]$ nslookup ejblb-headless-svc
    Server:    10.96.0.10
    Address 1: 10.96.0.10 kube-dns.kube-system.svc.cluster.local

    Name:      ejblb-headless-svc
    Address 1: 10.244.0.21 ejblb-3.ejblb-headless-svc.default.svc.cluster.local
    Address 2: 10.244.0.19 ejblb-0.ejblb-headless-svc.default.svc.cluster.local
    Address 3: 10.244.0.18 ejblb-1.ejblb-headless-svc.default.svc.cluster.local
    Address 4: 10.244.0.8 ejblb-4.ejblb-headless-svc.default.svc.cluster.local
    Address 5: 10.244.0.20 ejblb-2.ejblb-headless-svc.default.svc.cluster.local
    ```
  * Comparison:
    | Headless Service   | StatefulSet | Deployment  | ip          |
    |--------------------|-------------|-------------|-------------|
    | ejblb-headless-svc | ejblb-0     | 10-244-0-19 | 10.244.0.19 |
    | ejblb-headless-svc | ejblb-1     | 10-244-0-18 | 10.244.0.18 |
    | ejblb-headless-svc | ejblb-2     | 10-244-0-20 | 10.244.0.20 |
    | ejblb-headless-svc | ejblb-3     | 10-244-0-21 | 10.244.0.21 |
    | ejblb-headless-svc | ejblb-4     | 10-244-0-8  | 10.244.0.8  |  

# WildFly and Kubernetes - how they play together - DevConf.CZ 2020
* https://github.com/wildfly/quickstart/tree/26.1.3.Final/ejb-txn-remote-call
* https://youtu.be/IUJt_wXwjms?t=1037

# Jkube
* Intrudtion: 
  * https://www.mastertheboss.com/eclipse/eclipse-microservices/how-to-deploy-wildfly-bootable-jar-on-openshift/
  * https://www.eclipse.org/jkube/
    * https://www.eclipse.org/jkube/docs/kubernetes-maven-plugin/#jkube:build
* example code : https://github.com/eclipse/jkube/blob/master/quickstarts/maven/docker-file-simple/README.md


```bash
mvn clean package -Pbootable  k8s:build
mvn clean package org.eclipse.jkube:kubernetes-maven-plugin:build
```