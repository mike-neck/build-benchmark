#!/usr/bin/env bash

set -eu

./gradlew clean
./gradlew :generator:classes :lib:assemble

./gradlew :generator:generate-with-interface-no-logging
./gradlew :with-interface:classes --info

echo '====starting test===='
echo '====1===='

./gradlew :generator:generate-with-interface-logging
./gradlew :with-interface:classes --info

echo '====2===='
./gradlew :generator:generate-with-interface-no-logging
./gradlew :with-interface:classes --info

echo '====3===='
./gradlew :generator:generate-with-interface-logging
./gradlew :with-interface:classes --info

echo '====4===='
./gradlew :generator:generate-with-interface-no-logging
./gradlew :with-interface:classes --info

echo '====5===='
./gradlew :generator:generate-with-interface-logging
./gradlew :with-interface:classes --info

echo '====6===='
./gradlew :generator:generate-with-interface-no-logging
./gradlew :with-interface:classes --info
