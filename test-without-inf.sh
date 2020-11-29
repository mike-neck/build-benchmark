#!/usr/bin/env bash

set -eu

./gradlew clean
./gradlew :generator:classes :lib:assemble

./gradlew :generator:generate-without-interface-no-logging
./gradlew :without-interface:classes --info

echo '====starting test===='
echo '====1===='

./gradlew :generator:generate-without-interface-logging
./gradlew :without-interface:classes --info

echo '====2===='
./gradlew :generator:generate-without-interface-no-logging
./gradlew :without-interface:classes --info

echo '====3===='
./gradlew :generator:generate-without-interface-logging
./gradlew :without-interface:classes --info

echo '====4===='
./gradlew :generator:generate-without-interface-no-logging
./gradlew :without-interface:classes --info

echo '====5===='
./gradlew :generator:generate-without-interface-logging
./gradlew :without-interface:classes --info

echo '====6===='
./gradlew :generator:generate-without-interface-no-logging
./gradlew :without-interface:classes --info
