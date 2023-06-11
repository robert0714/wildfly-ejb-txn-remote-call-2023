#!/bin/bash
 
echo "======================================="
echo "Startup Minikube"

minikube -p sandbox delete && minikube -p sandbox start --kubernetes-version=v1.24.13 \
--cpus=4 \
--memory=8g \
--bootstrapper=kubeadm \
--extra-config=kubelet.authentication-token-webhook=true \
--extra-config=kubelet.authorization-mode=Webhook \
--extra-config=scheduler.bind-address=0.0.0.0 \
--extra-config=controller-manager.bind-address=0.0.0.0 \
--extra-config=kubeadm.pod-network-cidr=192.168.0.0/16



echo "======================================="
echo "Add the loki helm chart"
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

BASE=$(pwd)
 
echo "======================================="
echo "Deploying Prometheus"

cd $BASE/kube-prometheus-v0.12.0 
kubectl apply --server-side -f $BASE/kube-prometheus-v0.12.0/manifests/setup

kubectl wait \
 --for condition=Established \
 --all CustomResourceDefinition \
 --namespace=monitoring

kubectl apply -f $BASE/kube-prometheus-v0.12.0/manifests/
 
echo "======================================="
echo "Deploying Loki"
# helm install loki-stack grafana/loki-stack --values $BASE/loki-stack-values.yaml -n monitoring 

echo "======================================="
echo "Deploying Postrgress Database"
kubectl create ns database
kubectl config set-context --current --namespace database
kubectl apply -f  "$BASE/010_postgreSQL.yaml"
kubectl apply -f  "$BASE/secret.yaml"


kubectl config set-context --current --namespace default

echo "======================================="
echo "Deploying Server Side"
kubectl apply -f  "$BASE/../server/k8s-yml/server.yaml"

echo "======================================="
echo "Deploying Client Side"

kubectl apply -f  "$BASE/../client/k8s-yml/client.yaml" 