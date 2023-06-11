#!/bin/bash
podNames=$(kubectl get pods -l application=ejblb  --no-headers -o custom-columns=":metadata.name")

# kubectl label pod <your-pod-name> version=v1
# use loop,counter
counter=0;

for i in $podNames ;\
do \
  echo $i; \
  echo "---------------------------------"  ;\
  let counter=counter+1 ;
  kubectl label pod $i app=ejblb-v$counter ;
done

kubectl get pods -l application=ejblb --show-labels 


BASE=$(pwd)
kubectl apply -f  "$BASE/ejb-slb-deploy-5-versions.yaml" 

# kubectl apply -f  "$BASE/../cluster-example-client/k8s-yml/ejb-slb-client-prometheus-call-5-versions.yaml" 
# kubectl apply -f  ../cluster-example-client/k8s-yml/ejb-slb-client-prometheus-call-5-versions.yaml