다음 커맨드로 리소스 생성 권한 부여
oc label namespace sayaya argocd.argoproj.io/managed-by=openshift-gitops

./helm repo add hashicorp https://helm.releases.hashicorp.com
./helm repo update

./helm fetch hashicorp/vault
tar -xzvf vault-0.25.0.tgz
rm vault-0.25.0.tgz

./helm fetch hashicorp/vault-secrets-operator
tar -xzvf vault-secrets-operator-0.2.0.tgz
rm vault-secrets-operator-0.2.0.tgz

oc project cloudnative-pg
oc get deployment cloudnative-pg-operator -o yaml | oc adm policy scc-subject-review -f -
oc adm policy add-scc-to-user nonroot-v2 -z cloudnative-pg-operator