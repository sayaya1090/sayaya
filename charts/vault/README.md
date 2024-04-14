# 초기화
```
oc exec -ti sayaya-test-vault-0 -- vault operator init -key-shares=1 -key-threshold=1
```

화면에 출력되는 키를 저장

이 시점까지는 모든 서비스가 inactive 상태

# Unseal
```
oc exec -ti sayaya-test-vault-0 -- vault operator unseal
```

초기화시 저장한 키를 입력

0번 pod가 activation 됨

# Join

```
oc exec -ti sayaya-test-vault-1 -- vault operator raft join http://sayaya-test-vault-active:8200
oc exec -ti sayaya-test-vault-2 -- vault operator raft join http://sayaya-test-vault-active:8200
oc exec -ti sayaya-test-vault-1 -- vault operator unseal
oc exec -ti sayaya-test-vault-2 -- vault operator unseal
```
