#!/usr/bin/env sh
set -eu

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
ENV_FILE="${ROOT_DIR}/.env"
EXAMPLE_FILE="${ROOT_DIR}/.env.example"
JWT_DIR="${ROOT_DIR}/infra/jwt"

if [ ! -f "$ENV_FILE" ]; then
  cp "$EXAMPLE_FILE" "$ENV_FILE"
fi

mkdir -p "$JWT_DIR"
openssl ecparam -name prime256v1 -genkey -noout -out "${JWT_DIR}/private.pem"
openssl pkcs8 -topk8 -nocrypt -in "${JWT_DIR}/private.pem" -outform DER -out "${JWT_DIR}/private.der"
openssl ec -in "${JWT_DIR}/private.pem" -pubout -outform DER -out "${JWT_DIR}/public.der" 2>/dev/null

PRIVATE_KEY="$(base64 < "${JWT_DIR}/private.der" | tr -d '\n')"
PUBLIC_KEY="$(base64 < "${JWT_DIR}/public.der" | tr -d '\n')"

set_env_value() {
  name="$1"
  value="$2"
  if grep -q "^${name}=" "$ENV_FILE"; then
    tmp="${ENV_FILE}.tmp"
    awk -v n="$name" -v v="$value" 'BEGIN { prefix=n"=" } index($0, prefix)==1 { print n"="v; next } { print }' "$ENV_FILE" > "$tmp"
    mv "$tmp" "$ENV_FILE"
  else
    printf '%s=%s\n' "$name" "$value" >> "$ENV_FILE"
  fi
}

set_env_value "JWT_PRIVATE_KEY" "$PRIVATE_KEY"
set_env_value "JWT_PUBLIC_KEY" "$PUBLIC_KEY"

printf 'Updated %s with a fresh ES256 JWT key pair.\n' "$ENV_FILE"
