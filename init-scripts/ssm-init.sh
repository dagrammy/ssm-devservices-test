#!/bin/bash

ENDPOINT_URL="localhost"

if [[ -n "$LOCALSTACK_HOST" ]]; then
    ENDPOINT_URL="$LOCALSTACK_HOST"
fi

echo "-------------------------------------"
echo "########### Creating profile ###########"

aws configure set aws_access_key_id default_access_key --profile=localstack
aws configure set aws_secret_access_key default_secret_key --profile=localstack
aws configure set region "eu-central-1" --profile=localstack

echo "########### Listing profile ###########"
aws configure list --profile=localstack

put_ssm_parameter() {
  local name=$1
  local value=$2

echo "$name"
echo "$value"

aws ssm put-parameter \
    --name "$name" --type "String" --value "$value" \
    --overwrite --region eu-central-1 --output table --profile=localstack --endpoint-url=http://"$ENDPOINT_URL":4566
}

echo "########### add SSM parameter ###########"

# Generate and put 10 SSM parameters
for i in {1..10}; do
  param_name="test_param_$i"
  param_value="value_$i"
  put_ssm_parameter "$param_name" "$param_value"
done

put_ssm_parameter "hello" "Hello from SSM!"

