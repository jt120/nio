#!/bin/bash

keytool -genkey -alias thttpsd -keystore thttpsd.auth -keyalg rsa -dname "CN=Greg Travis, OU=Trivial Server Department, O=Ministry of Servers, L=New York City, S=NY, C=US" -storepass thttpsd -keypass thttpsd
