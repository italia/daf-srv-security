#!/usr/bin/env bash

set -e

dhall-to-yaml --omitNull <<< ./service.dhall > daf-security-manager.yml
echo --- >> daf-security-manager.yml
dhall-to-yaml --omitNull <<< ./deployment.dhall >> daf-security-manager.yml

kubectl --kubeconfig=$KUBECONFIG delete configmap security-manager-conf || true
kubectl --kubeconfig=$KUBECONFIG create configmap security-manager-conf --from-file=../conf/${DEPLOY_ENV}/prodBase.conf
kubectl --kubeconfig=$KUBECONFIG delete -f security-manager-logback.yml  || true
kubectl --kubeconfig=$KUBECONFIG create -f security-manager-logback.yml
kubectl --kubeconfig=$KUBECONFIG delete -f daf-security-manager.yml || true
kubectl --kubeconfig=$KUBECONFIG create -f daf-security-manager.yml
