#!/usr/bin/env bash

set -e

kubectl --kubeconfig=$KUBECONFIG delete configmap security-manager-conf -n testci || true
kubectl --kubeconfig=$KUBECONFIG create configmap security-manager-conf -n testci --from-file=../conf/${DEPLOY_ENV}/prodBase.conf
kubectl --kubeconfig=$KUBECONFIG replace -f daf-security-manager.yml -n testci --force
