MODE=PROD_MODE

if [ ! -z "$1" ]; then
        MODE=$1;
fi

trap "error_exit" 1 2 3 15 ERR 

function error_exit 
{
	echo "An error has happened: ${1:-"Unknown Error"}" 1>&2
	echo "Stopping ERTP"
	./stop-ertp.sh
	exit 1
}

function run_command
{
	$1
	if [ $? -gt 0 ]; then
		error_exit "Failed to run $1"
	fi
}
