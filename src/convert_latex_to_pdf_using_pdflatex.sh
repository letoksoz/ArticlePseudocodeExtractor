#!/bin/bash

# Provide the directory path
directory="/Users/Levent/Desktop/pseudo_code_data/latex_and_pdf_4"

# Check if the specified directory exists
if [ ! -d "$directory" ]; then
  echo "Directory not found."
  exit 1
fi

# List all directories within the specified directory
echo "Directories in '$directory':"
for dir in "$directory"/*; do
  files=$(find "$dir" -type f -name "*.tex")
  for file in $files; do
    # Check if the file is a regular file (not a directory)
    if [ -f "$file" ]; then
      # Print the filename
      echo "::FILE::"
      echo "$file"
      pdflatex -interaction=nonstopmode -output-directory $dir $file
    fi
  done
done
