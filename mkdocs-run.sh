#!/bin/bash
cd $(dirname $0)

if [[ ! -d .mkdocs-venv ]]; then
    python3 -m venv .mkdocs-venv
fi

source .mkdocs-venv/bin/activate
pip install -r mkdocs-requirements.txt

CMD=${1:-serve}
shift

mkdocs $CMD "$@"
st=$?
deactivate

exit $st
