* https://github.com/wildfly/wildfly-operator/tree/0.5.6#quickstart

```bash
git clone https://github.com/wildfly/wildfly-operator
cd  wildfly-operator
git checkout 0.5.6

# install WildFlyServer CRD
kubectl apply -f deploy/crds/wildfly.org_wildflyservers_crd.yaml
# Install all resources for the WildFly Operator
kubectl apply -f deploy/operator.yaml
```

## Test Wildfly Operator
* https://www.wildfly.org/news/2020/10/27/wildfly-operator-0.4.1-released/