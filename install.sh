#!/bin/bash

set -e

APP_NAME="cli-stash"
IMAGE_NAME="clistash/cli-stash:latest"
VOLUME_NAME="clistash_data"
WRAPPER_PATH="/usr/local/bin/cli-stash"

echo "Installing $APP_NAME..."

if ! command -v docker &> /dev/null; then
    echo "Docker is not installed."
    exit 1
fi

echo "Building Docker image..."
docker build -t $IMAGE_NAME .

echo "Creating wrapper..."

sudo bash -c "cat > $WRAPPER_PATH" <<'EOF'
#!/bin/bash

IMAGE_NAME="clistash/cli-stash:latest"
VOLUME_NAME="clistash_data"

if [ $# -eq 0 ]; then
  docker run -it --rm \
    -v $VOLUME_NAME:/data \
    $IMAGE_NAME
  exit 0
fi

OUTPUT=$(docker run -i --rm \
  -v $VOLUME_NAME:/data \
  $IMAGE_NAME "$@")

if [[ "$1" == "-run" ]]; then
  if [ -n "$OUTPUT" ]; then
    eval "$OUTPUT"
  fi
else
  echo "$OUTPUT"
fi
EOF

sudo chmod +x $WRAPPER_PATH

echo ""
echo "Installation complete!"
echo "Image name: $IMAGE_NAME"
echo ""
