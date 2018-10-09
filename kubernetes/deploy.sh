#!/usr/bin/env bash

set -e

kubectl --kubeconfig=$KUBECONFIG delete configmap security-manager-conf -n sec || true
kubectl --kubeconfig=$KUBECONFIG create configmap security-manager-conf -n sec --from-file=../conf/${DEPLOY_ENV}/prodBase.conf
kubectl --kubeconfig=$KUBECONFIG replace -f daf-security-manager-${DEPLOY_ENV}.yml -n sec --force
