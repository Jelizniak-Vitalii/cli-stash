#!/bin/bash

set -e

APP_NAME="cli-stash"
IMAGE_NAME="cli-stash"
VOLUME_NAME="cli_stash_data"
WRAPPER_PATH="/usr/local/bin/cli-stash"

echo "Installing $APP_NAME..."

# Проверка Docker
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed."
    exit 1
fi

# Сборка Docker образа (с Maven внутри)
echo "Building Docker image..."
docker build -t $IMAGE_NAME .

# Создание volume
if ! docker volume inspect $VOLUME_NAME >/dev/null 2>&1; then
    echo "Creating Docker volume..."
    docker volume create $VOLUME_NAME
fi

# Создание wrapper
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
