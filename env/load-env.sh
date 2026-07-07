#!/usr/bin/env bash
# load-env.sh
# .local.env の内容を現在のシェルセッションに一時的に設定します。
# セッションを閉じると設定は消えます。
#
# 使い方:
#   source ./env/load-env.sh
#   source ./env/load-env.sh path/to/.local.env
#   . ./env/load-env.sh   # . でも可

ENV_FILE="${1:-.local.env}"

if [ ! -f "$ENV_FILE" ]; then
    echo "Warning: env file not found: $ENV_FILE" >&2
    return 1 2>/dev/null || exit 1
fi

count=0
while IFS= read -r line || [ -n "$line" ]; do
    # 前後の空白除去
    line="${line#"${line%%[![:space:]]*}"}"
    line="${line%"${line##*[![:space:]]}"}"

    # 空行・コメント行はスキップ
    [[ -z "$line" || "$line" == \#* ]] && continue
    [[ "$line" != *=* ]] && continue

    key="${line%%=*}"
    value="${line#*=}"

    # クォート除去
    if [[ "$value" == '"'*'"' || "$value" == "'"*"'" ]]; then
        value="${value:1:${#value}-2}"
    fi

    export "$key=$value"
    echo "  SET $key"
    ((count++)) || true
done < "$ENV_FILE"

echo "Loaded $count variable(s) from $ENV_FILE"
