ssh-keygen -t rsa -m PEM -b 4096 -C sayaya1090@hotmail.com
ssh-keygen -f q.pub -e -m pem > q.pem
oc create secret generic sayaya-token-keypair --from-file=private-key=q --from-file=public-key=q.pem