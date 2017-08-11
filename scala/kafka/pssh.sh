#!/bin/bash

PREFIX=192.168.1
HOSTS="102 103 105 106 107 108 109"

run() {
	local host="$1"; shift

	echo "${host}: $@"

	sshpass -v -p aaaaaa ssh -o StrictHostKeyChecking=no root@${host} "$@" 
}

main() {
	local hosts="$1"; shift
	local host

	case ${hosts} in
	all)
		hosts=${HOSTS}
		;;
	102|103|105|106|107|108|109)
		;;
	*)
		return 1
		;;
	esac

	for host in ${hosts}; do
		run ${PREFIX}.${host} "$@"
	done
}

main "$@"
