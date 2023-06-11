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

# EJB-Client  Test
* Use kubectl port-forward: 
```
kubectl  port-forward svc/ejblbclient-service  8081:8080
```
## Test Approaches
* Use Anthos Sandbox 's Web Preview
  * path: ``/test/stateless``
    ```
    https://8081-cs-983647568396-default.cs-asia-east1-jnrc.cloudshell.dev/test/stateless?authuser=0&redirectedPreviously=true
    ```
* Use siege
  ```bash 
  sudo apt install -y siege
  siege -c 100 -r 200 -d1 'http://localhost:8081/test/stateless'
  ```
## NodeSelector Approaches - Using Selectors
```
podNames=$(kubectl get pods -l application=ejblb  --no-headers -o custom-columns=":metadata.name")

# kubectl label pod <your-pod-name> version=v1
# use loop,counter
counter=0;

for i in $podNames ;\
do \
  echo $i; \
  echo "---------------------------------"  ;\
  let counter=counter+1 ;
  kubectl label pod $i test=v$counter ;
done

kubectl get pods -l application=ejblb --show-labels 

NAME                               READY   STATUS    RESTARTS   AGE   LABELS
ejblb-deploy-577b4477c6-rvjgv      2/2     Running   0          16h   application=ejblb,istio.io/rev=basic,pod-template-hash=577b4477c6,security.istio.io/tlsMode=istio,service.istio.io/canonical-name=ejblb-deploy,service.istio.io/canonical-revision=latest,test=v25
ejblb-deploy-v1-855667c655-twfwt   2/2     Running   0          16h   app=ejblb-v1,application=ejblb,istio.io/rev=basic,pod-template-hash=855667c655,security.istio.io/tlsMode=istio,service.istio.io/canonical-name=ejblb-v1,service.istio.io/canonical-revision=v1,test=v26,version=v1
ejblb-deploy-v2-6ff8fc74df-9xvjc   2/2     Running   0          16h   app=ejblb-v2,application=ejblb,istio.io/rev=basic,pod-template-hash=6ff8fc74df,security.istio.io/tlsMode=istio,service.istio.io/canonical-name=ejblb-v2,service.istio.io/canonical-revision=v2,test=v27,version=v2
ejblb-deploy-v3-75fd47b6ff-7tbhv   2/2     Running   0          16h   app=ejblb-v3,application=ejblb,istio.io/rev=basic,pod-template-hash=75fd47b6ff,security.istio.io/tlsMode=istio,service.istio.io/canonical-name=ejblb-v3,service.istio.io/canonical-revision=v3,test=v28,version=v3
ejblb-deploy-v4-6c9db49cc7-97zkx   2/2     Running   0          16h   app=ejblb-v4,application=ejblb,istio.io/rev=basic,pod-template-hash=6c9db49cc7,security.istio.io/tlsMode=istio,service.istio.io/canonical-name=ejblb-v4,service.istio.io/canonical-revision=v4,test=v29,version=v4
ejblb-deploy-v5-5dfccc9687-xxpjm   2/2     Running   0          16h   app=ejblb-v5,application=ejblb,istio.io/rev=basic,pod-template-hash=5dfccc9687,security.istio.io/tlsMode=istio,service.istio.io/canonical-name=ejblb-v5,service.istio.io/canonical-revision=v5,test=v30,version=v5

```
## Using Headless Service Approaches  
* With a Headless Service, clients can connect to it’s pods by connecting to the service’s DNS name. But using headless services, DNS returns the pod’s IPs and client can connect directly to the pods instead via the service proxy.
  * https://blog.knoldus.com/what-is-headless-service-setup-a-service-in-kubernetes/
  * https://stackoverflow.com/questions/52707840/what-is-a-headless-service-what-does-it-do-accomplish-and-what-are-some-legiti

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
The DNS server returns three different IPs for the **ejblb-headless-svc.default.svc.cluster.local** FQDN.

**Note 1:** with a headless service, clients can connect to its pods by connecting to the service’s DNS name, as they can with regular services. But with headless services, because DNS returns the pods’ IPs, clients connect directly to the pods, instead of through the service proxy.

**Note 2:** Headless services still provide load balancing across pods but through the DNS round-robin mechanism instead of through the service proxy.