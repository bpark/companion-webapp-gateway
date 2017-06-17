#!/usr/bin/env bash

docker build -t companion-nlp-micro ../companion-nlp-micro

docker build -t companion-wordnet-micro ../companion-wordnet-micro

docker build -t companion-classification-micro ../companion-classification-micro

docker build -t companion-webapp-gateway .

