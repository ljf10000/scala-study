#!/bin/bash

kafka-topics --zookeeper 192.168.1.105:2181 --describe 2>/dev/null | grep "^Topic:*" | awk '{print $1}' | awk -F ':' '{print $2}'

