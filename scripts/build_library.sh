#!/bin/bash

exit_with_error() {
    echo $@
    exit 1
}

[ $# -gt 0 ] || exit_with_error "No VALID arguments passed"

# Parse arguments
while getopts :s:t: OPT
do
    case $OPT in
        s)
            case "$OPTARG" in
                "")
                    exit_with_error "A valid source Directory must be passed via '-s'"
                    ;;
                *)
                    SOURCE=$OPTARG
                    ([ -d $SOURCE ] && [ -x $SOURCE ]) || exit_with_error "Directory '$SOURCE' is inaccessible"
                    ;;
                esac
                ;;
        t)
            case "$OPTARG" in
                "")
                    exit_with_error "A valid build target must be passed via '-t'"
                    ;;
                "win32-x86-64")
                    TARGET="x86_64-w64-mingw32"
                ;;
                "linux-x86-64")
                    TARGET="x86_64-linux-gnu"
                ;;
                *)
                    exit_with_error "Invalid build target $OPT"
                    ;;
            esac
            ;;
        *)
            exit_with_error "Unsupported option '$OPT'"
            ;;
    esac
done

# Verify script arguments
([[ -n $TARGET ]] && [[ -n $SOURCE ]]) || exit_with_error "Both '-s' and '-t' are mandatory"

# Build
cd $SOURCE && ./autogen.sh && ./configure --host=$TARGET && make clean && make -j 8
