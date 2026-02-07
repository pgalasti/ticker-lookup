#!/bin/bash

# Default installation paths
PREFIX="/usr/local"
BIN_DIR="$PREFIX/bin"
SHARE_DIR="$PREFIX/share/ticker-lookup"
JAR_NAME="ticker-lookup-1.0-SNAPSHOT.jar"
SOURCE_JAR="build/$JAR_NAME"

if [ ! -f "$SOURCE_JAR" ]; then
    echo "Error: $SOURCE_JAR not found. Run 'make' first."
    exit 1
fi

echo "Installing ticker-lookup to $PREFIX..."

# Create directories (may require sudo)
sudo mkdir -p "$BIN_DIR"
sudo mkdir -p "$SHARE_DIR"

# Copy the JAR
sudo cp "$SOURCE_JAR" "$SHARE_DIR/"

# Create the wrapper script
cat <<EOF | sudo tee "$BIN_DIR/tl" > /dev/null
#!/bin/bash
java -jar "$SHARE_DIR/$JAR_NAME" "\$@"
EOF

# Make wrapper executable
sudo chmod +x "$BIN_DIR/tl"

echo "Installation complete. You can now run 'tl' from anywhere."
