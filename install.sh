#!/bin/bash

set -e

APP_NAME="cli-stash"
IMAGE_NAME="cli-stash"
VOLUME_NAME="cli_stash_data"
WRAPPER_PATH="/usr/local/bin/cli-stash"

echo "Installing $APP_NAME..."

# -----------------------------
# Проверка Docker
# -----------------------------
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed. Install Docker first."
    exit 1
fi

# -----------------------------
# Проверка Maven
# -----------------------------
if ! command -v mvn &> /dev/null; then
    echo "Maven not found. Install Maven first."
    exit 1
fi

# -----------------------------
# Сборка JAR
# -----------------------------
echo "Building JAR..."
mvn clean package -DskipTests

# Проверка что jar существует
if ! ls target/*.jar 1> /dev/null 2>&1; then
    echo "JAR file not found in target/"
    exit 1
fi

# -----------------------------
# Сборка Docker образа
# -----------------------------
echo "Building Docker image..."
docker build -t $IMAGE_NAME .

# -----------------------------
# Создание volume
# -----------------------------
if ! docker volume inspect $VOLUME_NAME >/dev/null 2>&1; then
    echo "Creating Docker volume..."
    docker volume create $VOLUME_NAME
fi

# -----------------------------
# Создание wrapper
# -----------------------------
echo "Creating wrapper at $WRAPPER_PATH..."

sudo bash -c "cat > $WRAPPER_PATH" <<EOF
#!/bin/bash
docker run -it --rm \\
  -v $VOLUME_NAME:/data \\
  $IMAGE_NAME "\$@"
EOF

sudo chmod +x $WRAPPER_PATH

echo ""
echo "Installation complete!"
echo ""
echo "You can now use:"
echo "  cli-stash"
echo "  cli-stash -add build \"mvn clean install\""
echo ""

