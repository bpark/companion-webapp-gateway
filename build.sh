#!/usr/bin/env bash

docker build -t companion-nlp-micro ../companion-nlp-micro

docker build -t companion-nlg-micro ../companion-nlg-micro

docker build -t companion-wordnet-micro ../companion-wordnet-micro

docker build -t companion-classification-micro ../companion-classification-micro

docker build -t companion-sentiment-micro ../companion-sentiment-micro

docker build -t companion-dialogmanager-micro ../companion-dialogmanager-micro

docker build -t companion-webapp-gateway .

