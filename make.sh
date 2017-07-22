#!/usr/bin/env bash

mvn clean install;cd ..

cd companion-classification-micro;mvn clean install;cd ..
cd companion-dialogmanager-micro;mvn clean install;cd ..
cd companion-nlg-micro;mvn clean install;cd ..
cd companion-nlp-micro;mvn clean install;cd ..
cd companion-sentiment-micro;mvn clean install;cd ..
cd companion-vagrant;mvn clean install;cd ..
cd companion-webapp-gateway;mvn clean install;cd ..
cd companion-wordnet-micro;mvn clean install;cd ..


