#!/usr/bin/env bash

set -eu

echo '====starting test===='
echo "==${GENERATOR_LIMIT}=="
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
