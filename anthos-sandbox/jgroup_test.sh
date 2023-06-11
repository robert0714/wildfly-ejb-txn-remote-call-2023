# https://github.com/jgroups-extras/jgroups-kubernetes/blob/main/README.adoc#Demo
# ---------------------------------------------------------------------
# This demo assumes that RBAC is enabled on the Kubernetes cluster.
#
# The serviceaccount, clusterrole and clusterrolebinding provide
# permission for the pods to query K8S api
# ---------------------------------------------------------------------

# Change to a Kubernetes namespace of your preference
export TARGET_NAMESPACE=default

kubectl create serviceaccount jgroups-kubeping-service-account -n $TARGET_NAMESPACE

cat <<EOF | kubectl apply -f -
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: jgroups-kubeping-pod-reader
rules:
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["get", "list"]

---

apiVersion: rbac.authorization.k8s.io/v1beta1
kind: ClusterRoleBinding
metadata:
  name: jgroups-kubeping-api-access
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: jgroups-kubeping-pod-reader
subjects:
- kind: ServiceAccount
  name: jgroups-kubeping-service-account
  namespace: $TARGET_NAMESPACE


EOF