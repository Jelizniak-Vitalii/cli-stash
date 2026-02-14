#!/bin/bash

set -e

APP_NAME="cli-stash"
IMAGE_NAME="cli-stash"
VOLUME_NAME="cli_stash_data"
WRAPPER_PATH="/usr/local/bin/cli-stash"

echo "Installing $APP_NAME..."

# Check Docker
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed."
    exit 1
fi

# Build Docker image (with Maven inside)
echo "Building Docker image..."
docker build -t $IMAGE_NAME .

# Create volume
if ! docker volume inspect $VOLUME_NAME >/dev/null 2>&1; then
    echo "Creating Docker volume..."
    docker volume create $VOLUME_NAME
fi

# Create wrapper
echo "Creating wrapper..."

sudo bash -c "cat > $WRAPPER_PATH" <<EOF
#!/bin/bash
docker run -it --rm \\
  -v $VOLUME_NAME:/data \\
  $IMAGE_NAME "\$@"
EOF

sudo chmod +x $WRAPPER_PATH

echo ""
echo "Installation complete!"
echo "Now you can use:"
echo "  cli-stash"
echo ""
