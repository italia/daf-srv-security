#!/usr/bin/env bash

set -e

kubectl --kubeconfig=$KUBECONFIG delete configmap security-manager-conf || true
kubectl --kubeconfig=$KUBECONFIG create configmap security-manager-conf --from-file=../conf/${DEPLOY_ENV}/prodBase.conf
kubectl --kubeconfig=$KUBECONFIG replace -f security-manager-logback.yml --force
kubectl --kubeconfig=$KUBECONFIG replace -f daf-security-manager.yml --force
